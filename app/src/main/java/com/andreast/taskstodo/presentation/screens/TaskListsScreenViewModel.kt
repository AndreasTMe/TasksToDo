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
    val lists: List<TaskListDto> = listOf()
)

@HiltViewModel
class TaskListsScreenViewModel @Inject constructor(
    private val taskScreenService: ITaskScreenService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListsScreenState())
    val uiState: StateFlow<TaskListsScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshScreen()
        }
    }

    suspend fun handleTaskListTitleChange(title: String): Long {
        return taskScreenService.upsertTaskList(TaskListDto(title = title))
    }

    private suspend fun refreshScreen() {
        _uiState.value = TaskListsScreenState(
            lists = taskScreenService.getAllTaskLists()
        )
    }
}