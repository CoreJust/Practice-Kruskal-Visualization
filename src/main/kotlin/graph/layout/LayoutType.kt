/*
* graph.layout.LayoutType.kt
* Contains the enum class LayoutType that enumerates the possible layout algorithms.
*/

package graph.layout

enum class LayoutType(val layoutName: String, val layoutClass: Layout) {
    CircleLayout("Circle layout", CircleLayout()),
    NaiveGridLayout("Naive grid layout", NaiveGridLayout()),
    SpringLayout("Spring layout", SpringLayout())
}