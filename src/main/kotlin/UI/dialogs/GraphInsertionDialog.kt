/*
* UI.dialogs.GraphInsertionDialog.kt
* Contains the function GraphInsertionDialogUI that allows to create a dialog that allows to create
* a graph according to some specific pattern.
* Also, this file contains a helper class GraphInfoDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.GraphView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import graph.RenderableGraph
import graph.Vertex
import graph.layout.CircleLayout

class GraphInsertionDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                GraphInsertionDialogUI({ isOpen = false })
            }
        }

        // To be used out of Composable context to open up a dialog
        fun open() {
            isOpen = true
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GraphInsertionDialogUI(onDismiss: () -> Unit) {
    val graphTypes = listOf("Complete graph", "Null graph", "Cycle graph", "Complete bipartite graph")
    var isGraphTypeSelectionExpanded by remember { mutableStateOf(false) }
    var selectedGraphType by remember { mutableStateOf(graphTypes[0]) }

    var graphSize by remember { mutableStateOf("") }
    var firstPartSize by remember { mutableStateOf("") }
    var secondPartSize by remember { mutableStateOf("") }

    val focusRequester = FocusRequester()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Card (
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            border = BorderStroke(width = 3.dp, color = Color.Blue),
            modifier = Modifier
                .padding(8.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        onConfirmation(
                            selectedGraphType = selectedGraphType,
                            graphSize = graphSize.toIntOrNull() ?: 1,
                            firstPartSize = firstPartSize.toIntOrNull() ?: 2,
                            secondPartSize = secondPartSize.toIntOrNull() ?: 2
                        )
                        onDismiss()

                        true
                    } else {
                        false
                    }
                }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(9.dp)
            ) {
                Text(text = "Create a new graph", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Spacer(Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = isGraphTypeSelectionExpanded,
                    onExpandedChange = { isGraphTypeSelectionExpanded = it }
                ) {
                    TextField(
                        value = selectedGraphType,
                        readOnly = true,
                        onValueChange = { },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGraphTypeSelectionExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = isGraphTypeSelectionExpanded,
                        onDismissRequest = { isGraphTypeSelectionExpanded = false }
                    ) {
                        graphTypes.forEach { graphType ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedGraphType = graphType
                                    isGraphTypeSelectionExpanded = false
                                }
                            ) {
                                Text(graphType)
                            }
                        }
                    }
                }

                if (selectedGraphType == graphTypes[3]) { // Complete bipartite graph
                    OutlinedTextField(
                        value = firstPartSize,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { firstPartSize = it },
                        label = { Text("First part size") },
                        modifier = Modifier.focusRequester(focusRequester)
                    )

                    OutlinedTextField(
                        value = secondPartSize,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { secondPartSize = it },
                        label = { Text("Second part size") }
                    )
                } else {
                    OutlinedTextField(
                        value = graphSize,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { graphSize = it },
                        label = { Text("Number of vertices") },
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                Row {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(7.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onConfirmation(
                                selectedGraphType = selectedGraphType,
                                graphSize = graphSize.toIntOrNull() ?: 1,
                                firstPartSize = firstPartSize.toIntOrNull() ?: 2,
                                secondPartSize = secondPartSize.toIntOrNull() ?: 2
                            )
                            onDismiss()
                        }, modifier = Modifier.padding(7.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

// Does the actual new graph creation
private fun onConfirmation(selectedGraphType: String, graphSize: Int, firstPartSize: Int, secondPartSize: Int) {
    val result = RenderableGraph()
    if (selectedGraphType == "Complete bipartite graph") {
        val firstHalfStep = 1f / firstPartSize
        val firstHalfVertices = List<Vertex>(firstPartSize) { index ->
            result.addVertex("A${index + 1}", Offset(0.2f, firstHalfStep * index))
        }

        val secondHalfStep = 1f / secondPartSize
        val secondHalfVertices = List<Vertex>(secondPartSize) { index ->
            result.addVertex("B${index + 1}", Offset(0.8f, secondHalfStep * index))
        }

        for (firstVertex in firstHalfVertices) {
            for (secondVertex in secondHalfVertices) {
                result.addEdge(firstVertex, secondVertex, 1)
            }
        }
    } else {
        for (i in 1..graphSize) {
            result.addVertex("$i")
        }

        if (selectedGraphType == "Complete graph") {
            for (firstVertex in result.vertices) {
                for (secondVertex in result.vertices) {
                    if (firstVertex.id < secondVertex.id) {
                        result.addEdge(firstVertex, secondVertex, 1)
                    }
                }
            }
        } else if (selectedGraphType == "Cycle graph") {
            result.addEdge("1", "$graphSize", 1)
            for (i in 2..graphSize) {
                result.addEdge("${i - 1}", "$i", 1)
            }
        }

        result.positionVertices(CircleLayout())
    }

    GraphView.onGraphChange(result)
}