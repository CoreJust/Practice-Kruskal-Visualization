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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

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
    val title = buildAnnotatedString {
        append("Guide")
    }

    val guideContents = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)){
            append("Top Menu\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("File -> Load Graph or Ctrl + O ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to load a graph in one of the following formats: *.tgf or *.gml.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("File -> Save Graph or Ctrl + S ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to save the graph in one of the following formats: *.tgf or *.gml.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("File -> Exit or Escape ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to terminate the program.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Mode -> Edit or Shift + E ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to switch to graph editing mode.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Mode -> Algorithm or Shift + A ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to switch to the execution mode of Kruskal's algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Options -> Graph render options or Ctrl + G ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to open the graph rendering settings.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Options -> Algorithm options or Ctrl + A ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to open settings for displaying information about the execution of the algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Info -> Guide or Ctrl + H ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to open the guide.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Info -> About program or Ctrl + P ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to open the guide.\n\n")
        }

        withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)){
            append("Right menu in edit mode\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("A ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to arrange vertices according to one of three patterns (you can select a pattern in the graph display settings).\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("C ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to clear the field from the current graph.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("I ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to insert a graph of a special structure.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("? ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to learn more about the characteristics of the current graph.\n\n")
        }

        withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)){
            append("Right menu in algorithm mode\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("A ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to arrange vertices according to one of three patterns (you can select a pattern in the graph display settings).\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("? ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to learn more about the characteristics of the current graph.\n\n")
        }

        withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)){
            append("Bottom menu in algorithm mode\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("◀ ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to go to the beginning of the algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("< ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to take 1 step back in the algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("> ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to take 1 step forward in the algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("▶ ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to go to the end of the algorithm.\n")
        }

        withStyle(SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)){
            append("Run/Pause ")
        }
        withStyle(SpanStyle(fontSize = 12.sp)){
            append("allows you to switch between automatic and manual display mode.")
        }
    }

    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true,
        width = 800.dp,
        height = 600.dp
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize(),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(brush = Brush.linearGradient(listOf(Color.Cyan, Color.Magenta)))
        )

        Text(
            text = guideContents,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxSize()
        )
    }
}