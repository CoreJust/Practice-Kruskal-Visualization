/*
* graph.Edge.kt
* Contains the implementation of class Edge that represents a single edge of the graph.
* The actual logic of an edge is distributed throughout the project (specifically in Graph.kt).
*/

package graph

import androidx.compose.ui.graphics.Color

data class Edge(val to: Vertex, var weight: Int = 1, var color: Color)