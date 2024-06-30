/*
* graph.GraphColoring.kt
* Contains the implementation of the GraphColoring class that allows to conveniently store
* all colors of an existent graph so that its colors could be switched without containing the whole graph
*/

package graph

import androidx.compose.ui.graphics.Color

class GraphColoring {
    data class VertexColorInfo(var vertexColor: Color, val edgeColors: ArrayList<Color>)

    private var graphColors: ArrayList<VertexColorInfo> = arrayListOf()

    // Loads the coloring of the given graph
    internal fun loadFromGraph(graph: RenderableGraph): GraphColoring {
        graphColors.ensureCapacity(graph.vertices.size)
        for (vertex in graph.vertices) {
            val edgeColors: ArrayList<Color> = arrayListOf()
            edgeColors.ensureCapacity(vertex.outcomingEdges.size)

            for (outcomingEdge in vertex.outcomingEdges) {
                edgeColors.add(outcomingEdge.color)
            }

            graphColors.add(VertexColorInfo(vertex.color, edgeColors))
        }

        return this
    }

    // Applies the loaded coloring to a graph
    internal fun applyToGraph(graph: RenderableGraph) {
        assert(graphColors.size == graph.vertices.size)

        var i = 0
        for (vertex in graph.vertices) {
            val vertexColors = graphColors[i]
            i += 1

            assert(vertexColors.edgeColors.size == vertex.outcomingEdges.size)

            vertex.color = vertexColors.vertexColor
            for (j in vertex.outcomingEdges.indices) {
                vertex.outcomingEdges[j].color = vertexColors.edgeColors[j]
            }
        }
    }
}