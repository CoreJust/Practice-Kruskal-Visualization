package UI

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import graph.Edge
import java.awt.Color

class EdgeWindow() {
    companion object {
        var isOpen by mutableStateOf(false)
        var text by mutableStateOf(TextFieldValue(text = "Test\nTest"))
    }

    fun printColored(text:String, color: Color){
        //что-то тут точно будет
    }
}

@Composable
fun ApplicationScope.EdgeWindowUI() {
    if (EdgeWindow.isOpen) {
        Window(
            onCloseRequest = { EdgeWindow.isOpen = false },
            title = "Edge list",
            visible = true,
            state = rememberWindowState(width = 400.dp)
        ) {
            OutlinedTextField(
                value = EdgeWindow.text,
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