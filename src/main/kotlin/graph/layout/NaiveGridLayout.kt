/*
* graph.layout.NaiveGridLayout.kt
* Contains the implementation of class NaiveGridLayout that is one of the vertex positioning algorithms.
* Naive grid layout positions vertices in a simple grid.
*/

package graph.layout

import androidx.compose.ui.geometry.Offset
import graph.RenderableGraph
import kotlin.math.floor
import kotlin.math.sqrt

/*
* Parameters:
*   gridStep is the distance between the vertices.
*/
class NaiveGridLayout(var gridStep: Float = 0.1f) : Layout() {
    override fun positionVertices(renderableGraph: RenderableGraph) {
        // First of all, we reset the vertices - grid layout wouldn't tolerate preset positions
        resetVertexPositions(renderableGraph.vertices)

        val width = floor(sqrt(renderableGraph.vertices.size.toDouble())).toInt()
        val initialCoordinate = 0.5f - width * gridStep / 2
        var x = 0
        var y = 0
        val components = renderableGraph.splitIntoComponents()
        for (component in components) {
            for (vertex in component) {
                vertex.position = Offset(initialCoordinate + x * gridStep, initialCoordinate + y * gridStep)
                x += 1
                if (x >= width) {
                    x = 0
                    y += 1
                }
            }
        }
    }
}