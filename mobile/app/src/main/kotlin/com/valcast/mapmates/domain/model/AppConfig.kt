package com.valcast.mapmates.domain.model

import mapmates.core.ui.Theme

data class AppConfig(
    val theme: Theme,
) {
    companion object {
        val DEFAULT = AppConfig(
            theme = Theme.SYSTEM
        )
    }
}