package com.andreast.taskstodo.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskFamilyService
import com.andreast.taskstodo.application.services.ITaskOrderingService
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.utils.Level
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TaskListScreenAction {
    None,
    AddTask,
    EditTask
}

data class TaskListScreenState(
    val list: TaskListDto = TaskListDto(),
    val items: List<TaskListItemDto> = emptyList(),
    val selectedItem: TaskListItemDto? = null,
    val screenAction: TaskListScreenAction = TaskListScreenAction.None
)

@HiltViewModel(assistedFactory = TaskListScreenViewModel.Factory::class)
class TaskListScreenViewModel @AssistedInject constructor(
    @Assisted private val taskListId: Long,
    private val taskScreenService: ITaskScreenService,
    private val taskFamilyService: ITaskFamilyService,
    private val taskOrderingService: ITaskOrderingService
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(taskListId: Long): TaskListScreenViewModel
    }

    private val _uiState = MutableStateFlow(TaskListScreenState())
    private val _expandedState = MutableStateFlow(setOf<Long>())
    private val _hiddenState = MutableStateFlow(setOf<Long>())

    val uiState = _uiState.asStateFlow()
    val expandedState = _expandedState.asStateFlow()
    val hiddenState = _hiddenState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshTasks()

            _hiddenState.value = _uiState.value.items
                .filter { it.parentId != null }
                .map { it.id }
                .toSet()
        }
    }

    private fun clearState() {
        _uiState.value = TaskListScreenState()
        _expandedState.value = setOf()
        _hiddenState.value = setOf()
    }

    private suspend fun refreshTasks() {
        _uiState.value = TaskListScreenState(
            list = taskScreenService.getTaskListById(taskListId),
            items = taskScreenService.getTaskListItemsByListId(taskListId)
        )
    }

    fun selectItem(taskListItem: TaskListItemDto?, screenAction: TaskListScreenAction) {
        _uiState.value = _uiState.value.copy(
            selectedItem = taskListItem,
            screenAction = screenAction
        )
    }

    suspend fun handleTaskListTitleChange(title: String) {
        val titleNoWhitespace = title.trim()

        if (titleNoWhitespace == "") {
            return
        }

        taskScreenService.upsertTaskList(_uiState.value.list.copy(title = titleNoWhitespace))
        refreshTasks()
    }

    suspend fun handleTaskListItemCompletedState(id: Long, isCompleted: Boolean) {
        val currentAndChildren = taskFamilyService.getParentAndDescendants(
            id,
            _uiState.value.items,
        )

        if (currentAndChildren.isEmpty()) {
            return
        }

        val current = currentAndChildren[0]
        var siblings = taskFamilyService.getSiblings(current, _uiState.value.items)

        if (siblings.all { it.isCompleted }) {
            taskFamilyService.getAncestors(current.id, _uiState.value.items)
                .let { ancestors ->
                    if (ancestors.isNotEmpty()) {
                        val ancestorsToUpdate = mutableListOf<TaskListItemDto>()

                        for (ancestor in ancestors) {
                            ancestorsToUpdate.add(ancestor.toggle(isCompleted))

                            siblings = taskFamilyService.getSiblings(
                                ancestor,
                                _uiState.value.items
                            )

                            if (siblings.any { !it.isCompleted }) {
                                break
                            }
                        }

                        taskScreenService.updateTaskListItemsCompletedState(
                            ancestorsToUpdate +
                                    currentAndChildren.map { item -> item.toggle(isCompleted) }
                        )
                    } else {
                        taskScreenService.updateTaskListItemsCompletedState(
                            currentAndChildren.map { item -> item.toggle(isCompleted) }
                        )
                    }
                }
        } else {
            taskScreenService.updateTaskListItemsCompletedState(
                currentAndChildren.map { item -> item.toggle(isCompleted) }
            )
        }

        refreshTasks()
    }

    suspend fun handleTaskListUncheckCompleted() {
        val items = _uiState.value.items
            .filter {
                it.isCompleted
            }

        if (items.isEmpty()) {
            return
        }

        taskScreenService.updateTaskListItemsCompletedState(
            items.map { item -> item.toggle(false) }
        )
        refreshTasks()
    }

    suspend fun handleTaskListRemoveCompleted() {
        val ids = _uiState.value.items
            .filter {
                it.isCompleted
            }
            .map {
                it.id
            }

        if (ids.isEmpty()) {
            return
        }

        taskScreenService.deleteTaskListItemsByIds(ids)
        refreshTasks()
    }

    suspend fun handleTaskListDelete() {
        taskScreenService.deleteTaskListById(_uiState.value.list.id)
        clearState()
    }

    suspend fun handleTaskListItemDelete(id: Long) {
        val ids = taskFamilyService.getParentAndDescendantsIds(
            id,
            _uiState.value.items,
        )

        if (ids.isEmpty()) {
            return
        }

        taskScreenService.deleteTaskListItemsByIds(ids)

        _expandedState.value = _expandedState.value.minus(ids.toSet())
        _hiddenState.value = _hiddenState.value.minus(ids.toSet())

        refreshTasks()
    }

    suspend fun handleTaskListScreenAction(title: String) {
        val titleNoWhitespace = title.trim()

        if (titleNoWhitespace == "") {
            return
        }

        if (_uiState.value.screenAction == TaskListScreenAction.EditTask
            && _uiState.value.selectedItem != null
        ) {
            if (_uiState.value.selectedItem!!.title == titleNoWhitespace) {
                return
            }

            handleTaskListItemEdit(titleNoWhitespace)
        } else {
            handleTaskListItemAdd(titleNoWhitespace)
        }

        refreshTasks()
    }

    suspend fun handleTaskListReorder(from: Int, to: Int) {
        val reordered = taskOrderingService.reorderTasks(from, to, _uiState.value.items)
        if (reordered.isEmpty()) {
            return
        }

        taskScreenService.updateTaskListItemParentIdAndOrder(reordered)
        refreshTasks()
    }

    suspend fun handleTaskListItemLevelChange(index: Int, level: Level) {
        if (index !in 1..<_uiState.value.items.size) {
            return
        }

        val reordered = taskOrderingService.reorderTasksAfterLevelChange(
            index,
            level,
            _uiState.value.items
        )
        if (reordered.isEmpty()) {
            return
        }

        taskScreenService.updateTaskListItemParentIdAndOrder(reordered)
        refreshTasks()
    }

    private suspend fun handleTaskListItemAdd(title: String) {
        val order = taskOrderingService.calculateOrderForNewItem(
            _uiState.value.selectedItem?.parentId,
            _uiState.value.items
        )

        if (order < 0) {
            return
        }

        taskScreenService.upsertTaskListItem(
            TaskListItemDto(
                parentId = _uiState.value.selectedItem?.id,
                taskListId = taskListId,
                title = title,
                order = order
            )
        )

        if (_uiState.value.selectedItem != null) {
            handleTaskListItemExpandedState(
                _uiState.value.items.indexOfFirst { it.id == _uiState.value.selectedItem!!.id }
            )
        }
    }

    private suspend fun handleTaskListItemEdit(title: String): Boolean {
        assert(_uiState.value.selectedItem != null) { "No item selected" }

        if (title == _uiState.value.selectedItem!!.title) {
            return false
        }

        taskScreenService.updateTaskListItemTitle(_uiState.value.selectedItem!!.id, title)

        return true
    }

    fun handleTaskListItemExpandedState(index: Int) {
        if (index !in 0..<_uiState.value.items.size) {
            return
        }

        val current = _uiState.value.items[index]

        if (!current.hasChildren) {
            return
        }

        if (_expandedState.value.contains(current.id)) {
            _expandedState.value = _expandedState.value.minus(current.id)
            _hiddenState.value = _hiddenState.value.plus(
                taskFamilyService.getDescendants(current.id, _uiState.value.items).map { it.id }
            )
        } else {
            _expandedState.value = _expandedState.value.plus(current.id)

            taskFamilyService.getDescendants(current.id, _uiState.value.items)
                .let { descendants ->
                    val itemsToShow = mutableSetOf<Long>()

                    for (descendant in descendants) {
                        if (descendant.parentId == current.id) {
                            itemsToShow.add(descendant.id)
                        }

                        if (_expandedState.value.contains(descendant.parentId)
                            && taskFamilyService.getAncestors(
                                descendant.parentId!!,
                                _uiState.value.items
                            ).all { _expandedState.value.contains(it.id) }
                        ) {
                            itemsToShow.add(descendant.id)
                        }
                    }

                    _hiddenState.value = _hiddenState.value.minus(itemsToShow)
                }
        }
    }
}