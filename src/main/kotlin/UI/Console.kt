/*
* UI.Console.kt
* Contains the implementation of ConsoleUI function that represents the text console widget to the right of the graph window
* and the implementation of the Console class that provides API to work with the console (specifically, to print there).
* Can be in 2 modes: edit mode where it can accept user input and algorithm mode where it can only show information.
*/

package UI

import CommandProcessor
import UI.dialogs.AlertDialogHelper
import algorithm.AlgorithmOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Class with static API for simulating console
class Console {
    companion object {
        internal var textRenderTrigger by mutableStateOf(0)
        internal var text = AnnotatedString.Builder("")
        internal var lastEditMode = true

        fun print(str: String, color: Color = Color.Blue) {
            text.withStyle(SpanStyle(color = color)) {
                append(str)
            }

            textRenderTrigger += 1
        }

        fun println(str: String, color: Color = Color.Blue) {
            text.withStyle(SpanStyle(color = color)) {
                append(str + '\n')
            }

            textRenderTrigger += 1
        }

        fun clear() {
            text = AnnotatedString.Builder("")
            textRenderTrigger += 1
        }

        internal fun setEditMode(isEditMode: Boolean) {
            if (isEditMode != lastEditMode) {
                clear()
                lastEditMode = isEditMode
            }
        }

        internal fun getTextFieldValue(): TextFieldValue {
            val annotatedString = text.toAnnotatedString()
            return TextFieldValue(
                annotatedString = annotatedString,
                selection = TextRange(annotatedString.length)
            )
        }
    }
}

// The composable function that displays the actual console
@Composable
fun RowScope.ConsoleUI(isEditMode: Boolean, onModeChangeFailure: () -> Unit) {
    var isAlgorithmRunning by remember { mutableStateOf(false) }

    // Reloading the algorithm runner if necessary
    if (isEditMode != Console.lastEditMode) {
        if (!isEditMode) {
            if (!AlgorithmRunner.initAlgorithm()) {
                onModeChangeFailure()
                return
            }
        } else {
            AlgorithmRunner.destroyAlgorithm()
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
            value = if (Console.textRenderTrigger < 0) TextFieldValue() else Console.getTextFieldValue(),
            onValueChange = {
                if (isEditMode) { // Executing commands on enter
                    Console.text = AnnotatedString.Builder(it.text)
                    Console.textRenderTrigger += 1
                }
            },
            label = { Text("Console") },
            singleLine = false,
            readOnly = !isEditMode,
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(fontSize = if (Console.lastEditMode) 16.sp else AlgorithmOptions.consoleTextSize.sp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(10f)
                .padding(bottom = 4.dp)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        val text = Console.text.toAnnotatedString().toString()
                        Console.print("\n")
                        CommandProcessor.execute(text.substringAfterLast('\n'))

                        true
                    } else {
                        false
                    }
                }
        )

        val buttonRoundness = 9.dp
        val bottomButtonPadding = 6.dp
        if (!isEditMode) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.CenterHorizontally)) {
                Box(modifier = Modifier.fillMaxHeight().weight(1f).padding(horizontal = 5.dp)) {
                    Button(onClick = {
                        isAlgorithmRunning = false
                        AlgorithmRunner.toTheBeginning()
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topStart = buttonRoundness, bottomStart = buttonRoundness))
                        .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("◀", textAlign = TextAlign.Center)
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(1f).padding(horizontal = 3.dp)) {
                    Button(onClick = {
                        if (isAlgorithmRunning) {
                            AlgorithmRunner.decelerate()
                        } else {
                            AlgorithmRunner.stepBack()
                        }
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topStart = buttonRoundness, bottomStart = buttonRoundness))
                        .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("<", textAlign = TextAlign.Center)
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(2.5f).padding(horizontal = 5.dp)) {
                    Button(onClick = {
                        isAlgorithmRunning = !isAlgorithmRunning

                        if (isAlgorithmRunning) {
                            AlgorithmRunner.run()
                        } else {
                            AlgorithmRunner.pause()
                        }
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (isAlgorithmRunning) "Pause" else "Run", textAlign = TextAlign.Center)
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(1f).padding(horizontal = 5.dp)) {
                    Button(onClick = {
                        if (isAlgorithmRunning) {
                            AlgorithmRunner.accelerate()
                        } else {
                            AlgorithmRunner.stepForth()
                        }
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topEnd = buttonRoundness, bottomEnd = buttonRoundness))
                        .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(">", textAlign = TextAlign.Center)
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().weight(1f).padding(horizontal = 3.dp)) {
                    Button(onClick = {
                        isAlgorithmRunning = false
                        AlgorithmRunner.toTheEnd()
                    }, modifier = Modifier
                        .padding(bottom = bottomButtonPadding)
                        .clip(shape = RoundedCornerShape(topEnd = buttonRoundness, bottomEnd = buttonRoundness))
                        .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("▶", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}