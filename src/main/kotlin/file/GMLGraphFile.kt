/*
* file.GMLGraphFile.kt
* Contains the implementation of GMLGraphFile class that inherits the GraphFile and allows
* to work with the Graph Modeling Language (.gml) files.
*/

package file

import graph.RenderableGraph
import java.io.File

class GMLGraphFile(val file: File) : GraphFile {
    override fun loadGraph(): RenderableGraph {
        TODO("Not yet implemented")
    }

    override fun saveGraph(graph: RenderableGraph) {
        TODO("Not yet implemented")
    }
}