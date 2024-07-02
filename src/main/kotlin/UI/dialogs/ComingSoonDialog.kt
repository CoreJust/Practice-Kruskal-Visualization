/*
* UI.dialogs.ComingSoonDialog.kt
* Contains the function ComingSoonDialogUI that allows to create a dialog with "Coming soon" message
* Also, contains a helper class ComingSoonDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.utils.CustomDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ComingSoonDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                ComingSoonDialogUI { isOpen = false }
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@Composable
private fun ComingSoonDialogUI(onDismiss: () -> Unit) {
    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true,
        alignCenterVertically = true,
        width = 440.dp,
        height = 240.dp
    ) {
        Text(
            text = "Coming soon",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(brush = Brush.linearGradient(listOf(Color.Magenta, Color.Blue, Color.Magenta)))
        )
    }
}