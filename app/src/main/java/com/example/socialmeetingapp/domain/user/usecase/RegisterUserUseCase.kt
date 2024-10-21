package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        if (email.isEmpty() || password.isEmpty()) {
            return Result.Error("Email and password cannot be empty")
        }

        if (!email.contains("@")) {
            return Result.Error("Invalid email address")
        }

        if (password != confirmPassword) {
            return Result.Error("Passwords do not match")
        }

        if (password.length < 6) {
            return Result.Error("Password must be at least 6 characters long")
        }

        if (!password.any { it.isDigit() }) {
            return Result.Error("Password must contain at least one digit")
        }

        if (!password.any { it.isUpperCase() }) {
            return Result.Error("Password must contain at least one uppercase letter")
        }

        if (!password.any { it.isLowerCase() }) {
            return Result.Error("Password must contain at least one lowercase letter")
        }

        if (!password.any { !it.isLetterOrDigit() }) {
            return Result.Error("Password must contain at least one special character")
        }

        return userRepository.registerUser(email, password)
    }
}