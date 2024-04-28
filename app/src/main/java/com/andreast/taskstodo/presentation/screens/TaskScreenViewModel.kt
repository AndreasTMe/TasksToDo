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

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val taskScreenService: ITaskScreenService
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<TaskListDto>())
    val uiState: StateFlow<List<TaskListDto>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            resetScreen()
        }
    }

    suspend fun createTaskList(title: String): Long {
        return taskScreenService.upsertTaskList(TaskListDto(title = title))
    }

    private suspend fun resetScreen() {
        _uiState.value = taskScreenService.getAllTaskLists()
    }
}