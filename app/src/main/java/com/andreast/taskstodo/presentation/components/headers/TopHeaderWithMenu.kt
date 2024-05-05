package com.andreast.taskstodo.presentation.components.headers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TopHeaderWithMenu(
    title: String,
    menuImageVector: ImageVector,
    onMenuIconClick: () -> Unit,
    dropdownMenu: @Composable () -> Unit,
) {
    GenericTopHeader(
        title = title,
        afterText = {
            Box {
                IconButton(
                    modifier = Modifier
                        .height(40.dp),
                    onClick = {
                        onMenuIconClick()
                    }
                ) {
                    Icon(
                        imageVector = menuImageVector,
                        contentDescription = "'$title' Screen Menu"
                    )
                }

                dropdownMenu()
            }
        }
    )
}