package com.example.socialmeetingapp.domain.model

import android.net.Uri

data class Category(
    val id: String,
    val iconUrl: Uri
) {
    companion object {
        val EMPTY = Category(
            id = "",
            iconUrl = Uri.EMPTY
        )
    }
}