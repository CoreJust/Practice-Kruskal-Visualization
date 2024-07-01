/*
* UI.SingleFieldInputDialog.kt
* Contains the function SingleFieldInputDialogUI that allows to create a dialog with a single input field
* to retrieve some information from the user.
* Also, contains a helper class SingleFieldInputDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import graph.GraphException

class SingleFieldInputDialogHelper() {
    private var isOpen by mutableStateOf(false)
    private var title: String = ""
    private var message: String? = null
    private var label = ""
    private var defaultText = ""
    private var onConfirmation: (String) -> Unit = { }

    // To be used somewhere in a Composable context
    @Composable
    fun show() {
        if (isOpen) {
            SingleFieldInputDialogUI(
                title = title,
                message = message,
                label = label,
                defaultText = defaultText,
                onConfirmation = {
                    try {
                        onConfirmation(it)
                    } catch (e: GraphException) {
                        GraphView.alertDialogHelper.open("Graph exception", e.message ?: "")
                    }

                    isOpen = false
                },
                onDismiss = { isOpen = false }
            )
        }
    }

    // To be used out of Composable context to open up a dialog
    fun open(title: String, message: String? = null, label: String = "Input", defaultText: String = "", onConfirmation: (String) -> Unit) {
        this.title = title
        this.message = message
        this.label = label
        this.defaultText = defaultText
        this.onConfirmation = onConfirmation
        isOpen = true
    }
}


// Creates an input dialog with a given title and message, as well as a single input field.
// Calls the onConfirmation with the text the user entered on confirmation and onDismiss on dismiss
@Composable
fun SingleFieldInputDialogUI(title: String, message: String? = null, label: String = "Input", defaultText: String = "", onConfirmation: (String) -> Unit, onDismiss: () -> Unit) {
    var inputFieldText by remember { mutableStateOf(
        TextFieldValue(text = defaultText, selection = TextRange(0, defaultText.length))
    ) }
    val focusRequester = FocusRequester()

    Dialog(onDismissRequest = { onDismiss() }) {
        Card (
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            border = BorderStroke(width = 3.dp, color = Color.Blue),
            modifier = Modifier
                .padding(8.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        onConfirmation(inputFieldText.text)
                        true
                    } else {
                        false
                    }
                }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                if (!message.isNullOrEmpty()) {
                    Text(message, fontSize = 16.sp)
                }

                OutlinedTextField(
                    value = inputFieldText,
                    label = { Text(label) },
                    singleLine = true,
                    modifier = Modifier.focusRequester(focusRequester),
                    onValueChange = { inputFieldText = it }
                )

                LaunchedEffect(Unit) { // Making the text field active upon creation
                    focusRequester.requestFocus()
                }

                Row {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(7.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onConfirmation(inputFieldText.text)
                        },
                        modifier = Modifier.padding(7.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}