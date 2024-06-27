/*
* graph.Vertex.kt
* Contains the implementation of class Vertex that represents a single vertex of a graph.
* The actual logic of a vertex is distributed throughout the project (specifically in Graph.kt and RenderableGraph.kt).
*/

package graph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

typealias VertexId = Int

data class Vertex(val id: VertexId, var name: String, var position: Offset? = null, var color: Color)
    : Comparable<Vertex> {
    var edges: ArrayList<Edge> = arrayListOf()

    override fun compareTo(other: Vertex): Int = this.id - other.id
}