package mapmates.feature.home.impl.di

import androidx.navigation.compose.composable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import mapmates.core.navigation.api.AppNavGraphBuilder
import mapmates.core.navigation.api.Destination
import mapmates.feature.home.impl.ui.HomeScreen

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @IntoSet
    fun provideHomeGraph(): AppNavGraphBuilder = AppNavGraphBuilder {
        composable("home") {
            HomeScreen()
        }
    }
}