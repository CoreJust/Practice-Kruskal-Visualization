/*
* graph.GraphRenderOptions.kt
* Contains the GraphRenderOptions class that contains a number of global constants used for rendering graphs.
*/

package graph

import graph.layout.CircleLayout
import graph.layout.Layout

class GraphRenderOptions {
    companion object {
        var VERTEX_SIZE = 0.028f
        var EDGE_WIDTH = 0.005f
        var VERTEX_NAME_FONT_SIZE = 0.04f
        var WEIGHT_FONT_SIZE = 0.03f
        var WEIGHT_POSITION = 0.5f // Controls how far the weight is positioned on the edge

        var layout: Layout = CircleLayout()
    }
}