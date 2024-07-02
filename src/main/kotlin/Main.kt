/*
* Main.kt
* Contains the high-level GUI implementation that relies on more specific components
* from other files. Contains the code for mode switching (between editing and running the
* algorithm).
*/

import UI.ConsoleUI
import UI.EdgeWindowUI
import UI.GraphView
import UI.GraphViewUI
import UI.dialogs.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    var isEditMode by remember { mutableStateOf(true) }

    Window(
        title = "Practice",
        onCloseRequest = ::exitApplication,
        icon = painterResource("images/main_icon.png")
    ) {
        MaterialTheme {
            MenuBar {
                Menu("File", mnemonic = 'F') {
                    Item(
                        text = "Load graph",
                        onClick = { GraphLoadDialogUI(window, GraphView::onGraphChange) },
                        shortcut = KeyShortcut(Key.O, ctrl = true)
                    )
                    Item(
                        text = "Save graph",
                        onClick = { GraphSaveDialogUI(window, GraphView.renderableGraph) },
                        shortcut = KeyShortcut(Key.S, ctrl = true)
                    )
                    Item("Exit", onClick = ::exitApplication, shortcut = KeyShortcut(Key.Escape))
                }
                Menu("Mode", mnemonic = 'M') {
                    RadioButtonItem(
                        text = "Edit",
                        onClick = { isEditMode = true },
                        shortcut = KeyShortcut(Key.E, ctrl = true),
                        selected = isEditMode
                    )
                    RadioButtonItem(
                        text = "Algorithm",
                        onClick = { isEditMode = false },
                        shortcut = KeyShortcut(Key.A, ctrl = true),
                        selected = !isEditMode
                    )
                }
                Menu("Options", mnemonic = 'O') {
                    Item("Graph render options", onClick = { GraphRenderOptionsDialogHelper.open() }, shortcut = KeyShortcut(Key.G, ctrl = true))
                    Item("Algorithm options", onClick = { AlgorithmOptionsDialogHelper.open() }, shortcut = KeyShortcut(Key.A, ctrl = true))
                }
                Menu("Info", mnemonic = 'I') {
                    Item("Guide", onClick = { GuideDialogHelper.open() })
                    Item("About program", onClick = { ProgramInfoDialogHelper.open() })
                    Item("Marks", onClick = { ComingSoonDialogHelper.open() })
                }
            }

            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                ConsoleUI(isEditMode, onModeChangeFailure = { isEditMode = !isEditMode })
                GraphViewUI(isEditMode)
            }
        }
    }

    EdgeWindowUI()
}
