/*
* graph.OutcomingEdge.kt
* Contains the implementation of class OutcomingEdge that represents a single edge of a vertex.
* It is not an edge in the graph, but rather a convenient internal representation of an edge that
* connects a vertex to another (and another vertex has the opposite edge).
*/

package graph

import androidx.compose.ui.graphics.Color

data class OutcomingEdge(val to: Vertex, var weight: Int = 1, var color: Color)