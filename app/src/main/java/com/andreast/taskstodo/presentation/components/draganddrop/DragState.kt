package com.andreast.taskstodo.presentation.components.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

enum class DragPhase {
    None,
    Start,
    Move,
    End,
    Cancel,
}

data class DragState<T>(
    val phase: DragPhase = DragPhase.None,
    val position: Offset = Offset.Zero,
    val offset: Offset = Offset.Zero,
    val preview: @Composable () -> Unit = { },
    val dropData: T? = null
) {
    fun isDragging(): Boolean {
        return phase == DragPhase.Start || phase == DragPhase.Move
    }
}
