package mapmates.core.navigation.api

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface Navigator {

    val destinations: Flow<Event>

    fun navigateUp()

    fun navigateTo(
        destination: Destination,
        builder: NavOptionsBuilder.() -> Unit = {},
    )

    sealed interface Event {
        data class ToDestination(
            val destination: Destination,
            val builder: NavOptionsBuilder.() -> Unit = {},
        ) : Event

        object Up : Event
    }
}


@Serializable
data class Destination(val value: String)

