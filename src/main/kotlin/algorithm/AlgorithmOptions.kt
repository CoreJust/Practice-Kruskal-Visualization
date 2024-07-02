package algorithm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AlgorithmOptions{
    companion object {
        var theme = AlgorithmTheme.DEFAULT
        var consoleTextSize by mutableStateOf(16f)
        var edgeWindowTextSize by mutableStateOf(22f)
        var useEdgeWindow by mutableStateOf(true)
    }
}