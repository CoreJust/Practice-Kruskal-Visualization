/*
* UI.dialogs.ProgramInfoDialog.kt
* Contains the function ProgramInfoDialogUI that allows to create a dialog with information about the program
* Also, contains a helper class ProgramInfoDialogHelper that allows to create that kind of dialog from
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProgramInfoDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                ProgramInfoDialogUI { isOpen = false }
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@Composable
private fun ProgramInfoDialogUI(onDismiss: () -> Unit) {
    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true,
        alignCenterVertically = true,
        width = 800.dp,
        height = 600.dp
    ) {
        Text(
            text = "Kruskal algorithm visualizer",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Blue,
            lineHeight = TextUnit(40f, TextUnitType.Sp)
        )
        Text(
            text = "by",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Anna Efremova",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(brush = Brush.linearGradient(listOf(Color.Yellow, Color.Magenta)))
        )
        Text(
            text = "Denis Lamashovsky",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(brush = Brush.linearGradient(listOf(Color.Magenta, Color.Yellow)))
        )
        Text(
            text = "Ilyin Egor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(brush = Brush.linearGradient(listOf(Color.Blue, Color.Magenta)))
        )
    }
}