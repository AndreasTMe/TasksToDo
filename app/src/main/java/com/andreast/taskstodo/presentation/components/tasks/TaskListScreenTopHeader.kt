package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.presentation.components.DropdownDivider
import com.andreast.taskstodo.presentation.components.DropdownMenuSubtitle
import com.andreast.taskstodo.presentation.components.dialogs.ConfirmDialog
import com.andreast.taskstodo.presentation.components.dialogs.InputDialog
import com.andreast.taskstodo.presentation.components.headers.TopHeaderWithMenu

private enum class MenuAction {
    None,
    UncheckCompleted,
    RemoveCompleted,
    EditTitle,
    DeleteList
}

@Composable
fun TaskListScreenTopHeader(
    title: String,
    onUncheckCompleted: () -> Unit,
    onRemoveCompleted: () -> Unit,
    onEditTitle: (title: String) -> Unit,
    onDeleteList: () -> Unit
) {
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val menuActionState = remember { mutableStateOf(MenuAction.None) }
    val isInputDialogOpen = remember { mutableStateOf(false) }
    val isConfirmDialogOpen = remember { mutableStateOf(false) }

    TopHeaderWithMenu(
        title = title,
        menuImageVector = Icons.Filled.MoreVert,
        onMenuIconClick = {
            isDropdownExpanded.value = true
        }
    ) {
        DropdownMenu(
            expanded = isDropdownExpanded.value,
            offset = DpOffset(x = (-16).dp, y = 0.dp),
            onDismissRequest = {
                isDropdownExpanded.value = false
            }
        ) {
            DropdownMenuSubtitle(text = "Items")
            DropdownMenuItem(
                leadingIcon = {
                    Icon(Icons.Filled.Refresh, contentDescription = "Uncheck completed")
                },
                text = {
                    Text(
                        text = "Uncheck completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    menuActionState.value = MenuAction.UncheckCompleted
                    isConfirmDialogOpen.value = true
                    isDropdownExpanded.value = false
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(Icons.Filled.Clear, contentDescription = "Remove completed")
                },
                text = {
                    Text(
                        text = "Remove completed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    menuActionState.value = MenuAction.RemoveCompleted
                    isConfirmDialogOpen.value = true
                    isDropdownExpanded.value = false
                }
            )

            DropdownDivider()

            DropdownMenuSubtitle(text = "List")
            DropdownMenuItem(
                leadingIcon = {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit list title")
                },
                text = {
                    Text(
                        text = "Edit list title",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    menuActionState.value = MenuAction.EditTitle
                    isInputDialogOpen.value = true
                    isDropdownExpanded.value = false
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete list")
                },
                text = {
                    Text(
                        text = "Delete title",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    menuActionState.value = MenuAction.DeleteList
                    isConfirmDialogOpen.value = true
                    isDropdownExpanded.value = false
                }
            )
        }
    }

    if (isInputDialogOpen.value) {
        InputDialog(
            header = getDialogLabel(menuActionState.value),
            label = "Title",
            value = title,
            placeholder = "Enter title...",
            onConfirmRequest = {
                onEditTitle(it)
            },
            onFinally = {
                isInputDialogOpen.value = false
            }
        )
    }

    if (isConfirmDialogOpen.value) {
        ConfirmDialog(
            imageVector = Icons.Rounded.Warning,
            label = getDialogLabel(menuActionState.value),
            onConfirmRequest = {
                when (menuActionState.value) {
                    MenuAction.UncheckCompleted -> onUncheckCompleted()
                    MenuAction.RemoveCompleted -> onRemoveCompleted()
                    MenuAction.DeleteList -> onDeleteList()
                    else -> return@ConfirmDialog
                }
            },
            onFinally = {
                isConfirmDialogOpen.value = false
            }
        )
    }
}

private fun getDialogLabel(menuAction: MenuAction): String {
    return when (menuAction) {
        MenuAction.None -> ""
        MenuAction.UncheckCompleted -> "Are you sure you want to uncheck all tasks?"
        MenuAction.RemoveCompleted -> "Are you sure you want to remove all completed tasks? This action cannot be undone!"
        MenuAction.EditTitle -> "Edit List Title"
        MenuAction.DeleteList -> "Are you sure you want to delete the full list? This action cannot be undone!"
    }
}