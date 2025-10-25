package mapmates.core.navigation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mapmates.core.navigation.api.Navigator
import mapmates.core.navigation.impl.NavigatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NavigationModule {

    @Provides
    @Singleton
    fun provideNavigator(): Navigator = NavigatorImpl()
}