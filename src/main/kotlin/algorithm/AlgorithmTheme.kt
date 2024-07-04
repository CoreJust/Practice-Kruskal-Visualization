package algorithm

import androidx.compose.ui.graphics.Color

enum class AlgorithmTheme(val edgeInMST: Color, val skippedEdge:Color, val textEdgeInMST:Color, val textSkippedEdge:Color, val allConsoleText:Color, val consoleSkippedEdge:Color, val consoleEdgeInMST:Color, val consoleRecoloredVert:Color, val consoleMSTWeight:Color){
    DEFAULT(Color.Magenta, Color.Gray, Color.Magenta, Color.Gray, Color.Black, Color(0, 24, 161, 255), Color(47, 5, 173, 255), Color(132, 0, 176, 255), Color.Magenta),
    RED(Color.Red, Color.Gray, Color.Red, Color.Gray, Color(69, 0, 196, 255), Color(131, 0, 207, 255), Color(255, 0, 251, 255), Color(255, 0, 136, 255), Color.Red),
    BLUE(Color.Blue, Color.Gray, Color.Blue, Color.Gray, Color(8, 0, 161, 255), Color(0, 52, 143, 255), Color(6, 122, 161, 255), Color(0, 163, 166, 255), Color(0, 179, 134, 255)),
    PASTEL(Color(252, 116, 173, 255), Color(128, 128, 128, 200), Color(252, 116, 173, 255), Color(128, 128, 128, 200), Color(212, 104, 131, 255), Color(212, 106, 185, 255), Color(180, 86, 196, 255), Color(139, 77, 189, 255), Color(73, 64, 168, 255))
}