/*
* file.GraphFileException.kt
* Contains the implementation of GraphFileException class that represents a non-fatal exception
* that is caused by some exceptional situation when loading from or saving to a graph file.
*
* Additionally, contains some more specific classes that extend the general GraphFileException class.
*/

package file

open class GraphFileException(text: String) : Exception(text)

class UnexpectedEOFException() : GraphFileException("Unexpected end of file while parsing graph while")
class UnexpectedTokenException(token: String) : GraphFileException("Unexpected token in graph file: $token")
class LabelDuplicateException(label: String) : GraphFileException("Encountered duplicate of label $label")

class UnsupportedGraphTypeException(graphType: String) : GraphFileException("Unsupported graph type: $graphType")
class LackingLabelException(parent: String, label: String) : GraphFileException("Lacking label: scope of $parent must have $label label")
class NonExistentNodeIdException(nodeId: Int) : GraphFileException("Edge contains node id that was not declared: $nodeId")