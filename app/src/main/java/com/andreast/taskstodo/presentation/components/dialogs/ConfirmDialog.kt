package com.andreast.taskstodo.presentation.components.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    label: String,
    imageVector: ImageVector? = null,
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit = { },
    onError: (ex: Exception) -> Unit = { },
    onFinally: () -> Unit = { },
) {
    BaseDialog(
        onConfirmRequest = onConfirmRequest,
        onDismissRequest = onDismissRequest,
        onError = onError,
        onFinally = onFinally
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
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}