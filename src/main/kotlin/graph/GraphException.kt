/*
* graph.GraphException.kt
* Contains the implementation of GraphException class that represents a non-fatal exception
* that is caused by some graph-specific situation (for example, trying to add a self-loop).
*
* Additionally contains some more specific classes that extend the general GraphException class.
*/

package graph

open class GraphException(text: String) : Exception(text)

class VertexAlreadyExistsException(vertexName: String) : GraphException("Such a vertex already exists: $vertexName")
class EdgeAlreadyExistsException(fromVertexName: String, toVertexName: String) : GraphException("Such an edge already exists: ($fromVertexName - $toVertexName)")
class SelfLoopException(vertexName: String) : GraphException("No self-loops are allowed in the graph, tried to add a self-loop to vertex $vertexName")
class NoSuchVertexException(vertexName: String) : GraphException("No such vertex exists in graoh: $vertexName")
class NoSuchEdgeException(fromVertexName: String, toVertexName: String) : GraphException("No such edge exists in graoh: ($fromVertexName - $toVertexName)")

class UnsupportedGraphFormatException(extension: String) : GraphException("Unsupported graph extension: $extension")