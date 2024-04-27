package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.android.awaitFrame

@Composable
fun InputField(
    value: String,
    placeholder: String? = null,
    autoFocus: Boolean = false,
    onValueChange: ((value: String) -> Unit)? = null,
    onFocusChange: ((state: FocusState, valueChanged: Boolean) -> Unit)? = null,
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

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusable()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (onFocusChange != null) {
                    onFocusChange(it, valueChanged.value)
                    valueChanged.value = false
                }
            },
        value = valueState.value,
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    fontStyle = FontStyle.Italic
                )
            }
        },
        colors = TextFieldDefaults.noBackground(),
        onValueChange = {
            if (onValueChange != null) {
                valueState.value = it
                valueChanged.value = true
                onValueChange(it.text)
            }
        },
    )
}

@Composable
private fun TextFieldDefaults.noBackground(): TextFieldColors {
    return this.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    )
}