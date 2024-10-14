package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import java.util.Date
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val userRepository: UserRepository, private val modifyUserUseCase: ModifyUserUseCase) {
    suspend operator fun invoke(email: String, password: String): UserResult {
        if (email.isEmpty() || password.isEmpty()) {
            return UserResult.Error("Email and password cannot be empty")
        }

        if (!email.contains("@")) {
            return UserResult.Error("Invalid email address")
        }

        val loginResult = userRepository.loginUser(email, password)

        return when (loginResult) {
            is UserResult.Success -> {
                modifyUserUseCase(UserUpdateData(lastLogin = Date(System.currentTimeMillis())))

                loginResult
            }
            is UserResult.Error -> loginResult
            else -> UserResult.Error("Unknown error")
        }
    }

}