/*
* file.GMLTokenizer.kt
* Contains the implementation of GMLTokenizer class that is used to split a GML file into a list of tokens.
*/

package file

import java.io.BufferedReader

class GMLTokenizer(private val input: BufferedReader) {
    // Returns null only when the end of file is reached
    fun nextToken(): GMLToken? {
        var ch = next() ?: return null
        while (true) {
            when {
                ch.isDigit() || ch == '-' -> return tokenizeNumber()
                ch.isLetter() -> return tokenizeIdentifier()
                ch == '"' -> return tokenizeString()
                ch == '[' -> return GMLToken(GMLTokenType.LEFT_BRACKET, "[")
                ch == ']' -> return GMLToken(GMLTokenType.RIGHT_BRACKET, "]")
                else -> {
                    ch = next() ?: return null
                }
            }
        }
    }

    // Reads an identifier and returns the token for it
    private fun tokenizeIdentifier(): GMLToken {
        unreadChar()

        var ch = next()
        val text = StringBuilder()

        while (ch != null && ch.isLetter()) {
            text.append(ch)
            ch = next()
        }

        unreadIfNotNull(ch)
        return GMLToken(GMLTokenType.IDENTIFIER, text.toString())
    }

    // Reads a number and returns the token for it
    private fun tokenizeNumber(): GMLToken {
        unreadChar()

        var ch = next()
        val text = StringBuilder()
        if (ch == '-') {
            text.append('-')
            ch = next()
        }


        while (ch != null && ch.isDigit()) {
            text.append(ch)
            ch = next()
        }

        if (ch == '.') {
            text.append('.')

            ch = next()
            while (ch != null && ch.isDigit()) {
                text.append(ch)
                ch = next()
            }

            unreadIfNotNull(ch)
            return GMLToken(GMLTokenType.DOUBLE, text.toString())
        } else {
            unreadIfNotNull(ch)
            return GMLToken(GMLTokenType.INT, text.toString())
        }
    }

    // Reads a string and returns the token for it
    private fun tokenizeString(): GMLToken {
        var ch = next()
        val text = StringBuilder()
        var isColor = false

        if (ch == '#') {
            isColor = true
        }

        while (ch != null && ch != '"') {
            text.append(ch)
            ch = next()
        }

        if (isColor && (text.length == 7 || text.length == 9)) {
            val rawColorValue = text.substring(1).toLongOrNull(16)
            if (rawColorValue != null) {
                var r = 0
                var g = 0
                var b = 0
                var a = 255
                when (text.length) {
                    7 -> { // RRGGBB
                        r = (rawColorValue / (256*256)).toInt()
                        g = ((rawColorValue / 256) % 256).toInt()
                        b = (rawColorValue % 256).toInt()
                    }
                    9 -> { // RRGGBBAA
                        r = (rawColorValue / (256*256*256)).toInt()
                        g = ((rawColorValue / (256*256)) % 256).toInt()
                        b = ((rawColorValue / 256) % 256).toInt()
                        a = (rawColorValue % 256).toInt()
                    }
                }

                return GMLToken(GMLTokenType.COLOR, "$r $g $b $a")
            }
        }

        return GMLToken(GMLTokenType.STRING, text.toString())
    }

    // Shift the reader to the previous character, unreading the current, if the current is not null
    // Works only once after next() was called
    private fun unreadIfNotNull(ch: Char?) {
        if (ch != null) {
            input.reset()
        }
    }

    // Returns the next character in the file
    private fun next(): Char? {
        input.mark(1)
        val result = input.read()
        if (result == -1) {
            return null
        }

        return result.toChar()
    }

    // Shift the reader to the previous character, unreading the current
    // Works only once after next() was called
    private fun unreadChar() {
        input.reset()
    }
}