package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andreast.taskstodo.application.utils.Try

private val MIN_WIDTH = 280.dp
private val MAX_WIDTH = 560.dp

@Composable
fun ConfirmDialog(
    label: String,
    imageVector: ImageVector? = null,
    onDismissRequest: () -> Unit = { },
    onConfirmRequest: () -> Unit,
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { },
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
                .sizeIn(minWidth = MIN_WIDTH, maxWidth = MAX_WIDTH),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (imageVector != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp),
                            imageVector = imageVector,
                            contentDescription = "Confirmation icon"
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = label,
                    textAlign = TextAlign.Center
                )
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