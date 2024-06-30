/*
* UI.GraphSaveDialog.kt
* Contains the implementation of GraphSaveDialogUI function that opens and controls a file dialog to choose a file
* to which the graph would be saved.
*/

package UI

import androidx.compose.ui.awt.ComposeWindow
import file.openGraphFile
import graph.RenderableGraph
import java.awt.FileDialog

fun GraphSaveDialogUI(window: ComposeWindow, renderableGraph: RenderableGraph) {
    val allowedExtensions = listOf(".tgf", ".graphml", ".dot", ".gv")
    val openedFile = FileDialog(window, "Choose a file to save graph", allowedExtensions, FileDialog.SAVE) ?: return
    openGraphFile(openedFile).saveGraph(renderableGraph)
}

