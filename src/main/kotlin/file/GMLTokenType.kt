/*
* file.GMLTokenType.kt
* Contains the implementation of GMLTokenType enum class that represents a kind of GMLToken.
*/

package file

enum class GMLTokenType {
    IDENTIFIER,
    INT,
    DOUBLE,
    STRING,
    COLOR,
    LEFT_BRACKET, // [
    RIGHT_BRACKET // ]
}