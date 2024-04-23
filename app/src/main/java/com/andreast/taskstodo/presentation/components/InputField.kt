package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle

@Composable
fun InputField(
    value: String,
    placeholder: String? = null,
    onValueChange: ((String) -> Unit)? = null
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
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