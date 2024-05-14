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
import kotlinx.coroutines.flow.StateFlow
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
    private val _isDeleted = MutableStateFlow(false)

    val uiState: StateFlow<TaskListScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshScreen()
        }
    }

    private suspend fun refreshScreen() {
        if (_isDeleted.value) {
            _uiState.value = TaskListScreenState()
            return
        }

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
        refreshScreen()
    }

    suspend fun handleTaskListItemCompletedState(id: Long, isCompleted: Boolean) {
        val items = taskFamilyService.getParentAndChildren(
            id,
            _uiState.value.items,
        )

        if (items.isEmpty()) {
            return
        }

        taskScreenService.updateTaskListItemsCompletedState(
            items.map { item -> item.copy(isCompleted = isCompleted) }
        )
        refreshScreen()
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
            items.map { item -> item.copy(isCompleted = false) }
        )
        refreshScreen()
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
        refreshScreen()
    }

    suspend fun handleTaskListDelete() {
        taskScreenService.deleteTaskListById(_uiState.value.list.id)
        _isDeleted.value = true
        refreshScreen()
    }

    suspend fun handleTaskListItemDelete(id: Long) {
        val ids = taskFamilyService.getParentAndChildrenIds(
            id,
            _uiState.value.items,
        )

        if (ids.isEmpty()) {
            return
        }

        taskScreenService.deleteTaskListItemsByIds(ids)
        refreshScreen()
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

        refreshScreen()
    }

    suspend fun handleTaskListReorder(from: Int, to: Int) {
        val reordered = taskOrderingService.reorderTasks(from, to, _uiState.value.items)
        if (reordered.isEmpty()) {
            return
        }

        taskScreenService.updateTaskListItemParentIdAndOrder(reordered)
        refreshScreen()
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
        refreshScreen()
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
    }

    private suspend fun handleTaskListItemEdit(title: String): Boolean {
        assert(_uiState.value.selectedItem != null) { "No item selected" }

        if (title == _uiState.value.selectedItem!!.title) {
            return false
        }

        taskScreenService.updateTaskListItemTitle(_uiState.value.selectedItem!!.id, title)

        return true
    }
}