/*
* file.GMLGraphFile.kt
* Contains the implementation of GMLGraphFile class that inherits the GraphFile and allows
* to work with the Graph Modeling Language (.gml) files.
*/

package file

import androidx.compose.ui.graphics.Color
import graph.GraphRenderOptions
import graph.RenderableGraph
import java.io.File

class GMLGraphFile(val file: File) : GraphFile {
    override fun loadGraph(): RenderableGraph {
        val tokenizer = GMLTokenizer(file.bufferedReader())
        val result = GMLParser(tokenizer).parse()
        if (result.vertices.find { it.position == null } != null) {
            result.positionVertices()
        }

        return result
    }

    override fun saveGraph(graph: RenderableGraph) {
        // Converts a single float color component into hex format
        fun Float.colorToHex(): String {
            return (this * 255).toInt().toString(16).let {
                if (it.length == 1) {
                    "0$it"
                } else {
                    it
                }
            }
        }

        // Converts Compose color into GML color format
        fun Color.toGMLColor(): String {
            return "\"#${red.colorToHex()}${green.colorToHex()}${blue.colorToHex()}${alpha.colorToHex()}\""
        }

        file.bufferedWriter().use { out ->
            out.write("Creator \"Kruskal algorithm visualizer\"\n")
            out.write("graph [\n")
            out.write("\tlabel \"Graph\"\n")
            out.write("\tdirected -1\n")
            out.write("\tvertexsize ${GraphRenderOptions.VERTEX_SIZE}\n")
            out.write("\tedgewidth ${GraphRenderOptions.EDGE_WIDTH}\n")
            out.write("\tvertexfontsize ${GraphRenderOptions.VERTEX_NAME_FONT_SIZE}\n")
            out.write("\tweightfontsize ${GraphRenderOptions.WEIGHT_FONT_SIZE}\n")
            out.write("\tweightposition ${GraphRenderOptions.WEIGHT_POSITION}\n")

            for (vertex in graph.vertices) {
                out.write("\tnode [\n")
                out.write("\t\tid ${vertex.id}\n")
                out.write("\t\tlabel \"${vertex.name}\"\n")
                out.write("\t\tgraphics [\n")
                out.write("\t\t\tfill ${vertex.color.toGMLColor()}\n")
                if (vertex.position != null) {
                    out.write("\t\t\tx ${vertex.position!!.x}\n")
                    out.write("\t\t\ty ${vertex.position!!.y}\n")
                }
                out.write("\t\t]\n")
                out.write("\t]\n")
            }

            for (edge in graph.edges) {
                out.write("\tedge [\n")
                out.write("\t\tsource ${edge.first.id}\n")
                out.write("\t\ttarget ${edge.second.id}\n")
                out.write("\t\tlabel \"${edge.weight}\"\n")
                out.write("\t\tgraphics [\n")
                out.write("\t\t\tfill ${edge.color.toGMLColor()}\n")
                out.write("\t\t]\n")
                out.write("\t]\n")
            }

            out.write("]")
        }
    }
}