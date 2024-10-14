package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import java.sql.Date
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val userRepository: UserRepository, private val modifyUserUseCase: ModifyUserUseCase) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): UserResult {
        if (email.isEmpty() || password.isEmpty()) {
            return UserResult.Error("Email and password cannot be empty")
        }

        if (!email.contains("@")) {
            return UserResult.Error("Invalid email address")
        }

        if (password != confirmPassword) {
            return UserResult.Error("Passwords do not match")
        }

        if (password.length < 6) {
            return UserResult.Error("Password must be at least 6 characters long")
        }

        if (!password.any { it.isDigit() }) {
            return UserResult.Error("Password must contain at least one digit")
        }

        if (!password.any { it.isUpperCase() }) {
            return UserResult.Error("Password must contain at least one uppercase letter")
        }

        if (!password.any { it.isLowerCase() }) {
            return UserResult.Error("Password must contain at least one lowercase letter")
        }

        if (!password.any { !it.isLetterOrDigit() }) {
            return UserResult.Error("Password must contain at least one special character")
        }

        val registerResult = userRepository.registerUser(email, password)

        return when (registerResult) {
            is UserResult.Success -> {
                modifyUserUseCase(UserUpdateData(createdAt = Date(System.currentTimeMillis()), lastLogin = Date(System.currentTimeMillis())))
                UserResult.Success

            }
            is UserResult.Error -> registerResult
            else -> UserResult.Error("Unknown error")

        }
    }
}