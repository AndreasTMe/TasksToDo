package com.andreast.taskstodo.presentation.components.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun <T> DropArea(
    modifier: Modifier = Modifier,
    dragState: DragState<T>,
    content: @Composable BoxScope.(data: T?) -> Unit
) {
    val position = dragState.position
    val offset = dragState.offset
    val isCurrentDropTarget = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                coordinates.boundsInWindow().let { rect ->
                    isCurrentDropTarget.value = rect.contains(position + offset)
                }
            }
    ) {
        val data =
            if (isCurrentDropTarget.value && !dragState.isDragging) dragState.dropData else null

        content(data)
    }
}