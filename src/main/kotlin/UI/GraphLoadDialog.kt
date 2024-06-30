/*
* UI.GraphLoadDialog.kt
* Contains the implementation of GraphLoadDialogUI function that opens and controls a file dialog to choose a file
* from which the graph would be loaded.
*/

package UI

import androidx.compose.ui.awt.ComposeWindow
import file.openGraphFile
import graph.RenderableGraph
import graph.layout.CircleLayout
import java.awt.FileDialog

fun GraphLoadDialogUI(window: ComposeWindow, onGraphChange: (RenderableGraph) -> Unit) {
    val allowedExtensions = listOf(".tgf", ".gml")
    val openedFile = FileDialog(window, "Choose a graph file", allowedExtensions, FileDialog.LOAD) ?: return
    val loadedGraph = openGraphFile(openedFile).loadGraph().also { it.positionVertices(CircleLayout()) }
    onGraphChange(loadedGraph)
}
