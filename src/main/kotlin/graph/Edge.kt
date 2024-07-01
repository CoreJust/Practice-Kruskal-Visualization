/*
* graph.Edge.kt
* Contains the implementation of class Edge that represents a single edge of the graph.
*/

package graph

import androidx.compose.ui.graphics.Color

data class Edge(val first: Vertex, val second: Vertex, var weight: Int = 1, val color: Color = Color.Transparent)