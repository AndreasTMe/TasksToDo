package com.andreast.taskstodo.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuSubtitle(
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = TextUnit(12f, TextUnitType.Sp),
            fontStyle = FontStyle.Italic,
            color = color
        )
    }
}