package com.andreast.taskstodo.presentation.components.headers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TopHeaderWithBackAndMenu(
    title: String,
    onBackClicked: (() -> Unit)? = null,
    menuImageVector: ImageVector,
    onMenuIconClick: () -> Unit,
    dropdownMenu: @Composable () -> Unit,
) {
    GenericTopHeader(
        title = title,
        beforeText = {
            if (onBackClicked == null) {
                return@GenericTopHeader
            }

            Box {
                IconButton(
                    modifier = Modifier
                        .height(40.dp),
                    onClick = {
                        onBackClicked()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "'$title' Screen Back Button"
                    )
                }
            }
        },
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
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "'$title' Screen Menu"
                    )
                }

                dropdownMenu()
            }
        }
    )
}