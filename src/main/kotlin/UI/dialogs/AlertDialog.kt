/*
* UI.dialogs.AlertDialog.kt
* Contains the function AlertDialogUI that allows to create a dialog with a given alert message.
* Also, this file contains a helper class AlertDialogHelper that allows to create that kind of dialog from
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
import androidx.compose.ui.window.DialogProperties

class AlertDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)
        private var title: String = ""
        private var message: String = ""

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                AlertDialogUI(
                    title = title,
                    message = message,
                    onDismiss = { isOpen = false }
                )
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open(title: String, message: String) {
            this.title = title
            this.message = message
            isOpen = true
        }
    }
}

@Composable
private fun AlertDialogUI(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        text = { Text(message) },
        title = { Text(title) },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button({ onDismiss() }) {
                Text("Ok")
            }
        })
}