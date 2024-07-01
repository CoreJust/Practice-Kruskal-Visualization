/*
* UI.dialogs.FileDialog.kt
* Contains the implementation of FileDialog function that open a file dialog and returns the chosen file.
* Relies on java.awt.FileDialog since Compose Desktop has no own file explorer yet.
*/

package UI.dialogs

import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog
import java.io.File

fun FileDialog(
    window: ComposeWindow,
    title: String = "Choose a file",
    allowedExtensions: List<String>,
    mode: Int
): File? = FileDialog(window, title, mode).apply {
            isMultipleMode = false
            file = allowedExtensions.joinToString(";") { "*$it" }
            isVisible = true
        }.files.firstOrNull()
