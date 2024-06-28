/*
* UI.GraphView.kt
* Contains the implementation of GraphView class and GraphViewUI function.
* The class provides an API to work with the graph view and edit area, whereas the function
* is used to display the GUI itself.
*/

package UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import graph.GraphException
import graph.RenderableGraph
import graph.Vertex
import graph.layout.CircleLayout
import kotlin.math.exp

class GraphView {
    companion object {
        internal var renderableGraph = RenderableGraph()
        internal var graphRenderTrigger by mutableIntStateOf(0) // Used to trigger graph redrawing

        internal var isEditMode = true // Is it the edit mode at all or algorithm mode
        internal var isVertexEditMode by mutableStateOf(true) // Otherwise it is edge editing

        // The current 1D size of the graph view area, allows to convert internal graph coordinates
        // into graph view coordinates and vice versa
        internal var widgetScale = 1f

        // The scale and offset of the graph within graph view area
        internal var transformScale = 1f
        internal var transformOffset = Offset(0f, 0f)

        internal var wasLMBPressed = false // Monitored from outside, set to false whenever mouse button is released
        internal var wasRMBPressed = false // Monitored from outside, set to false whenever mouse button is released

        private var activeVertex: Vertex? = null

        // Calls the subsequent functions to render the graph
        fun render(drawScope: DrawScope, textMeasurer: TextMeasurer) {
            renderableGraph.renderGraph(drawScope, textMeasurer, transformOffset, transformScale, widgetScale)
        }

        // Automatically repositions all the vertices according to the current layout
        fun repositionVertices() {
            renderableGraph.positionVertices(CircleLayout())
            graphRenderTrigger += 1
        }

        fun handleMouseWheelScroll(delta: Float) {
            transformScale *= exp(delta * 0.1f)
            transformScale = Math.clamp(transformScale, 0.025f, 3f)
            graphRenderTrigger += 1
        }

        // Called upon any mouse button press in graph view when in edit mode
        fun handleMousePress(buttons: PointerButtons, originalMousePosition: Offset) {
            if (buttons.isTertiaryPressed) { // If mouse wheel is pressed, return to the original position
                transformScale = 1f
                transformOffset = Offset(0f, 0f)
                graphRenderTrigger += 1
            }

            val position = (originalMousePosition / widgetScale - Offset(0.5f, 0.5f)) / transformScale + Offset(
                0.5f,
                0.5f
            ) - transformOffset
            if (isVertexEditMode) {
                handleMousePressInVertexMode(buttons, position)
            } else {
                handleMousePressInEdgeMode(buttons, position)
            }
        }

        // Callback function to be used externally where graph loading occurs
        fun onGraphChange(renderableGraph: RenderableGraph) {
            this.renderableGraph = renderableGraph
            graphRenderTrigger += 1
        }

        // Handles user mouse press in graph view when in vertex edit mode
        private fun handleMousePressInVertexMode(buttons: PointerButtons, position: Offset) {
            if (buttons.isPrimaryPressed && !wasLMBPressed) { // On left mouse button click a new vertex is being added
                val vertex = renderableGraph.addVertex(renderableGraph.makeUpVertexName(), position)
                setActiveVertex(vertex)
                wasLMBPressed = true // So that we don't insert many vertices at once
            }

            if (buttons.isSecondaryPressed && !wasRMBPressed) { // On right mouse button click a vertex is getting removed
                renderableGraph
                    .getVertexAtPosition(position)
                    ?.also {
                        renderableGraph.removeVertex(it)
                        if (it.id == activeVertex?.id) {
                            activeVertex = null
                        } else {
                            setActiveVertex(null)
                        }
                    }
                graphRenderTrigger += 1
                wasRMBPressed = true
            }
        }

        // Handles user mouse press in graph view when in edge edit mode
        private fun handleMousePressInEdgeMode(buttons: PointerButtons, position: Offset) {
            val clickedVertex = renderableGraph.getVertexAtPosition(position) ?: return
            if (activeVertex == null) { // First click just chooses the first vertex no matter the button
                setActiveVertex(clickedVertex)
            } else if (clickedVertex.id != activeVertex!!.id) {
                if (buttons.isPrimaryPressed && !wasLMBPressed) { // On left mouse button click a new edge is being added
                    renderableGraph.addEdge(activeVertex!!, clickedVertex, 1)
                    setActiveVertex(null)
                    wasLMBPressed = true // So that we don't insert many vertices at once
                }

                if (buttons.isSecondaryPressed && !wasRMBPressed) { // On right mouse button click an edge is getting removed
                    renderableGraph.removeEdge(activeVertex!!, clickedVertex)
                    setActiveVertex(null)
                    wasRMBPressed = true
                }
            }
        }

        // Switches the graph view into vertex editing or into edge editing
        internal fun changeVertexEditModeTo(value: Boolean) {
            if (value != isVertexEditMode) {
                setActiveVertex(null)
                isVertexEditMode = value
            }
        }

        // Sets the currently active vertex and changes its color
        private fun setActiveVertex(vertex: Vertex?) {
            if (vertex != activeVertex) {
                activeVertex?.also { renderableGraph.setVertexColor(it, RenderableGraph.DEFAULT_COLOR) }
                vertex?.also { renderableGraph.setVertexColor(it, RenderableGraph.ACTIVE_COLOR) }
                activeVertex = vertex
                graphRenderTrigger += 1
            }
        }
    }
}

@Composable
fun RowScope.GraphViewUI(isEditMode: Boolean) {
    var alertMessage by remember { mutableStateOf("") }

    GraphView.isEditMode = isEditMode

    Row(
        modifier = Modifier.fillMaxHeight().weight(2f).padding(horizontal = 10.dp)
    ) {
        val textMeasurer = rememberTextMeasurer()
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .weight(10f)
                .clipToBounds()
                .pointerInput(Unit) { // Processing user mouse input within GraphView
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (!GraphView.isEditMode) continue

                            val mousePosition = event.changes.first().position

                            event.changes.forEach { e -> e.consume() }

                            if (event.type == PointerEventType.Release) {
                                GraphView.wasLMBPressed = event.buttons.isPrimaryPressed
                                GraphView.wasRMBPressed = event.buttons.isSecondaryPressed
                            }

                            try {
                                if (event.type == PointerEventType.Press) {
                                    GraphView.handleMousePress(event.buttons, mousePosition)
                                }

                                if (event.type == PointerEventType.Scroll) {
                                    val wheelDelta = event.changes.map { it.scrollDelta.let { it.x + it.y } }.sum()
                                    GraphView.handleMouseWheelScroll(wheelDelta)
                                }
                            } catch (e: GraphException) {
                                alertMessage = e.message ?: ""
                            }
                        }
                    }
                }
        ) {
            if (GraphView.graphRenderTrigger < 0) return@Canvas
            GraphView.widgetScale = minOf(size.height, size.width)
            GraphView.render(this, textMeasurer)
        }

        // The menu to the right of GraphView
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f)
        ) {
            val buttonTextStyle = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp)
            val buttonPadding = 7.dp
            var editModeText by remember { mutableStateOf("V") }

            if (isEditMode) {
                Button(onClick = {
                    if (editModeText == "V") {
                        GraphView.changeVertexEditModeTo(false)
                        editModeText = "E"
                    } else {
                        GraphView.changeVertexEditModeTo(true)
                        editModeText = "V"
                    }
                }) {
                    Text(editModeText, style = buttonTextStyle)
                }
            }

            Button(onClick = {
                GraphView.repositionVertices()
            }, modifier = Modifier.padding(vertical = buttonPadding)) {
                Text("A", style = buttonTextStyle)
            }

            if (isEditMode) {
                Button(onClick = {

                }) {
                    Text("C", style = buttonTextStyle)
                }

                Button(onClick = {

                }, modifier = Modifier.padding(vertical = buttonPadding)) {
                    Text("I", style = buttonTextStyle)
                }
            }

            Button(onClick = {

            }) {
                Text("?", style = buttonTextStyle)
            }
        }
    }

    // The alert in case something went wrong while editing the graph
    if (alertMessage.isNotEmpty()) {
        AlertDialog(
            text = { Text(alertMessage) },
            title = { Text("Graph exception") },
            onDismissRequest = { alertMessage = "" },
            confirmButton = {
                Button({ alertMessage = "" }) {
                    Text("Ok")
                }
            })
    }
}