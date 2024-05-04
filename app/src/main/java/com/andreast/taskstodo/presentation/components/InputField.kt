package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame

@Composable
fun InputField(
    label: @Composable (() -> Unit)? = null,
    value: String,
    placeholder: @Composable (() -> Unit)? = null,
    autoFocus: Boolean = false,
    onValueChange: (value: String) -> Unit = { },
    onFocusChange: (state: FocusState, valueChanged: Boolean) -> Unit = { _, _ -> }
) {
    val valueState = rememberSaveable(value, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(text = value, selection = TextRange(value.length))
        )
    }
    val valueChanged = remember { mutableStateOf(false) }
    val hasAutoFocus = remember { mutableStateOf(autoFocus) }
    val focusRequester = remember { FocusRequester() }

    if (hasAutoFocus.value) {
        LaunchedEffect(Unit) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusable()
            .focusRequester(focusRequester)
            .onFocusChanged {
                onFocusChange(it, valueChanged.value)
                valueChanged.value = false
            },
        label = label,
        value = valueState.value,
        placeholder = placeholder,
        shape = RoundedCornerShape(16.dp),
        onValueChange = {
            valueState.value = it
            valueChanged.value = true
            onValueChange(it.text)
        },
    )
}