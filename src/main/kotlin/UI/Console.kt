/*
* UI.Console.kt
* Contains the implementation of ConsoleUI function that represents the text console widget to the right of the graph window
* and the implementation of the Console class that provides API to work with the console (specifically, to print there).
* Can be in 2 modes: edit mode where it can accept user input and algorithm mode where it can only show information.
*/

package UI

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import graph.Graph
import graph.RenderableGraph
import graph.layout.CircleLayout

// Class with static API for simulating console
class Console {
    companion object {
        internal var text by mutableStateOf("")
        internal var lastEditMode = true

        fun print(str: String) {
            text += str
        }

        fun println(str: String) {
            text += str + '\n'
        }

        fun clear() {
            text = ""
        }

        fun setEditMode(isEditMode: Boolean) {
            if (isEditMode != lastEditMode) {
                clear()
                lastEditMode = isEditMode
            }
        }
    }
}

// The composable function that displays the actual console
@Composable
fun RowScope.ConsoleUI(isEditMode: Boolean, onGraphChange: (RenderableGraph) -> Unit) {
    Console.setEditMode(isEditMode)

    Column (
        modifier = Modifier.fillMaxHeight().weight(1f)
    ) {
        OutlinedTextField(
            value = Console.text,
            onValueChange = { if (isEditMode) Console.text = it },
            modifier = Modifier.fillMaxWidth().weight(10f),
            label = { Text("Console") },
            singleLine = false,
            readOnly = !isEditMode
        )

        if (isEditMode) {
            Button(onClick = {
                val renderableGraph = RenderableGraph()

                for (line in Console.text.splitToSequence('\n')) {
                    val cmd = line.split(' ')
                    when (cmd[0]) {
                        "v", "vertex" ->
                            if (cmd.size >= 2) {
                                val vertex = renderableGraph.addVertex(cmd[1])
                            }

                        "e", "edge" ->
                            if (cmd.size >= 3) {
                                val weight = if (cmd.size <= 3) 1 else cmd[3].toIntOrNull() ?: 1
                                renderableGraph.addEdge(cmd[1], cmd[2], weight)
                            }
                    }
                }

                renderableGraph.positionVertices(CircleLayout())
                onGraphChange(renderableGraph)
            }, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text("Execute")
            }
        } else {
            Row (modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.CenterHorizontally)) {
                Box (modifier = Modifier.fillMaxHeight().weight(1f)) {
                    Button(onClick = {

                    }, modifier = Modifier.fillMaxSize()) {
                        Text("<")
                    }
                }

                Box (modifier = Modifier.fillMaxHeight().weight(2f).padding(horizontal = 10.dp)) {
                    Button(onClick = {

                    }, modifier = Modifier.fillMaxSize()) {
                        Text("Run")
                    }
                }

                Box (modifier = Modifier.fillMaxHeight().weight(1f)) {
                    Button(onClick = {

                    }, modifier = Modifier.fillMaxSize()) {
                        Text(">")
                    }
                }
            }
        }
    }
}