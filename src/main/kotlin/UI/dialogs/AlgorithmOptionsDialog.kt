/*
* UI.dialogs.AlgorithmOptionsDialog.kt
* Contains the function AlgorithmOptionsDialogUI that allows to create a dialog with graph rendering options.
* Also, contains a helper class AlgorithmOptionsDialogHelper that allows to create that kind of dialog from
* a non-composable context (by placing its show() function call somewhere within composable context)
*/

package UI.dialogs

import UI.AlgorithmRunner
import UI.utils.CustomDialog
import UI.utils.SliderOption
import algorithm.AlgorithmOptions
import algorithm.AlgorithmTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*

class AlgorithmOptionsDialogHelper {
    companion object {
        private var isOpen by mutableStateOf(false)

        // To be used somewhere in a Composable context
        @Composable
        fun show() {
            if (isOpen) {
                AlgorithmOptionsDialogUI { isOpen = false }
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
private fun AlgorithmOptionsDialogUI(onDismiss: () -> Unit) {
    var isThemeSelectionExpanded by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(AlgorithmOptions.theme) }
    var isChecked by remember { mutableStateOf(AlgorithmOptions.useEdgeWindow) }

    CustomDialog(
        onDismissRequest = { onDismiss() },
        dismissible = true
    ) {
        ExposedDropdownMenuBox(
            expanded = isThemeSelectionExpanded,
            onExpandedChange = { isThemeSelectionExpanded = it }
        ) {
            TextField(
                value = selectedTheme.name.lowercase().replaceFirstChar(Char::uppercase),
                readOnly = true,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isThemeSelectionExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = isThemeSelectionExpanded,
                onDismissRequest = { isThemeSelectionExpanded = false }
            ) {
                AlgorithmTheme.entries.forEach { theme ->
                    DropdownMenuItem(
                        onClick = {
                            selectedTheme = theme
                            AlgorithmOptions.theme = theme
                            isThemeSelectionExpanded = false
                            AlgorithmRunner.onAlgorithmSettingsChange()
                        }
                    ) {
                        Text(theme.name.lowercase().replaceFirstChar(Char::uppercase))
                    }
                }
            }
        }

        SliderOption(
            name = "Console text size: ",
            defaultValue = AlgorithmOptions.consoleTextSize,
            onValueChange = { AlgorithmOptions.consoleTextSize = it },
            valueRange = 9f..28f
        )

        if (isChecked) {
            SliderOption(
                name = "Edge window text size: ",
                defaultValue = AlgorithmOptions.edgeWindowTextSize,
                onValueChange = { AlgorithmOptions.edgeWindowTextSize = it },
                valueRange = 10f..30f
            )
        }

        Row {
            Text(text = "Use edge window: ")
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    AlgorithmOptions.useEdgeWindow = it
                    isChecked = it
                }
            )
        }
    }
}