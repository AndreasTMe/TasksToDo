package com.andreast.taskstodo.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.services.ITaskScreenService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListsScreenState(
    val lists: List<TaskListDto> = emptyList()
)

@HiltViewModel
class TaskListsScreenViewModel @Inject constructor(
    private val taskScreenService: ITaskScreenService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListsScreenState())
    val uiState: StateFlow<TaskListsScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshLists()
        }
    }

    private suspend fun refreshLists() {
        _uiState.value = TaskListsScreenState(
            lists = taskScreenService.getAllTaskLists()
        )
    }

    suspend fun handleTaskListTitleChange(id: Long? = null, title: String): Long {
        return if (id == null) {
            taskScreenService.upsertTaskList(TaskListDto(title = title))
        } else {
            _uiState.value.lists.firstOrNull {
                it.id == id
            }
                .takeIf {
                    it != null
                }
                ?.let {
                    val result = taskScreenService.upsertTaskList(it.copy(title = title))
                    refreshLists()

                    return@let result
                } ?: -1
        }
    }

    suspend fun handleTaskListDelete(id: Long) {
        if (_uiState.value.lists.none { it.id == id }) {
            return
        }

        taskScreenService.deleteTaskListById(id)
        refreshLists()
    }
}