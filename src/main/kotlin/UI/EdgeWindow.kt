package UI

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState

/*
*  Usage example:
*    // First, we clear the text
*    EdgeWindow.clear()
*
*    // Then, we println all the edges
*    for (edge in edges) {
*       EdgeWindow.println("some string about edge", text color)
*       if (it is the current edge) {
*           EdgeWindow.setAnchor()
*       }
*    }
*
*    // Finally, render the text
*    EdgeWindow.render()
*/

class EdgeWindow() {
    companion object {
        var isOpen by mutableStateOf(false)

        internal var text = AnnotatedString.Builder("")
        internal var textRenderTrigger by mutableStateOf(0)

        private var textAnchor = 0

        fun println(str: String, color: Color = Color.Black) {
            text.withStyle(SpanStyle(color = color)) {
                append(str + '\n')
            }
        }

        // Sets the text anchor at the current position (so that the currently last line
        // is guaranteed to be within window no matter the number of lines printed afterwards)
        fun setAnchor(anchor: Int = text.length) {
            textAnchor = anchor
        }

        // Outputs the whole text to the edge window
        fun render() {
            textRenderTrigger += 1
        }

        fun clear() {
            text = AnnotatedString.Builder("")
        }

        internal fun getTextFieldValue(): TextFieldValue {
            return TextFieldValue(
                annotatedString = text.toAnnotatedString(),
                selection = TextRange(textAnchor)
            )
        }
    }
}

@Composable
fun EdgeWindowUI() {
    if (EdgeWindow.isOpen) {
        Window(
            onCloseRequest = { EdgeWindow.isOpen = false },
            title = "Edge list",
            visible = true,
            state = rememberWindowState(width = 400.dp)
        ) {
            OutlinedTextField(
                value = if (EdgeWindow.textRenderTrigger < 0) TextFieldValue() else EdgeWindow.getTextFieldValue(),
                singleLine = false,
                readOnly = true,
                onValueChange = { },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}