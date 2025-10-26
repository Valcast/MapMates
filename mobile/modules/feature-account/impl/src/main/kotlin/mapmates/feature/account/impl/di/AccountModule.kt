package mapmates.feature.account.impl.di

import androidx.navigation.compose.composable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import mapmates.core.navigation.api.AppNavGraphBuilder
import mapmates.feature.account.impl.ui.createaccount.CreateAccountScreen

@Module
@InstallIn(SingletonComponent::class)
internal object AccountModule {

    @Provides
    @IntoSet
    fun provideAccountGraph(): AppNavGraphBuilder = AppNavGraphBuilder {
        composable("create_account") {
            CreateAccountScreen()
        }
    }
}