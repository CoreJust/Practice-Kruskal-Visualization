/*
* file.GraphFileException.kt
* Contains the implementation of GraphFileException class that represents a non-fatal exception
* that is caused by some exceptional situation when loading from or saving to a graph file.
*
* Additionally, contains some more specific classes that extend the general GraphFileException class.
*/

package file

open class GraphFileException(text: String) : Exception(text)