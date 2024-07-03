/*
* file.GMLParser.kt
* Contains the implementation of GMLParser class that is used to parse a GML file and load a graph from it.
*/

package file

import UI.Console
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import graph.GraphRenderOptions
import graph.RenderableGraph
import graph.Vertex

class GMLParser(private val tokenizer: GMLTokenizer) {
    private data class NodeGraphics(var position: Offset? = null, var color: Color? = null)
    private data class EdgeGraphics(var color: Color? = null)

    private val result = RenderableGraph()
    private val verticesByID: HashMap<Int, Vertex> = hashMapOf()

    fun parse(): RenderableGraph {
        while (parseTopLevel()) {
            // Does nothing, just parses the top-level as long as it is possible
        }

        return result
    }

    // The topmost level of GML file
    private fun parseTopLevel(): Boolean {
        val token = tokenizer.nextToken() ?: return false
        if (token.type != GMLTokenType.IDENTIFIER) {
            throw UnexpectedTokenException(token.text)
        }

        when (token.text) {
            "graph" -> parseGraphLevel()
            "Creator" -> consume(GMLTokenType.STRING).also { Console.println("Graph by ${it.text}") }
            "Version" -> consume(GMLTokenType.DOUBLE).also { Console.println("Graph version ${it.text}") }
            else -> skipUnknownLabel(token.text)
        }

        return true
    }

    // The contents of graph label - the actual graph (.graph)
    private fun parseGraphLevel() {
        fun consumeFloatInRange(range: ClosedFloatingPointRange<Float>): Float {
            return consume(GMLTokenType.DOUBLE).text
                .toFloat().let {
                    Math.clamp(it, range.start, range.endInclusive)
                }
        }

        parseScope(labelsAllowedToRepeat = listOf("node", "edge")) { label ->
            when (label) {
                "label" -> consume(GMLTokenType.STRING)
                "directed" -> consume(GMLTokenType.INT).also {
                    if ((it.text.toIntOrNull() ?: 1) > 0) throw UnsupportedGraphTypeException("directed graph")
                }
                "vertexsize" -> GraphRenderOptions.VERTEX_SIZE = consumeFloatInRange(GraphRenderOptions.VERTEX_SIZE_VALUE_RANGE)
                "edgewidth" -> GraphRenderOptions.EDGE_WIDTH = consumeFloatInRange(GraphRenderOptions.EDGE_WIDTH_VALUE_RANGE)
                "vertexfontsize" -> GraphRenderOptions.VERTEX_NAME_FONT_SIZE = consumeFloatInRange(GraphRenderOptions.VERTEX_NAME_FONT_SIZE_VALUE_RANGE)
                "weightfontsize" -> GraphRenderOptions.WEIGHT_FONT_SIZE = consumeFloatInRange(GraphRenderOptions.WEIGHT_FONT_SIZE_RANGE)
                "weightposition" -> GraphRenderOptions.WEIGHT_POSITION = consumeFloatInRange(GraphRenderOptions.WEIGHT_POSITION_VALUE_RANGE)
                "node" -> parseNode()
                "edge" -> parseEdge()
                else -> skipUnknownLabel(label)
            }
        }
    }

    // Parses a node scope (.graph.node)
    private fun parseNode() {
        var id: Int? = null
        var name: String? = null
        var nodeGraphics = NodeGraphics()

        parseScope { label ->
            when (label) {
                "id" -> id = consume(GMLTokenType.INT).text.toIntOrNull()
                "name", "label" -> name = consume(GMLTokenType.STRING).text
                "graphics" -> nodeGraphics = parseNodeGraphics()
                else -> skipUnknownLabel(label)
            }
        }

        if (id == null) {
            throw LackingLabelException("node", "id")
        }

        // Adding the node to the graph
        val vertex = result.addVertex(name ?: result.makeUpVertexName(), nodeGraphics.position)
        result.setVertexColor(vertex, nodeGraphics.color)

        verticesByID[id!!] = vertex
    }

    // Parses an edge scope (.graph.edge)
    private fun parseEdge() {
        var source: Int? = null
        var target: Int? = null
        var weightLabel: String? = null
        var edgeGraphics = EdgeGraphics()

        parseScope { label ->
            when(label) {
                "source" -> source = consume(GMLTokenType.INT).text.toIntOrNull()
                "target" -> target = consume(GMLTokenType.INT).text.toIntOrNull()
                "label" -> weightLabel = consume(GMLTokenType.STRING).text
                "graphics" -> edgeGraphics = parseEdgeGraphics()
                else -> skipUnknownLabel(label)
            }
        }

        val sourceId = source ?: throw LackingLabelException("edge", "source")
        val targetId = target ?: throw LackingLabelException("edge", "target")
        val weight = (weightLabel ?: "1").toIntOrNull() ?: throw UnsupportedGraphTypeException("weight can only be an integer")

        val from = verticesByID[sourceId] ?: throw NonExistentNodeIdException(sourceId)
        val to = verticesByID[targetId] ?: throw NonExistentNodeIdException(targetId)
        result.addEdge(from, to, weight)
        result.setEdgeColor(from, to, edgeGraphics.color)
    }

    // Parses a node graphics scope (.graph.node.graphics)
    private fun parseNodeGraphics(): NodeGraphics {
        var x: Float? = null
        var y: Float? = null
        var color: Color? = null

        parseScope { label ->
            when(label) {
                "x" -> x = consume(GMLTokenType.DOUBLE).text.toFloatOrNull()
                "y" -> y = consume(GMLTokenType.DOUBLE).text.toFloatOrNull()
                "fill" -> consume(GMLTokenType.COLOR).text
                    .split(' ')
                    .map{ it.toInt() }
                    .also {
                    color = Color(it[0], it[1], it[2], it[3])
                }
                else -> skipUnknownLabel(label)
            }
        }

        return if (x != null && y != null){
            NodeGraphics(Offset(x!!, y!!), color)
        } else {
            NodeGraphics(color = color)
        }
    }

    // Parses aa edge graphics scope (.graph.edge.graphics)
    private fun parseEdgeGraphics(): EdgeGraphics {
        var color: Color? = null

        parseScope { label ->
            when(label) {
                "fill" -> consume(GMLTokenType.COLOR).text
                    .split(' ')
                    .map{ it.toInt() }
                    .also {
                        color = Color(it[0], it[1], it[2], it[3])
                    }
                else -> skipUnknownLabel(label)
            }
        }

        return EdgeGraphics(color)
    }

    // Auxiliary function used for parsing scopes
    private fun parseScope(labelsAllowedToRepeat: List<String> = listOf(), onLabel: (String) -> Unit) {
        consume(GMLTokenType.LEFT_BRACKET)

        val labelsInScope: ArrayList<String> = arrayListOf()
        while (true) {
            val token = tokenizer.nextToken() ?: throw UnexpectedEOFException()
            if (token.type == GMLTokenType.RIGHT_BRACKET) {
                return // End of graph scope
            }

            if (token.type != GMLTokenType.IDENTIFIER) {
                throw UnexpectedTokenException(token.text)
            }

            if (!labelsAllowedToRepeat.contains(token.text)) {
                if (labelsInScope.contains(token.text)) {
                    throw LabelDuplicateException(token.text)
                }

                labelsInScope.add(token.text)
            }

            onLabel(token.text)
        }
    }

    // Skips an unknown GML label value
    private fun skipUnknownLabel(label: String) {
        Console.println("Unknown label: $label", color = Color.Yellow)

        var token = tokenizer.nextToken() ?: throw UnexpectedEOFException()
        if (token.type == GMLTokenType.RIGHT_BRACKET) {
            var scopeDepth = 1
            do {
                token = tokenizer.nextToken() ?: throw UnexpectedEOFException()
                when(token.type) {
                    GMLTokenType.LEFT_BRACKET -> scopeDepth += 1
                    GMLTokenType.RIGHT_BRACKET -> scopeDepth -= 1
                    else -> continue
                }
            } while(scopeDepth > 0)
        }
    }

    // Gets the next token and if its type is not the given one, throws an exception
    private fun consume(tokenType: GMLTokenType): GMLToken {
        val token = tokenizer.nextToken() ?: throw UnexpectedEOFException()
        if (token.type != tokenType) {
            throw UnexpectedTokenException(token.text)
        }

        return token
    }
}