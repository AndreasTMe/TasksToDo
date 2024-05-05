package com.andreast.taskstodo.presentation.components.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.presentation.components.InputField

@Composable
fun InputDialog(
    header: String,
    label: String,
    value: String = "",
    placeholder: String? = null,
    onConfirmRequest: (value: String) -> Unit,
    onDismissRequest: () -> Unit = { },
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { }
) {
    val fieldValue = remember { mutableStateOf(value) }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboard?.show()
    }

    BaseDialog(
        data = fieldValue.value,
        onConfirmRequest = onConfirmRequest,
        onDismissRequest = onDismissRequest,
        onError = onError,
        onFinally = onFinally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = header,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            InputField(
                label = {
                    Text(text = label)
                },
                value = fieldValue.value,
                placeholder = {
                    if (placeholder == null) {
                        return@InputField
                    }

                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                },
                autoFocus = true,
                onValueChange = {
                    fieldValue.value = it
                }
            )
        }
    }
}