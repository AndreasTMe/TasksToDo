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
                            phase = DragPhase.Start,
                            position = position.value + offset,
                            preview = content,
                            dropData = dropData
                        )

                        onDrag(state.value)
                    },
                    onDragEnd = {
                        state.value = state.value.copy(
                            phase = DragPhase.End,
                            position = Offset.Zero,
                            offset = Offset.Zero
                        )

                        onDrag(state.value)
                    },
                    onDragCancel = {
                        state.value = state.value.copy(
                            phase = DragPhase.Cancel,
                            position = Offset.Zero,
                            offset = Offset.Zero
                        )

                        onDrag(state.value)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    state.value = state.value.copy(
                        phase = DragPhase.Move,
                        offset = state.value.offset + dragAmount
                    )

                    onDrag(state.value)
                }
            }
    ) {
        content()
    }
}