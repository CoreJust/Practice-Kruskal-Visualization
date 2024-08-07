/*
* UI.dialogs.GraphInfoDialog.kt
* Contains the function GraphInfoDialogUI that allows to create a dialog that displays some information on
* the current graph.
* Also, this file contains a helper class GraphInfoDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.GraphView
import UI.utils.CustomDialog
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class GraphInfoDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                GraphInfoDialogUI { isOpen = false }
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@Composable
private fun GraphInfoDialogUI(onDismiss: () -> Unit) {
    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true
    ) {
        val edgesList = GraphView.renderableGraph.edges

        Text(text = "Graph info", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        Spacer(Modifier.height(10.dp))
        Text(text = "Vertices: ${GraphView.renderableGraph.vertices.size}", fontSize = 16.sp)
        Text(text = "Edges: ${edgesList.size}", fontSize = 16.sp)
        Text(text = "Components: ${GraphView.renderableGraph.splitIntoComponents().size}", fontSize = 16.sp)
        Text(text = "Overall graph weight: ${edgesList.sumOf { it.weight }}", fontSize = 16.sp)
    }
}