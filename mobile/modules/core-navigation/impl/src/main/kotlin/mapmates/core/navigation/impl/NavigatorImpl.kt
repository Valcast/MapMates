package mapmates.core.navigation.impl

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import mapmates.core.navigation.api.Destination
import mapmates.core.navigation.api.Navigator
import mapmates.core.navigation.api.Navigator.Event

internal class NavigatorImpl : Navigator {

    private val _destinations = Channel<Event>(Channel.CONFLATED)
    override val destinations: Flow<Event> = _destinations.receiveAsFlow()

    override fun navigateUp() {
        _destinations.trySend(Event.Up)
    }

    override fun navigateTo(
        destination: Destination,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        _destinations.trySend(Event.ToDestination(destination, builder))
    }
}
