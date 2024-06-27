/*
* file.GraphFile.kt
* Contains the GraphFIle interface that is the base for all graph file format classes,
* as well as the openGraphFile function that returns a GraphFile with a suitable extension.
*/

package file

import graph.RenderableGraph
import graph.UnsupportedGraphFormatException
import java.io.File

interface GraphFile {
    fun loadGraph(): RenderableGraph
    fun saveGraph(graph: RenderableGraph)
}

fun openGraphFile(file: File): GraphFile = when(file.extension) {
    "tgf" -> TGFGraphFile(file)
    else -> throw UnsupportedGraphFormatException(file.extension)
}