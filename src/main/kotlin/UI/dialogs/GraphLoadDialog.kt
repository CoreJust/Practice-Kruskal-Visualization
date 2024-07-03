/*
* UI.dialogs.GraphLoadDialog.kt
* Contains the implementation of GraphLoadDialogUI function that opens and controls a file dialog to choose a file
* from which the graph would be loaded.
*/

package UI.dialogs

import androidx.compose.ui.awt.ComposeWindow
import file.GraphFileException
import file.openGraphFile
import graph.GraphException
import graph.RenderableGraph
import java.awt.FileDialog

fun GraphLoadDialogUI(window: ComposeWindow, onGraphChange: (RenderableGraph) -> Unit) {
    val allowedExtensions = listOf(".tgf", ".gml")
    val openedFile = FileDialog(window, "Choose a graph file", allowedExtensions, FileDialog.LOAD) ?: return

    try {
        val loadedGraph = openGraphFile(openedFile).loadGraph()
        onGraphChange(loadedGraph)
    } catch (e: GraphFileException) {
        AlertDialogHelper.open(
            title = "Graph file exception",
            message = e.message ?: ""
        )
    } catch (e: GraphException) {
        AlertDialogHelper.open(
            title = "Graph exception",
            message = e.message ?: ""
        )
    }
}
