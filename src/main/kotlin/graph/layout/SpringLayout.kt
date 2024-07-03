/*
* graph.layout.SpringLayout.kt
* Contains the implementation of class SpringLayout that is one of the vertex positioning algorithms.
* Spring layout emulates a physical process where there is a repulsive force between vertices
* and the edges act as springs.
*/

package graph.layout

import androidx.compose.ui.geometry.Offset
import graph.RenderableGraph
import graph.VertexId
import kotlin.math.pow

/*
* Parameters:
*   iterationsFactor is the factor of iterations count from graph size: iterations = 5 * |G|^iterationsFactor.
*                    It has the range of values from 0f to 1f
*   repulsionFactor is a factor of repulsion force.
*   springFactor is a factor of spring force.
*   edgeWeightPower is the power to which the edge weight is raised (from 0 to 1)
*   distanceLimit is the maximal distance where repulsion can act.
*   delta is the factor with which the force is applied to a vertex.
*/
class SpringLayout(
    var iterationsFactor: Float = 0.5f,
    var repulsionFactor: Float = 0.9f,
    var springFactor: Float = 0.6f,
    var edgeWeightPower: Float = 0.5f,
    var distanceLimit: Float = 4f,
    var delta: Float = 0.05f
) : Layout() {
    override fun positionVertices(renderableGraph: RenderableGraph) {
        CircleLayout().positionVertices(renderableGraph) // We need some original vertices positions

        val iterationCount = 10 * renderableGraph.vertices.size.toFloat().pow(iterationsFactor).toInt()
        val scaleFactor = 8f // Since screen coordinates are just [0, 1], it would otherwise break

        rescaleVertexPositions(renderableGraph.vertices, scaleFactor)
        repeat(iterationCount) {
            iteration(renderableGraph)
        }

        rescaleVertexPositions(renderableGraph.vertices, 1f / scaleFactor)
    }

    // Does a single iteration of spring layout algorithm
    private fun iteration(renderableGraph: RenderableGraph) {
        // Forces acting on each of the vertices
        val vertexForce: HashMap<VertexId, Offset> = HashMap(renderableGraph.vertices.size)
        renderableGraph.vertices.forEach { vertexForce[it.id] = Offset(0f, 0f) }

        // Calculating forces
        for (vertex in renderableGraph.vertices) {
            for (secondVertex in renderableGraph.vertices) {
                if (secondVertex.id == vertex.id || secondVertex.position == vertex.position) continue

                val relativePosition = vertex.position!! - secondVertex.position!!
                val distance = maxOf(relativePosition.getDistance(), 0.01f)
                val edgeWeight = vertex.outcomingEdges.find { it.to.id == secondVertex.id }?.weight?.toFloat()?.pow(edgeWeightPower)

                val repulsionForce =
                    if (distance < distanceLimit) {
                        repulsionFactor / distance.pow(2)
                    } else {
                        0f
                    }

                val springForce =
                    if (edgeWeight != null) {
                        -springFactor * (distance - edgeWeight)
                    } else {
                        0f
                    }

                val totalForce = springForce + repulsionForce
                vertexForce[vertex.id] = vertexForce[vertex.id]!! + (relativePosition * totalForce / distance)
            }
        }

        // Applying forces
        for (vertex in renderableGraph.vertices) {
            vertex.position = vertex.position!! + vertexForce[vertex.id]!! * delta
        }
    }
}