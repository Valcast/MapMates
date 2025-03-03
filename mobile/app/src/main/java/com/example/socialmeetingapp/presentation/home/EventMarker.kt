package com.example.socialmeetingapp.presentation.home

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class EventMarker(
    private val latLng: LatLng,
    private val title: String,
    private val snippet: String,
    private val eventIconUrl: Uri
): ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    override fun getZIndex(): Float? {
        return 0f
    }

    fun getEventIconUrl(): Uri {
        return eventIconUrl
    }
}