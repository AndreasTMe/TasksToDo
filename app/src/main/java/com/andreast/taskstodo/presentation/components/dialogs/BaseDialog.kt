package com.andreast.taskstodo.presentation.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andreast.taskstodo.application.utils.Try

private val CardMinWidth = 280.dp
private val CardMaxWidth = 560.dp
private val CardShape = RoundedCornerShape(16.dp)

private val CardButtonPadding = 8.dp
private const val CardConfirmText = "Confirm"
private const val CardCancelText = "Cancel"

@Composable
fun BaseDialog(
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit = { },
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { },
    content: @Composable () -> Unit
) {
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
                .sizeIn(minWidth = CardMinWidth, maxWidth = CardMaxWidth),
            shape = CardShape,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()

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
                        modifier = Modifier.padding(CardButtonPadding),
                    ) {
                        Text(
                            text = CardCancelText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    TextButton(
                        onClick = {
                            Try.resolve(
                                onSuccess = onConfirmRequest,
                                onError = onError,
                                onFinally = onFinally
                            )
                        },
                        modifier = Modifier.padding(CardButtonPadding),
                    ) {
                        Text(
                            text = CardConfirmText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T> BaseDialog(
    data: T,
    onConfirmRequest: (data: T) -> Unit,
    onDismissRequest: () -> Unit = { },
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { },
    content: @Composable () -> Unit
) {
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
                .sizeIn(minWidth = CardMinWidth, maxWidth = CardMaxWidth),
            shape = CardShape,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()

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
                        modifier = Modifier.padding(CardButtonPadding),
                    ) {
                        Text(
                            text = CardCancelText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    TextButton(
                        onClick = {
                            Try.resolve(
                                result = data,
                                onSuccess = onConfirmRequest,
                                onError = onError,
                                onFinally = onFinally
                            )
                        },
                        modifier = Modifier.padding(CardButtonPadding),
                    ) {
                        Text(
                            text = CardConfirmText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}