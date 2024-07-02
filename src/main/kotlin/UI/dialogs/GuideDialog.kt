/*
* UI.dialogs.GuideDialog.kt
* Contains the function GuideDialogUI that allows to create a dialog with a guide on program usage.
* Also, contains a helper class GuideDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.utils.CustomDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class GuideDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                GuideDialogUI { isOpen = false }
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@Composable
private fun GuideDialogUI(onDismiss: () -> Unit) {
    val guideContents = buildAnnotatedString {
        append("Empty")
    }

    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true,
        alignCenterVertically = true,
        width = 800.dp,
        height = 600.dp
    ) {
        Text(
            text = guideContents,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}