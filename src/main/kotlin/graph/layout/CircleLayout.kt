/*
* graph.layout.CircleLayout.kt
* Contains the implementation of class CircleLayout that is one of the vertex positioning algorithms.
* Circle layout positions all the vertices in a circular manner.
* It is optimal for complete graphs, and works pretty fast, so can be applied in many general cases.
*/

package graph.layout

import androidx.compose.ui.geometry.Offset
import graph.RenderableGraph
import graph.Vertex
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/*
* Parameters:
*   radius is the circle's radius. It must be within [0; 1], where 1 is the maximum value.
*          If radius is 0, then it is given the default value.
*   separateComponents is a flag. If it is set, then separate components are adjusted into separate layouts.
*/
class CircleLayout(private val radius: Float = 0f, private val separateComponents: Boolean = true) : Layout() {
    override fun positionVertices(renderableGraph: RenderableGraph) {
        // First of all, we reset the vertices - circle layout wouldn't tolerate preset positions
        resetVertexPositions(renderableGraph.vertices)

        if (separateComponents) {
            val components = renderableGraph.splitIntoComponents().sortedByDescending { it.size }
            if (components.size > 1) {
                val areas = computeComponentAreas(components.map { it.size.toFloat() })
                for (i in areas.indices) {
                    positionVerticesInSingleCircle(
                        components[i],
                        areas[i].first,
                        computeActualRadius(radius, areas[i].second, components[i].size)
                    )
                }

                rescaleVertexPositionsIfNecessary(renderableGraph.vertices, renderableGraph.edges.size)
                return
            }
        }

        positionVerticesInSingleCircle(renderableGraph.vertices, Offset(0.5f, 0.5f), computeActualRadius(radius))
        rescaleVertexPositionsIfNecessary(renderableGraph.vertices, renderableGraph.edges.size)
    }

    // Scales vertex positions around center for the given scale
    // if it is necessary due to vertex count being too large
    private fun rescaleVertexPositionsIfNecessary(vertices: Set<Vertex>, edgesCount: Int) {
        val rescaleFactor = sqrt(vertices.size.toDouble() / 30 + edgesCount / 100).toFloat()
        if (rescaleFactor > 1) {
            for (vertex in vertices) {
                vertex.position = (vertex.position!! - Offset(0.5f, 0.5f)) * rescaleFactor + Offset(0.5f, 0.5f)
            }
        }
    }

    // Does the actual positioning within a predefined circle
    private fun positionVerticesInSingleCircle(vertices: Set<Vertex>, center: Offset, radius: Float) {
        if (vertices.size == 1) {
            vertices.first().position = center
            return
        }

        val step = 2 * Math.PI / vertices.size
        var angle = 0.0
        for (vertex in vertices) {
            vertex.position = center + Offset(sin(angle).toFloat(), cos(angle).toFloat()) * radius
            angle += step
        }
    }

    // Auxiliary function that computes where to place graph components
    // Returns a list of area centers and radii
    private fun computeComponentAreas(estimatedSizes: List<Float>): List<Pair<Offset, Float>> {
        val result: ArrayList<Pair<Offset, Float>> = arrayListOf()
        var end = 0f
        var endX = 0f
        var endY = 0f

        // Here we solve a task of packing some square areas into a square as small as possible (although not perfectly)
        for (areaSize in estimatedSizes) {
            if (areaSize > endX && areaSize > endY) {
                result.add(Pair(Offset(end + areaSize / 2f, end + areaSize / 2f), areaSize))
                endX = end
                endY = end
                end += areaSize
                continue
            }

            if (areaSize <= endX) {
                result.add(Pair(Offset(endX - areaSize / 2f, end - areaSize / 2f), areaSize))
                endX -= areaSize
                continue
            }

            result.add(Pair(Offset(end - areaSize / 2f, endY - areaSize / 2f), areaSize))
            endY -= areaSize
        }

        // And now all we need to do is to rescale the created bigger square so that
        // all the coordinates are within range [0; 1]
        val scaleFactor = 1f / end
        return result.map { Pair(it.first * scaleFactor, it.second * scaleFactor) }
    }

    // Auxiliary function that calculates the actual circle radius based on the parameter given and area size
    private fun computeActualRadius(originalRadius: Float, areaSize: Float = 1f, verticesCount: Int = 1): Float {
        val smallComponentModifier =
            if (verticesCount == 2) 0.85f else 1f // So that components of 2 vertices are rendered correctly
        if (originalRadius == 0f) {
            return areaSize * 0.45f * smallComponentModifier
        } else {
            return areaSize * Math.clamp(originalRadius, 0f, 1f) * 0.45f * smallComponentModifier
        }
    }
}