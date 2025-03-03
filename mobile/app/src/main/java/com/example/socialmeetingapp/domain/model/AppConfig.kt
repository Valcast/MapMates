package com.example.socialmeetingapp.domain.model

data class AppConfig(
    val theme: Theme,
) {
    companion object {
        val DEFAULT = AppConfig(
            theme = Theme.SYSTEM
        )
    }
}