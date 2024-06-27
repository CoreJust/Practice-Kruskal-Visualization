/*
* file.TGFGraphFile.kt
* Contains the implementation of TGFGraphFile class that inherits the GraphFile and allows
* to work with the Trivial Graph Format (.tgf) files.
*/

package file

import graph.Graph
import graph.RenderableGraph
import java.io.File

/*
*   Trivial Graph Format goes like that:
*   [TGF file] =
*       [Vertices]
*       #
*       [Edges]
*
*   [Vertices] =
*       [vertex number: Int] [optional: vertex name: String]
*       ...
*
*   [Edges] =
*       [first vertex: Int] [second vertex: Int] [optional: weight: Int]
*       ...
*
* Actually, weight can be a String, but here we suppose that it is always an Int
*/

class TGFGraphFile(val file: File) : GraphFile {
    override fun loadGraph(): RenderableGraph {
        val result = RenderableGraph()
        var isReadingVertices = true
        val vertexMap: MutableMap<Int, String> = mutableMapOf()

        file.forEachLine { line ->
            if (line == "#") {
                isReadingVertices = false
            } else if (isReadingVertices) {
                val vertexLabelStr = line.substringBefore(' ')
                if (vertexLabelStr.isEmpty()) return@forEachLine;

                val vertexLabel = vertexLabelStr.toIntOrNull() ?: return@forEachLine
                var vertexName = line.substringAfter(' ')
                if (vertexName.isEmpty()) {
                    vertexName = vertexLabelStr
                }

                vertexMap[vertexLabel] = vertexName
                result.addVertex(vertexName)
            } else { // Reading edges
                val from = line.substringBefore(' ').toIntOrNull() ?: return@forEachLine
                val leftover = line.substringAfter(' ', "")
                if (leftover.isEmpty()) return@forEachLine

                val weight = leftover.substringAfter(' ', "").toIntOrNull() ?: 1
                val to = leftover.substringBefore(' ').toIntOrNull() ?: return@forEachLine

                result.addEdge(vertexMap[from] ?: return@forEachLine, vertexMap[to] ?: return@forEachLine, weight)
            }
        }

        return result
    }

    override fun saveGraph(graph: RenderableGraph) {
        file.bufferedWriter().use { out ->
            for (vertex in graph.vertices) {
                out.write("${vertex.id} ${vertex.name}\n")
            }

            out.write("#\n")
            for (vertex in graph.vertices) {
                for (edge in vertex.edges) {
                    if (edge.to.id <= vertex.id) continue;

                    out.write("${vertex.id} ${edge.to.id} ${edge.weight}\n")
                }
            }
        }
    }
}