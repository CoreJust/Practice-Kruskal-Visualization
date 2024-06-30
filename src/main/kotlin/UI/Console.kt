/*
* UI.Console.kt
* Contains the implementation of ConsoleUI function that represents the text console widget to the right of the graph window
* and the implementation of the Console class that provides API to work with the console (specifically, to print there).
* Can be in 2 modes: edit mode where it can accept user input and algorithm mode where it can only show information.
*/

package UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
fun RowScope.ConsoleUI(isEditMode: Boolean) {
    val algorithmAlertDialogHelper by remember { mutableStateOf(AlertDialogHelper()) }
    val algorithmRunner by remember { mutableStateOf(AlgorithmRunner(algorithmAlertDialogHelper)) }

    // Reloading the algorithm runner if necessary
    if (isEditMode != Console.lastEditMode) {
        if (!isEditMode) {
            algorithmRunner.initAlgorithm()
        } else {
            algorithmRunner.destroyAlgorithm()
        }
    }

    // Changing console mode
    Console.setEditMode(isEditMode)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .padding(start = 10.dp)
    ) {
        OutlinedTextField(
            value = Console.text,
            onValueChange = { if (isEditMode) Console.text = it },
            label = { Text("Console") },
            singleLine = false,
            readOnly = !isEditMode,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(10f)
                .padding(bottom = 4.dp)
        )

        val buttonRoundness = 9.dp
        val bottomButtonPadding = 6.dp
        if (isEditMode) {
            Button(onClick = {
                val renderableGraph = RenderableGraph()

                for (line in Console.text.splitToSequence('\n')) {
                    val cmd = line.split(' ')
                    when (cmd[0]) {
                        "v", "vertex" ->
                            if (cmd.size >= 2) {
                                renderableGraph.addVertex(cmd[1])
                            }

                        "e", "edge" ->
                            if (cmd.size >= 3) {
                                val weight = if (cmd.size <= 3) 1 else cmd[3].toIntOrNull() ?: 1
                                renderableGraph.addEdge(cmd[1], cmd[2], weight)
                            }
                    }
                }

                renderableGraph.positionVertices(CircleLayout())
                GraphView.onGraphChange(renderableGraph)
            }, modifier = Modifier
                .padding(bottom = bottomButtonPadding)
                .clip(shape = RoundedCornerShape(buttonRoundness))
                .fillMaxWidth()
                .weight(1f)
            ) {
                Text("Execute")
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.CenterHorizontally)) {
                Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                    Button(onClick = {
                        algorithmRunner.stepBack()
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topStart = buttonRoundness, bottomStart = buttonRoundness))
                        .fillMaxSize()
                    ) {
                        Text("<")
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(2f).padding(horizontal = 10.dp)) {
                    Button(onClick = {

                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .fillMaxSize()
                    ) {
                        Text("Run")
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                    Button(onClick = {
                        algorithmRunner.stepForth()
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topEnd = buttonRoundness, bottomEnd = buttonRoundness))
                        .fillMaxSize()
                    ) {
                        Text(">")
                    }
                }
            }
        }
    }

    // Calling these functions here allow the helpers to be shown
    algorithmAlertDialogHelper.show()
}