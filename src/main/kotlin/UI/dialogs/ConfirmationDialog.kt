/*
* UI.dialogs.ConfirmationDialog.kt
* Contains the function ConfirmationDialogUI that allows to create a dialog with a given alert message.
* Also, contains a helper class ConfirmationDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConfirmationDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)
        private var title: String = ""
        private var message: String = ""
        private var onConfirmation: () -> Unit = { }

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                ConfirmationDialogUI(
                    title = title,
                    message = message,
                    onDismiss = { isOpen = false },
                    onConfirmation = { onConfirmation(); isOpen = false }
                )
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open(title: String, message: String, onConfirmation: () -> Unit) {
            this.title = title
            this.message = message
            this.onConfirmation = onConfirmation
            isOpen = true
        }
    }
}

@Composable
private fun ConfirmationDialogUI(title: String, message: String, onDismiss: () -> Unit, onConfirmation: () -> Unit) {
    AlertDialog(
        text = { Text(message) },
        title = { Text(title) },
        onDismissRequest = { onDismiss() },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmation() }) {
                Text("Confirm")
            }
        })
}