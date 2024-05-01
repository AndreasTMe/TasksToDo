package com.andreast.taskstodo.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskScreenService
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
    val items: List<TaskListItemDto> = listOf(),
    val selectedItem: TaskListItemDto? = null,
    val screenAction: TaskListScreenAction = TaskListScreenAction.None
)

@HiltViewModel(assistedFactory = TaskListScreenViewModel.Factory::class)
class TaskListScreenViewModel @AssistedInject constructor(
    @Assisted private val taskListId: Long,
    private val taskScreenService: ITaskScreenService
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
        val ids = getParentAndChildrenIds(id)
        taskScreenService.updateTaskListItemsCompletedState(
            ids.map { itemId -> itemId to isCompleted }
        )
        refreshScreen()
    }

    suspend fun handleTaskListUncheckCompleted() {
        val ids = getAllIdsRecursive(_uiState.value.items) { it.isCompleted }
        taskScreenService.updateTaskListItemsCompletedState(
            ids.map { itemId -> itemId to false }
        )
        refreshScreen()
    }

    suspend fun handleTaskListRemoveCompleted() {
        val ids = getAllIdsRecursive(_uiState.value.items) { it.isCompleted }
        taskScreenService.deleteTaskListItemsByIds(ids)
        refreshScreen()
    }

    suspend fun handleTaskListDelete() {
        taskScreenService.deleteTaskListById(_uiState.value.list.id)
        _isDeleted.value = true
        refreshScreen()
    }

    suspend fun handleTaskListItemDelete(id: Long) {
        val ids = getParentAndChildrenIds(id)
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

    private suspend fun handleTaskListItemAdd(title: String) {
        taskScreenService.upsertTaskListItem(
            TaskListItemDto(
                parentId = _uiState.value.selectedItem?.id,
                taskListId = taskListId,
                title = title,
                order = calculateOrder(_uiState.value.selectedItem?.parentId)
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

    private fun getParentAndChildrenIds(parentId: Long): List<Long> {
        val parent = getItemByIdRecursive(parentId, _uiState.value.items) ?: return listOf()

        return listOf(parentId) + getAllIdsRecursive(parent.children)
    }

    private fun calculateOrder(parentId: Long?): Int {
        if (parentId == null) {
            if (_uiState.value.items.isEmpty()) {
                return 0
            }

            return _uiState.value.items.maxBy { it.order }.order + 1
        }

        val parent = getItemByIdRecursive(parentId, _uiState.value.items)
        assert(parent != null) { "Parent not found for id $parentId" }

        return parent!!.children.maxBy { it.order }.order.plus(1)
    }

    private fun getItemByIdRecursive(
        parentId: Long,
        items: List<TaskListItemDto>
    ): TaskListItemDto? {
        val parent = items.find { it.id == parentId }

        if (parent != null) {
            return parent
        }

        for (item in items) {
            return getItemByIdRecursive(parentId, item.children) ?: continue
        }

        return null
    }

    private fun getAllIdsRecursive(
        items: List<TaskListItemDto>,
        predicate: (item: TaskListItemDto) -> Boolean = { false }
    ): List<Long> {
        val itemsList = mutableListOf<Long>()

        for (item in items) {
            if (predicate(item)) {
                itemsList.add(item.id)
            }

            if (item.children.isNotEmpty()) {
                itemsList.addAll(getAllIdsRecursive(item.children, predicate))
            }
        }

        return itemsList
    }
}