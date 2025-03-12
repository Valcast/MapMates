package com.example.socialmeetingapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun DashedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Int = 3,
    totalNumberOfBars: Int = 4
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val barArea = size.width / totalNumberOfBars
        val barLength = barArea - 15.dp.toPx()
        var nextBarStartPosition = 0F

        for (i in 0..totalNumberOfBars) {
            val barStartPosition = nextBarStartPosition
            val barEndPosition = barStartPosition + barLength

            val start = Offset(x = barStartPosition, y = size.height / 2)
            val end = Offset(x = barEndPosition, y = size.height / 2)

            drawLine(
                cap = StrokeCap.Round,
                color = if (i < progress) primaryColor else primaryColor.copy(alpha = .5F),
                start = start,
                end = end,
                strokeWidth = 13F
            )

            nextBarStartPosition = barEndPosition + 10.dp.toPx()
        }
    }
}
