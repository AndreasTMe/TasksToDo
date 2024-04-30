package com.andreast.taskstodo.presentation.components.headers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericTopHeader(
    title: String,
    beforeText: (@Composable () -> Unit)? = null,
    afterText: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (beforeText != null) {
            beforeText()
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.onPrimary
        )

        if (afterText != null) {
            afterText()
        }
    }
}