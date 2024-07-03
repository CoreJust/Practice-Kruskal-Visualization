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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import graph.GraphRenderOptions
import graph.layout.CircleLayout
import graph.layout.LayoutType
import graph.layout.NaiveGridLayout
import graph.layout.SpringLayout

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GraphRenderOptionsDialogUI(onDismiss: () -> Unit) {
    var isLayoutTypeSelectionExpanded by remember { mutableStateOf(false) }
    var selectedLayoutType by remember { mutableStateOf(GraphRenderOptions.layoutType) }

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
                .padding(top = 15.dp, bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isLayoutTypeSelectionExpanded,
            onExpandedChange = { isLayoutTypeSelectionExpanded = it }
        ) {
            TextField(
                value = selectedLayoutType.layoutName,
                readOnly = true,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLayoutTypeSelectionExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = isLayoutTypeSelectionExpanded,
                onDismissRequest = { isLayoutTypeSelectionExpanded = false }
            ) {
                LayoutType.entries.forEach { layoutType ->
                    DropdownMenuItem(
                        onClick = {
                            if (selectedLayoutType != layoutType) {
                                GraphRenderOptions.layout = layoutType.layoutClass
                                GraphView.repositionVertices()
                            }

                            GraphRenderOptions.layoutType = layoutType
                            selectedLayoutType = layoutType
                            isLayoutTypeSelectionExpanded = false
                        }
                    ) {
                        Text(layoutType.layoutName)
                    }
                }
            }
        }

        when (selectedLayoutType) {
            LayoutType.CircleLayout -> CircleLayoutOptionsUI()
            LayoutType.NaiveGridLayout -> NaiveGridLayoutOptionsUI()
            LayoutType.SpringLayout -> SpringLayoutOptionsUI()
        }
    }
}

@Composable
private fun CircleLayoutOptionsUI() {
    val layout = (GraphRenderOptions.layout as CircleLayout)

    SliderOption(
        name = "Circle radius (0 for default): ",
        defaultValue = layout.radius,
        onValueChange = {
            layout.radius = it
            GraphView.repositionVertices()
        },
        valueRange = 0f..1f
    )

    SliderOption(
        name = "Vertex density tolerance: ",
        defaultValue = layout.vertexTolerance,
        onValueChange = {
            layout.vertexTolerance = it
            GraphView.repositionVertices()
        },
        valueRange = 5f..100f
    )

    SliderOption(
        name = "Edge density tolerance: ",
        defaultValue = layout.edgeTolerance,
        onValueChange = {
            layout.edgeTolerance = it
            GraphView.repositionVertices()
        },
        valueRange = 10f..200f
    )

    Row {
        var isChecked by remember { mutableStateOf(layout.separateComponents) }
        Text(text = "Separate components: ")
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                layout.separateComponents = it
                isChecked = it
                GraphView.repositionVertices()
            }
        )
    }
}

@Composable
private fun NaiveGridLayoutOptionsUI() {
    val layout = (GraphRenderOptions.layout as NaiveGridLayout)

    SliderOption(
        name = "Grid step: ",
        defaultValue = layout.gridStep,
        onValueChange = {
            layout.gridStep = it
            GraphView.repositionVertices()
        },
        valueRange = 0.025f..0.5f
    )
}

@Composable
private fun SpringLayoutOptionsUI() {
    val layout = (GraphRenderOptions.layout as SpringLayout)

    SliderOption(
        name = "Iteration factor: ",
        defaultValue = layout.iterationsFactor,
        onValueChange = {
            layout.iterationsFactor = it
            GraphView.repositionVertices()
        },
        valueRange = 0f..1f
    )

    SliderOption(
        name = "Repulsion factor: ",
        defaultValue = layout.repulsionFactor,
        onValueChange = {
            layout.repulsionFactor = it
            GraphView.repositionVertices()
        },
        valueRange = 0f..3f
    )

    SliderOption(
        name = "Spring factor: ",
        defaultValue = layout.springFactor,
        onValueChange = {
            layout.springFactor = it
            GraphView.repositionVertices()
        },
        valueRange = 0f..0.75f
    )

    SliderOption(
        name = "Edge weight power: ",
        defaultValue = layout.edgeWeightPower,
        onValueChange = {
            layout.edgeWeightPower = it
            GraphView.repositionVertices()
        },
        valueRange = 0f..1f
    )

    SliderOption(
        name = "Distance limit: ",
        defaultValue = layout.distanceLimit,
        onValueChange = {
            layout.distanceLimit = it
            GraphView.repositionVertices()
        },
        valueRange = 2f..32f
    )

    SliderOption(
        name = "Delta: ",
        defaultValue = layout.delta,
        onValueChange = {
            layout.delta = it
            GraphView.repositionVertices()
        },
        valueRange = 0.001f..0.1f
    )
}