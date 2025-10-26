package mapmates.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun DashedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Int = 3,
    totalNumberOfBars: Int = 4,
    gapWidth: Dp = 10.dp,
    activeStrokeWidth: Dp = 13.dp,
    inactiveStrokeWidth: Dp = 9.dp,
    cap: StrokeCap = StrokeCap.Round,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = color.copy(alpha = 0.5f)
) {
    val bars = max(1, totalNumberOfBars)
    val filled = progress.coerceIn(0, bars)

    Canvas(modifier = modifier) {
        val gapPx = gapWidth.toPx()
        val w = size.width
        val hCenter = size.height / 2f
        val activePx = activeStrokeWidth.toPx()
        val inactivePx = inactiveStrokeWidth.toPx()

        val slotsGaps = (bars - 1).coerceAtLeast(0) * gapPx
        val slotWidth = if (bars == 0) 0f else (w - slotsGaps) / bars

        var x = 0f
        repeat(bars) { i ->
            val isActive = i < filled
            val stroke = if (isActive) activePx else inactivePx
            val segColor = if (isActive) color else trackColor
            val r = when (cap) {
                StrokeCap.Butt -> 0f
                else -> stroke / 2f
            }

            val startX = x + r
            val endX = x + slotWidth - r

            if (endX > startX) {
                drawLine(
                    color = segColor,
                    start = Offset(startX, hCenter),
                    end = Offset(endX, hCenter),
                    strokeWidth = stroke,
                    cap = cap
                )
            }

            x += slotWidth + gapPx
        }
    }
}