package mapmates.feature.login.impl.di

import android.content.Context
import androidx.navigation.compose.composable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import mapmates.core.navigation.api.AppNavGraphBuilder
import mapmates.feature.login.api.interactor.GetUserAuthenticatedId
import mapmates.feature.login.api.interactor.IsUserAuthenticatedInteractor
import mapmates.feature.login.impl.CredentialManager
import mapmates.feature.login.impl.data.LoginRepository
import mapmates.feature.login.impl.interactor.GetUserAuthenticatedIdImpl
import mapmates.feature.login.impl.interactor.IsUserAuthenticatedInteractorImpl
import mapmates.feature.login.impl.ui.forgotpassword.ForgotPasswordScreen
import mapmates.feature.login.impl.ui.login.LoginScreen
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LoginModule {

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager(context)
    }

    @Provides
    @Singleton
    fun provideIsUserAuthenticatedInteractor(
        loginRepository: LoginRepository
    ): IsUserAuthenticatedInteractor {
        return IsUserAuthenticatedInteractorImpl(loginRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserAuthenticatedIdInteractor(
        loginRepository: LoginRepository
    ): GetUserAuthenticatedId {
        return GetUserAuthenticatedIdImpl(loginRepository)
    }

    @Provides
    @IntoSet
    fun provideAuthGraph(): AppNavGraphBuilder = AppNavGraphBuilder {
        composable("login") {
            LoginScreen()
        }

        composable("forgotPassword") {
            ForgotPasswordScreen()
        }
    }
}