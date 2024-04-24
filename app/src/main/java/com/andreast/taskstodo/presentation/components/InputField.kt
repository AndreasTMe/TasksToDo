package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle

@Composable
fun InputField(
    value: String,
    placeholder: String? = null,
    onValueChange: ((value: String) -> Unit)? = null,
    onFocusChange: ((state: FocusState, valueChanged: Boolean) -> Unit)? = null,
) {
    val (valueChanged, setValueChanged) = remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusable()
            .onFocusChanged {
                if (onFocusChange != null) {
                    onFocusChange(it, valueChanged)
                    setValueChanged(false)
                }
            },
        value = value,
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
                setValueChanged(true)
                onValueChange(it)
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