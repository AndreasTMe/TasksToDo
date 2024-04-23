package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ClickableArea(
    onAreaClicked: () -> Unit
) {
    IconButton(
        modifier = Modifier.fillMaxSize(),
        onClick = {
            onAreaClicked()
        }
    ) {
        Icon(
            modifier = Modifier
                .size(100.dp),
            imageVector = Icons.Filled.AddCircle,
            contentDescription = "Add first item",
            tint = Color.LightGray,
        )
    }
}