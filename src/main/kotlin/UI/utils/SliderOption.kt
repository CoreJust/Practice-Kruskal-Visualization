/*
* UI.utils.SliderOption.kt
* Contains the SliderOption composable function that implements an option with a name,
* a slider and a custom content to the right of the slider.
*/

package UI.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SliderOption(
    name: String,
    defaultValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    additionalContent: (@Composable RowScope.() -> Unit)? = null
) {
    assert(defaultValue in valueRange)

    var value by remember { mutableStateOf(defaultValue) }

    Row (modifier = modifier)  {
       Text(name)
       Slider(
           value = value,
           onValueChange = {
               onValueChange(it)
               value = it
           },
           valueRange = valueRange
       )

       if (additionalContent != null) {
           additionalContent()
       }
    }
}