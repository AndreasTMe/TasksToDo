package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andreast.taskstodo.application.utils.Try

private val MIN_WIDTH = 280.dp
private val MAX_WIDTH = 560.dp

@Composable
fun InputDialog(
    label: String,
    value: String = "",
    placeholder: String? = null,
    onDismissRequest: () -> Unit = { },
    onConfirmRequest: (value: String) -> Unit,
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { }
) {
    val fieldValue = remember { mutableStateOf(value) }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboard?.show()
    }

    Dialog(
        onDismissRequest = {
            Try.resolve(
                onSuccess = onDismissRequest,
                onError = onError,
                onFinally = onFinally
            )
        }
    ) {
        Card(
            modifier = Modifier
                .sizeIn(minWidth = MIN_WIDTH, maxWidth = MAX_WIDTH),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = label,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    InputField(
                        value = fieldValue.value,
                        placeholder = placeholder,
                        autoFocus = true,
                        onValueChange = {
                            fieldValue.value = it
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = {
                            Try.resolve(
                                onSuccess = onDismissRequest,
                                onError = onError,
                                onFinally = onFinally
                            )
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            Try.resolve(
                                result = fieldValue.value,
                                onSuccess = onConfirmRequest,
                                onError = onError,
                                onFinally = onFinally
                            )
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}