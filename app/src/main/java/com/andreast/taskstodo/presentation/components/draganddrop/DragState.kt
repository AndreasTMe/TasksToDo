package com.andreast.taskstodo.presentation.components.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

data class DragState<T>(
    val isDragging: Boolean = false,
    val position: Offset = Offset.Zero,
    val offset: Offset = Offset.Zero,
    val preview: @Composable () -> Unit = { },
    val dropData: T? = null
)
