/*
* Main.kt
* Contains the high-level GUI implementation that relies on more specific components
* from other files. Contains the code for mode switching (between editing and running the
* algorithm).
*/

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import UI.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    var isEditMode by remember { mutableStateOf(true) }

    Window(onCloseRequest = ::exitApplication, title = "Practice") {
        MaterialTheme {
            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item("Load graph", onClick = { GraphLoadDialogUI(window, GraphView::onGraphChange) }, shortcut = KeyShortcut(Key.G, ctrl = true))
                    Item("Save graph", onClick = { GraphSaveDialogUI(window, GraphView.renderableGraph) }, shortcut = KeyShortcut(Key.S, ctrl = true))
                    Item("Load session", onClick = {  }, shortcut = KeyShortcut(Key.O, ctrl = true))
                    Item("New session", onClick = {  }, shortcut = KeyShortcut(Key.N, ctrl = true))
                    Item("Exit", onClick = ::exitApplication, shortcut = KeyShortcut(Key.Escape, ctrl = true))
                }
                Menu ("Mode", mnemonic = 'M') {
                    RadioButtonItem("Edit", onClick = { isEditMode = true }, selected = isEditMode)
                    RadioButtonItem("Algorithm", onClick = { isEditMode = false }, selected = !isEditMode)
                }
                Menu("Options", mnemonic = 'O') {
                    Item("Graph render options", onClick = { })
                    Item("Algorithm options", onClick = { })
                }
                Menu("Info", mnemonic = 'I') {
                    Item("Open guide", onClick = { })
                    Item("Open program info", onClick = { })
                }
            }

            Row (modifier = Modifier.padding(vertical = 10.dp)) {
                ConsoleUI(isEditMode, GraphView::onGraphChange)
                GraphViewUI(isEditMode)
            }
        }
    }
}
