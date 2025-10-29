package mapmates.feature.home.impl.ui

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import mapmates.feature.event.api.filters.Category
import mapmates.feature.home.impl.R as HomeR

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
            Category.CINEMA -> HomeR.drawable.cinema
            Category.CONCERT -> HomeR.drawable.concert
            Category.CONFERENCE -> HomeR.drawable.conference
            Category.HOUSEPARTY -> HomeR.drawable.houseparty
            Category.MEETUP -> HomeR.drawable.meetup
            Category.THEATER -> HomeR.drawable.theater
            Category.WEBINAR -> HomeR.drawable.webinar
        }
    }
}