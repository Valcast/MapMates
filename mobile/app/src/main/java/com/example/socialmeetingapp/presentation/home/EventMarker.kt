package com.example.socialmeetingapp.presentation.home

import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Category
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class EventMarker(
    private val latLng: LatLng,
    private val title: String,
    private val snippet: String,
    private val category: Category
) : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    override fun getZIndex(): Float {
        return 0f
    }

    fun getCategoryIcon(): Int {
        return when (category) {
            Category.CINEMA -> R.drawable.cinema
            Category.CONCERT -> R.drawable.concert
            Category.CONFERENCE -> R.drawable.conference
            Category.HOUSEPARTY -> R.drawable.houseparty
            Category.MEETUP -> R.drawable.meetup
            Category.THEATER -> R.drawable.theater
            Category.WEBINAR -> R.drawable.webinar
        }
    }
}