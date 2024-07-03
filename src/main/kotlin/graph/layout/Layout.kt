/*
* graph.layout.Layout.kt
* Contains the abstract Layout class.
* A layout is some kind of algorithm that places all the vertices of a graph within some 2D plane.
* There are some algorithms that may preserve vertex positions that are already set,
* and some that would reset them.
*/

package graph.layout

import androidx.compose.ui.geometry.Offset
import graph.RenderableGraph
import graph.Vertex
import java.util.*

abstract class Layout {
    abstract fun positionVertices(renderableGraph: RenderableGraph)

    // A simple auxiliary function that resets all vertex positions
    protected fun resetVertexPositions(vertices: TreeSet<Vertex>) {
        vertices.forEach { it.position = null }
    }

    // Multiplies vertex positions by a given factor
    protected fun rescaleVertexPositions(vertices: Set<Vertex>, factor: Float) {
        for (vertex in vertices) {
            vertex.position = (vertex.position!! - Offset(0.5f, 0.5f)) * factor + Offset(0.5f, 0.5f)
        }
    }
}