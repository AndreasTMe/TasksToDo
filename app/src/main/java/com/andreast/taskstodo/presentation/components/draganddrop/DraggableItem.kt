package com.andreast.taskstodo.presentation.components.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun <T> DraggableItem(
    modifier: Modifier = Modifier,
    dropData: T,
    onDragStart: (state: DragState<T>) -> Unit = { },
    onDragEnd: (state: DragState<T>) -> Unit = { },
    onDragCancel: (state: DragState<T>) -> Unit = { },
    onDrag: (state: DragState<T>) -> Unit,
    content: @Composable () -> Unit
) {
    val state = remember { mutableStateOf(DragState<T>()) }
    val position = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                position.value = coordinates.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        state.value = state.value.copy(
                            isDragging = true,
                            position = position.value + offset,
                            composable = content,
                            dropData = dropData
                        )

                        onDragStart(state.value)
                    },
                    onDragEnd = {
                        state.value = state.value.copy(
                            isDragging = false
                        )

                        onDragEnd(state.value)
                    },
                    onDragCancel = {
                        state.value = state.value.copy(
                            isDragging = false
                        )

                        onDragCancel(state.value)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    state.value = state.value.copy(
                        offset = state.value.offset + dragAmount
                    )

                    onDrag(state.value)
                }
            }
    ) {
        content()
    }
}