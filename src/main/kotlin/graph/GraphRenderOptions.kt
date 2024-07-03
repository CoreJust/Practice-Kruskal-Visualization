/*
* graph.GraphRenderOptions.kt
* Contains the GraphRenderOptions class that contains a number of global constants used for rendering graphs.
*/

package graph

import graph.layout.CircleLayout
import graph.layout.Layout
import graph.layout.LayoutType

class GraphRenderOptions {
    companion object {
        var VERTEX_SIZE = 0.028f
        var EDGE_WIDTH = 0.005f
        var VERTEX_NAME_FONT_SIZE = 0.04f
        var WEIGHT_FONT_SIZE = 0.03f
        var WEIGHT_POSITION = 0.5f // Controls how far the weight is positioned on the edge

        var layoutType: LayoutType = LayoutType.CircleLayout
        var layout: Layout = CircleLayout()

        val VERTEX_SIZE_VALUE_RANGE = 0.005f..0.1f
        val EDGE_WIDTH_VALUE_RANGE = 0.001f..0.025f
        val VERTEX_NAME_FONT_SIZE_VALUE_RANGE = 0.005f..0.07f
        val WEIGHT_FONT_SIZE_RANGE = 0.004f..0.05f
        val WEIGHT_POSITION_VALUE_RANGE = 0.05f..0.95f
    }
}