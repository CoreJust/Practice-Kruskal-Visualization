/*
* UI.GraphView.kt
* Contains the implementation of GraphView class and GraphViewUI function.
* The class provides an API to work with the graph view and edit area, whereas the function
* is used to display the GUI itself.
*/

package UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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

        internal val alertDialogHelper = AlertDialogHelper()
        internal val vertexNameInputDialogHelper = SingleFieldInputDialogHelper()
        internal val edgeWeightInputDialogHelper = SingleFieldInputDialogHelper()

        internal var isEditMode = true // Is it the edit mode at all or algorithm mode
        internal var isRenameMode = false // When we are in the edit mode, we can press shift and rename vertices / edges

        // The current 1D size of the graph view area, allows to convert internal graph coordinates
        // into graph view coordinates and vice versa
        internal var widgetScale = 1f

        // The scale and offset of the graph within graph view area
        private var transformScale = 1f
        private var transformOffset = Offset(0f, 0f)
        private var oldTransformOffset = Offset(0f, 0f)

        // The position of the pointer when the corresponding mouse button was pressed
        private var LMBClickPosition: Offset? = null
        private var RMBClickPosition: Offset? = null

        private var activeVertex: Vertex? = null // First chosen vertex in edge mode
        private var holdVertex: Vertex? = null // Used when moving a vertex

        // Calls the subsequent functions to render the graph
        fun render(drawScope: DrawScope, textMeasurer: TextMeasurer) {
            renderableGraph.renderGraph(drawScope, textMeasurer, transformOffset, transformScale, widgetScale)
        }

        // Automatically repositions all the vertices according to the current layout
        fun repositionVertices() {
            renderableGraph.positionVertices(CircleLayout())
            rerenderGraph()
        }

        // Callback function to be used externally where graph loading occurs
        fun onGraphChange(renderableGraph: RenderableGraph) {
            this.renderableGraph = renderableGraph
            rerenderGraph()
        }

        // Called upon scrolling mouse wheel
        internal fun handleMouseWheelScroll(delta: Float) {
            transformScale *= exp(delta * 0.1f)
            transformScale = Math.clamp(transformScale, 0.025f, 3f)
            rerenderGraph()
        }

        // Called upon any mouse button press in graph view
        internal fun handleMousePress(buttons: PointerButtons, originalMousePosition: Offset) {
            val position = (originalMousePosition / widgetScale - Offset(0.5f, 0.5f)) / transformScale + Offset(
                0.5f,
                0.5f
            ) - transformOffset
            val clickedVertex = renderableGraph.getVertexAtPosition(position)

            if (buttons.isPrimaryPressed && LMBClickPosition == null) {
                LMBClickPosition = position
                oldTransformOffset = transformOffset

                // On left mouse button click
                if (clickedVertex != null) { // Move the vertex if it is to be dragged
                    holdVertex = clickedVertex
                }
            }

            if (buttons.isSecondaryPressed && RMBClickPosition == null) {
                RMBClickPosition = position
            }
        }

        // Called upon mouse being released
        internal fun handleMouseRelease(buttons: PointerButtons, originalMousePosition: Offset) {
            fun isMouseButtonClickApplicable(clickPosition: Offset?, releasePosition: Offset): Boolean { // Checks if it can be considered a click rather than drag
                val transformDistance = (transformOffset - oldTransformOffset).getDistance()
                val mouseDistance = ((clickPosition ?: return false) - releasePosition).getDistance()
                return mouseDistance + transformDistance < 0.01f
            }

            val position = (originalMousePosition / widgetScale - Offset(0.5f, 0.5f)) / transformScale + Offset(
                0.5f,
                0.5f
            ) - transformOffset

            val clickedVertex = renderableGraph.getVertexAtPosition(position)
            val isLMBReleaseApplicable = !buttons.isPrimaryPressed && isMouseButtonClickApplicable(LMBClickPosition, position)
            val isRMBReleaseApplicable = !buttons.isSecondaryPressed && isMouseButtonClickApplicable(RMBClickPosition, position)

            if (isEditMode) {
                if (isRMBReleaseApplicable && clickedVertex != null) { // On right mouse button click a vertex is getting removed
                    if (activeVertex == null || clickedVertex.id == activeVertex?.id) {
                        renderableGraph.removeVertex(clickedVertex) // Remove a vertex upon RMB click
                        if (clickedVertex.id == activeVertex?.id) {
                            activeVertex = null // This vertex was just deleted and we cannot just call setActiveVertex because it is already non-existent
                            rerenderGraph()
                        } else {
                            setActiveVertex(null) // Reset the active vertex when deleting another vertex
                        }
                    } else {
                        setActiveVertex(null) // Reset active vertex when clicking RMB on an empty place
                    }
                }

                if (clickedVertex == null) { // If there is no vertex in the place of the click, a new vertex is being added
                    if (isLMBReleaseApplicable) {
                        handleVertexCreation(position)
                        rerenderGraph()
                    }
                } else {
                    if (activeVertex == null) {
                        if (isLMBReleaseApplicable) { // First click with LMB just chooses the first vertex
                            if (isRenameMode) { // Or we rename the vertex if the shift is applied
                                handleVertexRenaming(clickedVertex)
                            } else {
                                setActiveVertex(clickedVertex)
                            }
                        }
                    } else if (clickedVertex.id != activeVertex!!.id) {
                        if (isLMBReleaseApplicable) { // On left mouse button click a new edge is being added
                            if (isRenameMode) {
                                handleEdgeRenaming(activeVertex!!, clickedVertex)
                            } else {
                                handleEdgeCreation(activeVertex!!, clickedVertex)
                            }

                            setActiveVertex(null)
                        }

                        if (isRMBReleaseApplicable) { // On right mouse button click an edge is getting removed
                            renderableGraph.removeEdge(activeVertex!!, clickedVertex)
                            setActiveVertex(null)
                        }
                    }
                }
            }

            // Resetting the mouse button states upon button release
            if (!buttons.isPrimaryPressed) LMBClickPosition = null
            if (!buttons.isSecondaryPressed) RMBClickPosition = null
            if (LMBClickPosition == null) { // Release dragged vertex on left mouse button release
                holdVertex = null
            }
        }

        // Called upon mouse being moved
        internal fun handleMouseMove(mouseOffset: Offset) {
            val offset = mouseOffset / (widgetScale * transformScale)

            if (holdVertex != null) {
                holdVertex!!.position = holdVertex!!.position?.plus(offset) // Move the vertex along the mouse pointer
            } else if (LMBClickPosition != null) {
                transformOffset += offset // Move the view
            }

            rerenderGraph()
        }

        // Handles the case when a new vertex is being created by LMB click
        private fun handleVertexCreation(position: Offset) {
            vertexNameInputDialogHelper.open(
                title = "Enter vertex name",
                label = "Vertex name",
                defaultText = renderableGraph.makeUpVertexName(),
                onConfirmation = {
                    renderableGraph.addVertex(it, position)
                    rerenderGraph()
                }
            )
        }

        // Handles the case when a new edge is being created by LMB click
        private fun handleEdgeCreation(from: Vertex, to: Vertex) {
            edgeWeightInputDialogHelper.open(
                title = "Enter edge weight",
                label = "Edge weight",
                defaultText = "1",
                onConfirmation = {
                    val newWeight = it.toIntOrNull()
                    if (newWeight == null) {
                        alertDialogHelper.open(
                            title = "Error",
                            message = "You entered \"$it\", but a number was expected"
                        )
                    } else {
                        renderableGraph.addEdge(from, to, newWeight)
                        rerenderGraph()
                    }
                }
            )
        }

        // Handles the case of a vertex being renamed
        private fun handleVertexRenaming(vertex: Vertex) {
            vertexNameInputDialogHelper.open(
                title = "Enter new vertex name",
                label = "Vertex name",
                onConfirmation = {
                    renderableGraph.renameVertex(vertex, it)
                    rerenderGraph()
                }
            )
        }

        // Handles the case of an edge being renamed
        private fun handleEdgeRenaming(from: Vertex, to: Vertex) {
            edgeWeightInputDialogHelper.open(
                title = "Enter new edge weight",
                label = "Edge weight",
                onConfirmation = {
                    val newWeight = it.toIntOrNull()
                    if (newWeight == null) {
                        alertDialogHelper.open(
                            title = "Error",
                            message = "You entered \"$it\", but a number was expected"
                        )
                    } else {
                        renderableGraph.setEdgeWeight(from, to, newWeight)
                        rerenderGraph()
                    }
                }
            )
        }

        // Sets the currently active vertex and changes its color
        private fun setActiveVertex(vertex: Vertex?) {
            if (vertex != activeVertex) {
                activeVertex?.also { renderableGraph.setVertexColor(it, RenderableGraph.DEFAULT_COLOR) }
                vertex?.also { renderableGraph.setVertexColor(it, RenderableGraph.ACTIVE_COLOR) }
                activeVertex = vertex
                rerenderGraph()
            }
        }

        // Called when the graph is required to be re-rendered
        private fun rerenderGraph() {
            graphRenderTrigger += 1
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
                            val mousePosition = event.changes.first().position
                            val mouseOffset = event.changes.map { it.positionChange() }.reduce { acc, offset -> acc + offset }
                            val wheelDelta = event.changes.map { it.scrollDelta.let { it.x + it.y } }.sum()

                            event.changes.forEach { e -> e.consume() }
                            GraphView.isRenameMode = event.keyboardModifiers.isShiftPressed

                            try {
                                when (event.type) {
                                    PointerEventType.Press -> GraphView.handleMousePress(event.buttons, mousePosition)
                                    PointerEventType.Release -> GraphView.handleMouseRelease(event.buttons, mousePosition)
                                    PointerEventType.Scroll -> GraphView.handleMouseWheelScroll(wheelDelta)
                                    PointerEventType.Move -> GraphView.handleMouseMove(mouseOffset)
                                }
                            } catch (e: GraphException) {
                                alertMessage = e.message ?: ""
                            }
                        }
                    }
                }
        ) {
            if (GraphView.graphRenderTrigger < 0) { // So that the graph gets redrawn on its change
                return@Canvas
            }

            GraphView.widgetScale = minOf(size.height, size.width)
            GraphView.render(this, textMeasurer)
        }

        // The menu to the right of GraphView
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f)
        ) {
            val buttonTextStyle = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp)
            val buttonPadding = 7.dp

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
        AlertDialogUI(
            title = "Graph exception",
            message = alertMessage,
            onDismiss = { alertMessage = "" }
        )
    }

    // Calling these functions here allow the helpers to be shown
    GraphView.alertDialogHelper.show()
    GraphView.vertexNameInputDialogHelper.show()
    GraphView.edgeWeightInputDialogHelper.show()
}