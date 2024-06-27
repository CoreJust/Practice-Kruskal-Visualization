/*
* graph.Graph.kt
* Contains the implementation of class Graph that represents an actual graph.
* It allows some basic graph operations, incident lists are used to contain the graph.
* Also, graph implements the accommodation of vertices (but not their colors).
*/

package graph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.TreeSet
import kotlin.collections.ArrayList

open class Graph {
    var vertices: TreeSet<Vertex> = TreeSet()
        internal set

    private var vertexIdCounter = 0 // Allows to give unique IDs to each vertex

    // Splits the vertices of the graph into several subsets that represent its connectivity components.
    fun splitIntoComponents(): Array<MutableSet<Vertex>> {
        val vertexComponent = HashMap<Int, Int>(vertices.size) // The component that each vertex belongs to
        var currentComponent = 0

        vertices.forEach { vertexComponent[it.id] = -1 }

        for (startVertex in vertices) {
            if (vertexComponent[startVertex.id] == -1) { // Checking if this vertex has no component yet
                // Now we add all the vertices this one is somehow related to
                vertexComponent[startVertex.id] = currentComponent

                val stack: ArrayList<Pair<Vertex, Int>> = arrayListOf()
                stack.add(Pair(startVertex, 0))
                while (stack.isNotEmpty()) { // Go with DFS through graph
                    val currentVertex = stack.last().first
                    var i = stack.last().second

                    while (true) {
                        if (i >= currentVertex.edges.size) {
                            stack.removeLast()
                            break
                        }

                        val adjacentVertex = currentVertex.edges[i].to
                        if (vertexComponent[adjacentVertex.id] == -1) { // Found adjacent vertex that is still not in the component
                            stack[stack.size - 1] = Pair(currentVertex, i + 1)
                            stack.add(Pair(adjacentVertex, 0))
                            vertexComponent[adjacentVertex.id] = currentComponent
                            break
                        }

                        i += 1
                    }
                }

                currentComponent += 1
            }
        }

        // Now we have all the vertices marked according to their component and all that is left is to
        // put those vertices into separate lists
        val result: Array<MutableSet<Vertex>> = Array<MutableSet<Vertex>>(currentComponent) { mutableSetOf() }
        for (vertex in vertices) {
            result[vertexComponent[vertex.id]!!].add(vertex)
        }

        return result
    }

    // Adds an isolated unnamed vertex to the graph
    protected fun newVertex(name: String, color: Color, position: Offset? = null): Vertex {
        val vertex = Vertex(vertexIdCounter, name, position, color)
        vertexIdCounter += 1

        vertices.add(vertex)
        return vertex
    }

    // Removes a vertex from the graph
    protected fun deleteVertex(vertex: Vertex) {
        assert(vertices.contains(vertex))

        for (edge in vertex.edges) {
            edge.to.edges.removeIf { it.to.id == vertex.id }
        }

        vertex.edges.clear()
        vertices.remove(vertex)
    }

    // Adds an edge to the graph
    protected fun addEdge(from: Vertex, to: Vertex, weight: Int, color: Color) {
        from.edges.add(Edge(to, weight, color))
        to.edges.add(Edge(from, weight, color))
    }

    // Removes an edge from the graph
    protected fun deleteEdge(from: Vertex, to: Vertex) {
        from.edges.removeIf { it.to.id == to.id }
        to.edges.removeIf { it.to.id == from.id }
    }
}