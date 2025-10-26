package mapmates.core.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mapmates.core.ui.theme.MapMatesTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreviews

@Composable
fun MapMatesPreviewTheme(
    forceTheme: Theme? = null,
    content: @Composable () -> Unit
) {
    val resolved = forceTheme ?: Theme.SYSTEM
    MapMatesTheme(theme = resolved, content = content)
}

