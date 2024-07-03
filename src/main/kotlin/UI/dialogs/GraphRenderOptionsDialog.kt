/*
* UI.dialogs.GraphRenderOptionsDialog.kt
* Contains the function GraphRenderOptionsDialogUI that allows to create a dialog with graph rendering options.
* Also, contains a helper class GraphRenderOptionsDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.GraphView
import UI.utils.CustomDialog
import UI.utils.SliderOption
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import graph.GraphRenderOptions
import graph.layout.CircleLayout

class GraphRenderOptionsDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                GraphRenderOptionsDialogUI { isOpen = false }
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@Composable
private fun GraphRenderOptionsDialogUI(onDismiss: () -> Unit) {
    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true
    ) {
        SliderOption(
            name = "Vertex size: ",
            defaultValue = GraphRenderOptions.VERTEX_SIZE,
            onValueChange = {
                GraphRenderOptions.VERTEX_SIZE = it
                GraphView.onGraphChange(GraphView.renderableGraph)
            },
            valueRange = GraphRenderOptions.VERTEX_SIZE_VALUE_RANGE
        )

        SliderOption(
            name = "Edge width: ",
            defaultValue = GraphRenderOptions.EDGE_WIDTH,
            onValueChange = {
                GraphRenderOptions.EDGE_WIDTH = it
                GraphView.onGraphChange(GraphView.renderableGraph)
            },
            valueRange = GraphRenderOptions.EDGE_WIDTH_VALUE_RANGE
        )

        SliderOption(
            name = "Vertex name size: ",
            defaultValue = GraphRenderOptions.VERTEX_NAME_FONT_SIZE,
            onValueChange = {
                GraphRenderOptions.VERTEX_NAME_FONT_SIZE = it
                GraphView.onGraphChange(GraphView.renderableGraph)
            },
            valueRange = GraphRenderOptions.VERTEX_NAME_FONT_SIZE_VALUE_RANGE
        )

        SliderOption(
            name = "Weight size: ",
            defaultValue = GraphRenderOptions.WEIGHT_FONT_SIZE,
            onValueChange = {
                GraphRenderOptions.WEIGHT_FONT_SIZE = it
                GraphView.onGraphChange(GraphView.renderableGraph)
            },
            valueRange = GraphRenderOptions.WEIGHT_FONT_SIZE_RANGE
        )

        SliderOption(
            name = "Weight position: ",
            defaultValue = GraphRenderOptions.WEIGHT_POSITION,
            onValueChange = {
                GraphRenderOptions.WEIGHT_POSITION = it
                GraphView.onGraphChange(GraphView.renderableGraph)
            },
            valueRange = GraphRenderOptions.WEIGHT_POSITION_VALUE_RANGE
        )

        Text(
            text = "Layout options",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp, bottom = 8.dp)
        )

        SliderOption(
            name = "Circle radius (0 for default): ",
            defaultValue = (GraphRenderOptions.layout as CircleLayout).radius,
            onValueChange = {
                (GraphRenderOptions.layout as CircleLayout).radius = it
                GraphView.repositionVertices()
            },
            valueRange = 0f..1f
        )

        SliderOption(
            name = "Vertex density tolerance: ",
            defaultValue = (GraphRenderOptions.layout as CircleLayout).vertexTolerance,
            onValueChange = {
                (GraphRenderOptions.layout as CircleLayout).vertexTolerance = it
                GraphView.repositionVertices()
            },
            valueRange = 5f..100f
        )

        SliderOption(
            name = "Edge density tolerance: ",
            defaultValue = (GraphRenderOptions.layout as CircleLayout).edgeTolerance,
            onValueChange = {
                (GraphRenderOptions.layout as CircleLayout).edgeTolerance = it
                GraphView.repositionVertices()
            },
            valueRange = 10f..200f
        )

        Row {
            var isChecked by remember { mutableStateOf((GraphRenderOptions.layout as CircleLayout).separateComponents) }
            Text(text = "Separate components: ")
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    (GraphRenderOptions.layout as CircleLayout).separateComponents = it
                    isChecked = it
                    GraphView.repositionVertices()
                }
            )
        }
    }
}