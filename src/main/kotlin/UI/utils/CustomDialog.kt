/*
* UI.utils.CustomDialog.kt
* Contains the implementation of CustomDialog composable function that allows to conveniently create a dialog
* with custom contents.
*/

package UI.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onEnterPress: (() -> Unit)? = null,
    dismissible: Boolean = false,
    alignCenterVertically: Boolean = false,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    content: @Composable LazyItemScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = dismissible, dismissOnBackPress = dismissible)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            border = BorderStroke(width = 3.dp, color = Color.Blue),
            modifier = Modifier
                .padding(8.dp)
                .width(width)
                .height(height)
                .onPreviewKeyEvent {
                    if (onEnterPress != null && it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        onEnterPress()
                        true
                    } else {
                        false
                    }
                }
        ) {
            if (!alignCenterVertically) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(9.dp)
                ) {
                    item { content() }
                }
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    LazyColumn (horizontalAlignment = Alignment.CenterHorizontally) {
                        item { content() }
                    }
                }
            }
        }
    }
}