/*
* graph.RenderableGraph.kt
* Contains the implementation of RenderableGraph class that contains the actual logic of graph visualizing.
* It contains all the data on vertex positions, names, colors and so on.
*/

package graph

import algorithm.AlgorithmOptions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class RenderableGraph : Graph() {
    companion object {
        val DEFAULT_VERTEX_COLOR = Color.Blue
        val DEFAULT_EDGE_COLOR = Color.Black
        val ACTIVE_COLOR = Color.Red
        val TEXT_COLOR = Color.Black
        val WEIGHT_BACKGROUND_COLOR = Color.Gray.compositeOver(Color.White)
    }

    private var verticesByName: MutableMap<String, Vertex> = mutableMapOf()

    // A property that allows to operate on graph coloring
    var graphColoring: GraphColoring
        get() = GraphColoring().loadFromGraph(this)
        set(coloring) = coloring.applyToGraph(this)

    // Internal auxiliary class for graph rendering
    private class GraphRenderer(private val drawScope: DrawScope, private val textMeasurer: TextMeasurer, private val widgetScale: Float) {
        // Renders a single graph vertex
        fun renderVertex(vertex: Vertex) {
            if (vertex.position == null) return

            val vertexTextStyle = TextStyle.Default.copy(
                color = TEXT_COLOR,
                fontSize = GraphRenderOptions.VERTEX_NAME_FONT_SIZE.sp,
                fontWeight = FontWeight.Bold
            )

            // Vertex itself
            drawScope.drawCircle(
                color = vertex.color,
                radius = GraphRenderOptions.VERTEX_SIZE,
                center = vertex.position!!
            )

            // And its name
            val nameToDraw = vertex.name.substringBefore('$') // We drop the part of the name that comes after the first $
            if (nameToDraw.isNotEmpty()) { // Ignore vertex names that begin with $
                renderCenteredText(nameToDraw, vertex.position!!, vertexTextStyle)
            }
        }

        // Renders a single graph edge
        fun renderEdge(from: Vertex, to: Vertex, weight: String, color: Color) {
            if (from.id >= to.id || from.position == null || to.position == null) return

            val weightTextStyle = TextStyle.Default.copy(
                color = TEXT_COLOR,
                fontSize = GraphRenderOptions.WEIGHT_FONT_SIZE.sp
            )

            val toPosition = to.position!!
            val fromPosition = from.position!!
            drawScope.drawLine( // The edge itself
                color = color,
                start = fromPosition,
                end = toPosition,
                strokeWidth = GraphRenderOptions.EDGE_WIDTH
            )

            // Edge weight
            val weightPosition = lerp(fromPosition, toPosition, GraphRenderOptions.WEIGHT_POSITION)
            drawScope.drawCircle( // The background for the weight
                color = WEIGHT_BACKGROUND_COLOR,
                radius = GraphRenderOptions.VERTEX_SIZE * 0.65f,
                center = weightPosition
            )

            renderCenteredText(weight, weightPosition, weightTextStyle)
        }

        // Draws the text with its center at the given position
        fun renderCenteredText(text: String, position: Offset, style: TextStyle) {
            val vertexTextSize = textMeasurer.measure(
                text,
                style = style.copy(fontSize = style.fontSize * widgetScale)
            )
                .size.let {
                    Offset(
                        it.width.toFloat() / widgetScale,
                        -it.height.toFloat() / (2 * widgetScale)
                    )
                }

            drawScope.drawText( // Vertex name
                textMeasurer,
                text,
                topLeft = position - vertexTextSize * 0.5f,
                style = style
            )
        }
    }

    // Does the actual rendering,
    // Must be called from Canvas context.
    fun renderGraph(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        offset: Offset,
        scale: Float,
        widgetScale: Float
    ) {
        drawScope.withTransform(transformBlock = {
            scale(widgetScale, widgetScale, Offset(0f, 0f))
            translate(left = 0.5f, top = 0.5f)
            scale(scale, scale, Offset(0f, 0f))
            translate(left = offset.x - 0.5f, top = offset.y - 0.5f)
        }) {
            val renderer = GraphRenderer(drawScope, textMeasurer, widgetScale)

            // First we render the edges: gray, then normal, then other colors
            val edges = edges
            for (colorType in 0..2) {
                for (edge in edges) {
                    val edgeColorType = when(edge.color) {
                        AlgorithmOptions.theme.skippedEdge -> 0
                        DEFAULT_EDGE_COLOR -> 1
                        else -> 2
                    }

                    if (edgeColorType == colorType) {
                        renderer.renderEdge(edge.first, edge.second, edge.weight.toString(), edge.color)
                    }
                }
            }

            // Then, the vertices are rendered
            for (vertex in vertices) {
                renderer.renderVertex(vertex)
            }
        }
    }

    // Assign positions to all the vertices
    fun positionVertices() {
        GraphRenderOptions.layout.positionVertices(this)
    }

    // Changes all the colors to the default ones
    fun resetColors() {
        for (vertex in vertices) {
            vertex.color = DEFAULT_VERTEX_COLOR
            for (outcomingEdge in vertex.outcomingEdges) {
                outcomingEdge.color = DEFAULT_EDGE_COLOR
            }
        }
    }

    // Changes the color of all the vertices with oldColor to newColor
    // Returns the number of vertices that change the color
    fun replaceVertexColor(oldColor: Color, newColor: Color): Int {
        if (oldColor == newColor) {
            return 0
        }

        var colorChangeCounter = 0
        vertices.forEach {
            if (it.color == oldColor) {
                colorChangeCounter += 1
                it.color = newColor
            }
        }

        return colorChangeCounter
    }

    // Allows to give a vertex a color, if color is null than the default color is applied
    fun setVertexColor(vertex: Vertex, color: Color?) {
        if (!vertices.contains(vertex)) {
            throw NoSuchVertexException(vertex.name)
        }

        vertex.color = color ?: DEFAULT_VERTEX_COLOR
    }

    // Allows to give an edge a color, if color is null than the default color is applied
    fun setEdgeColor(edge: Edge, color: Color?) {
        setEdgeColor(edge.first, edge.second, color)
    }

    // Allows to give an edge a color, if color is null than the default color is applied
    fun setEdgeColor(from: Vertex, to: Vertex, color: Color?) {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        setOneSideEdgeColor(from, to, color ?: DEFAULT_EDGE_COLOR)
        setOneSideEdgeColor(to, from, color ?: DEFAULT_EDGE_COLOR)
    }

    // Allows to give a vertex a new name
    fun renameVertex(vertex: String, newName: String) {
        renameVertex(
            getVertexByName(vertex) ?: throw NoSuchVertexException(vertex),
            newName
        )
    }

    // Allows to give a vertex a new name
    fun renameVertex(vertex: Vertex, newName: String) {
        if (!vertices.contains(vertex)) {
            throw NoSuchVertexException(vertex.name)
        }

        if (vertex.name == newName) {
            return // Nothing to be done
        }

        if (verticesByName.containsKey(newName)) {
            throw VertexAlreadyExistsException(newName)
        }

        verticesByName.remove(vertex.name)
        verticesByName[newName] = vertex
        vertex.name = newName
    }

    // Allows to give an edge a new weight
    fun setEdgeWeight(from: String, to: String, newWeight: Int) {
        setEdgeWeight(
            getVertexByName(from) ?: throw NoSuchVertexException(from),
            getVertexByName(to) ?: throw NoSuchVertexException(to),
            newWeight
        )
    }

    // Allows to give an edge a new weight
    fun setEdgeWeight(edge: Edge, newWeight: Int) {
        setEdgeWeight(edge.first, edge.second, newWeight)
    }

    // Allows to give an edge a new weight
    fun setEdgeWeight(from: Vertex, to: Vertex, newWeight: Int) {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        setOneSideEdgeWeight(from, to, newWeight)
        setOneSideEdgeWeight(to, from, newWeight)
    }

    // Returns a vertex at specified position (or null, if there is no such vertex)
    fun getVertexAtPosition(position: Offset): Vertex? {
        var result: Vertex? = null
        var bestDistance = Float.POSITIVE_INFINITY
        for (vertex in vertices) {
            val vertexPosition = vertex.position ?: continue
            val distance = (vertexPosition - position).getDistance()
            if (distance <= GraphRenderOptions.VERTEX_SIZE && bestDistance > distance) {
                result = vertex
                bestDistance = distance
            }
        }

        return result
    }

    // Returns the vertex by its name, literally
    private fun getVertexByName(name: String): Vertex? {
        return verticesByName[name]
    }

    // Adds a vertex. New vertex must have some unique name
    fun addVertex(name: String, pos: Offset? = null): Vertex {
        if (verticesByName.containsKey(name)) {
            throw VertexAlreadyExistsException(name)
        }

        val vertex = super.newVertex(name, DEFAULT_VERTEX_COLOR, pos)
        verticesByName[name] = vertex

        return vertex
    }

    // Removes a vertex from the graph and removes its render info
    fun removeVertex(vertex: String) {
        removeVertex(getVertexByName(vertex) ?: throw NoSuchVertexException(vertex))
    }

    // Removes a vertex from the graph
    fun removeVertex(vertex: Vertex) {
        if (!vertices.contains(vertex)) {
            throw NoSuchVertexException(vertex.name)
        }

        assert(verticesByName.containsKey(vertex.name))

        verticesByName.remove(vertex.name)
        super.deleteVertex(vertex)
    }

    // Adds an edge to the graph between vertices with given names
    fun addEdge(from: String, to: String, weight: Int) {
        addEdge(getOrAddVertex(from), getOrAddVertex(to), weight)
    }

    // Adds an edge to the graph
    fun addEdge(from: Vertex, to: Vertex, weight: Int) {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        if (from.id == to.id) {
            throw SelfLoopException(from.name)
        }

        if (from.outcomingEdges.find { it.to.id == to.id } != null) {
            throw EdgeAlreadyExistsException(from.name, to.name)
        }

        super.addEdge(from, to, weight, DEFAULT_EDGE_COLOR)
    }

    // Removes an edge from the graph
    fun removeEdge(from: String, to: String) {
        removeEdge(
            getVertexByName(from) ?: throw NoSuchVertexException(from),
            getVertexByName(to) ?: throw NoSuchVertexException(to)
        )
    }

    // Removes an edge from the graph
    fun removeEdge(edge: Edge) {
        removeEdge(edge.first, edge.second)
    }

    // Removes an edge from the graph
    fun removeEdge(from: Vertex, to: Vertex) {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        if (from.outcomingEdges.find { it.to.id == to.id } == null) {
            throw NoSuchEdgeException(from.name, to.name)
        }

        super.deleteEdge(from, to)
    }

    // Generates a new unique name for a vertex
    fun makeUpVertexName(): String {
        val usedNumbers = BooleanArray(vertices.size) { false }
        vertices.forEach {
            val vertexNumber = it.name.toIntOrNull() ?: return@forEach

            if (vertexNumber in usedNumbers.indices) {
                usedNumbers[vertexNumber] = true
            }
        }

        val result = usedNumbers.indexOfFirst { !it }
        return (if (result == -1) vertices.size else result).toString()
    }


    // Returns the edge between 2 vertices
    // Returns null if no such edge exists
    fun getEdge(from: Vertex, to: Vertex): Edge? {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        val weight = from.outcomingEdges.find { it.to.id == to.id }?.weight ?: return null
        return Edge(from, to, weight)
    }

    // Returns the outcoming edge between 2 vertices
    // Returns null if no such edge exists
    fun getOutcomingEdge(from: Vertex, to: Vertex): OutcomingEdge? {
        if (!vertices.contains(from)) {
            throw NoSuchVertexException(from.name)
        } else if (!vertices.contains(to)) {
            throw NoSuchVertexException(to.name)
        }

        return from.outcomingEdges.find { it.to.id == to.id }
    }

    // Returns an existing vertex or inserts a new one if a vertex with such a name didn't exist
    private fun getOrAddVertex(name: String): Vertex {
        return if (!verticesByName.containsKey(name)) {
            addVertex(name)
        } else {
            verticesByName[name]!!
        }
    }

    // Returns the color of the given edge
    // If no such edge exists, returns null
    private fun getEdgeColor(from: Vertex, to: Vertex): Color? {
        return getOutcomingEdge(from, to)?.color
    }

    // Sets the color of an edge in one side
    private fun setOneSideEdgeColor(from: Vertex, to: Vertex, color: Color) {
        getOutcomingEdge(from, to)?.also { it.color = color } ?: throw NoSuchEdgeException(from.name, to.name)
    }

    // Sets the weight of an edge in one side
    private fun setOneSideEdgeWeight(from: Vertex, to: Vertex, newWeight: Int) {
        getOutcomingEdge(from, to)?.also { it.weight = newWeight } ?: throw NoSuchEdgeException(from.name, to.name)
    }
}