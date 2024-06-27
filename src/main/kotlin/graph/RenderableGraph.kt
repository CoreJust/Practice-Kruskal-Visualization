/*
* graph.RenderableGraph.kt
* Contains the implementation of RenderableGraph class that contains the actual logic of graph visualizing.
* It contains all the data on vertex positions, names, colors and so on.
*/

package graph

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
import graph.layout.Layout

class RenderableGraph : Graph() {
    companion object {
        val DEFAULT_COLOR = Color.Blue
        val ACTIVE_COLOR = Color.Red
        val TEXT_COLOR = Color.Cyan
        val WEIGHT_BACKGROUND_COLOR = Color.Gray.compositeOver(Color.White)

        const val VERTEX_SIZE = 0.028f
        const val EDGE_WIDTH = 0.005f
        const val VERTEX_NAME_FONT_SIZE = 0.04f
        const val WEIGHT_FONT_SIZE = 0.03f
        const val WEIGHT_POSITION = 0.5f // Controls how far the weight is positioned on the edge
    }

    private var verticesByName: MutableMap<String, Vertex> = mutableMapOf()

    // Does the actual rendering,
    // Must be called from Canvas context.
    fun renderGraph(drawScope: DrawScope, textMeasurer: TextMeasurer, offset: Offset, scale: Float, widgetScale: Float) {
        drawScope.withTransform(transformBlock = {
            scale(widgetScale, widgetScale, Offset(0f, 0f))
            translate(left = 0.5f, top = 0.5f)
            scale(scale, scale, Offset(0f, 0f))
            translate(left = offset.x - 0.5f, top = offset.y - 0.5f)
        }) {
            for (vertex in vertices) {
                val point = vertex.position ?: continue

                // First we render all the edges and their weights over them
                for (edge in vertex.edges) {
                    val toVertex = edge.to
                    if (vertex.id < toVertex.id) {
                        val toPosition = toVertex.position ?: continue

                        drawScope.drawLine(
                            color = edge.color,
                            start = point,
                            end = toPosition,
                            strokeWidth = EDGE_WIDTH
                        )

                        val weight = edge.weight.toString()
                        val weightPosition = lerp(point, toPosition, WEIGHT_POSITION)
                        val weightTextStyle = TextStyle.Default.copy(
                            color = TEXT_COLOR,
                            fontSize = WEIGHT_FONT_SIZE.sp
                        )

                        val weightSize = textMeasurer.measure(weight, style = weightTextStyle.copy(fontSize = weightTextStyle.fontSize * widgetScale))
                            .size.let {
                            Offset(
                                it.width.toFloat() / widgetScale,
                                -it.height.toFloat() / (2 * widgetScale)
                            )
                        }

                        drawScope.drawCircle( // The background for the weight
                            color = WEIGHT_BACKGROUND_COLOR,
                            radius = VERTEX_SIZE * 0.65f,
                            center = weightPosition
                        )

                        drawScope.drawText( // Weight of an edge
                            textMeasurer,
                            weight,
                            topLeft = weightPosition - weightSize * 0.5f,
                            style = weightTextStyle
                        )
                    }
                }

                // Then, the vertex itself is rendered
                drawScope.drawCircle(
                    color = vertex.color,
                    radius = VERTEX_SIZE,
                    center = point
                )

                // And finally, vertex names
                if (vertex.name.isNotEmpty() && vertex.name[0] != '_') { // Ignore vertex names that begin with _
                    val vertexTextStyle = TextStyle.Default.copy(
                        color = TEXT_COLOR,
                        fontSize = VERTEX_NAME_FONT_SIZE.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val vertexTextSize = textMeasurer.measure(vertex.name, style = vertexTextStyle.copy(fontSize = vertexTextStyle.fontSize * widgetScale))
                        .size.let {
                        Offset(
                            it.width.toFloat() / widgetScale,
                            -it.height.toFloat() / (2 * widgetScale)
                        )
                    }

                    drawScope.drawText( // Vertex name
                        textMeasurer,
                        vertex.name,
                        topLeft = point - vertexTextSize * 0.5f,
                        style = vertexTextStyle
                    )
                }
            }
        }
    }

    // Assign positions to all the vertices
    fun positionVertices(layout: Layout) {
        layout.positionVertices(this)
    }

    // Allows to give a vertex a color, if color is null than the default color is applied
    fun setVertexColor(vertex: Vertex, color: Color?) {
        vertex.color = color ?: DEFAULT_COLOR
    }

    // Allows to give a vertex a color, if color is null than the default color is applied
    fun setEdgeColor(from: Vertex, to: Vertex, color: Color?) {
        setOneSideEdgeColor(from, to, color ?: DEFAULT_COLOR)
        setOneSideEdgeColor(to, from, color ?: DEFAULT_COLOR)
    }

    // Returns a vertex at specified position (or null, if there is no such vertex)
    fun getVertexAtPosition(position: Offset): Vertex? {
        var result: Vertex? = null
        var bestDistance = Float.POSITIVE_INFINITY
        for (vertex in vertices) {
            val vertexPosition = vertex.position ?: continue
            val distance = (vertexPosition - position).getDistance()
            if (distance <= VERTEX_SIZE && bestDistance > distance) {
                result = vertex
                bestDistance = distance
            }
        }

        return result
    }

    // Returns the vertex by its name, literally
    fun getVertexByName(name: String): Vertex? {
        return verticesByName[name]
    }

    // Adds a vertex. New vertex must have some unique name
    fun addVertex(name: String, pos: Offset? = null): Vertex {
        if (verticesByName.containsKey(name)) {
            throw VertexAlreadyExistsException(name)
        }

        val vertex = super.newVertex(name, DEFAULT_COLOR, pos)
        verticesByName[name] = vertex

        return vertex
    }

    // Removes a vertex from the graph and removes its render info
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
        if (from.id == to.id) {
            throw SelfLoopException(from.name)
        }

        if (from.edges.find { it.to.id == to.id } != null) {
            throw EdgeAlreadyExistsException(from.name, to.name)
        }

        super.addEdge(from, to, weight, DEFAULT_COLOR)
    }

    // Removes an edge from the graph and removes its render info
    fun removeEdge(from: Vertex, to: Vertex) {
        if (from.edges.find { it.to.id == to.id } == null) {
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
        return from.edges.find { it.to.id == to.id }
    }

    // Returns an existing vertex or inserts a new one if a vertex with such a name didn't exist
    private fun getOrAddVertex(name: String): Vertex {
        if (!verticesByName.containsKey(name)) {
            return addVertex(name)
        } else {
            return verticesByName[name]!!
        }
    }

    // Returns the color of the given edge
    // If no such edge exists, returns null
    private fun getEdgeColor(from: Vertex, to: Vertex): Color? {
        return getEdge(from, to)?.color
    }

    // Sets the color of an edge in one side
    private fun setOneSideEdgeColor(from: Vertex, to: Vertex, color: Color) {
        getEdge(from, to)?.also { it.color = color } ?: throw NoSuchEdgeException(from.name, to.name)
    }
}