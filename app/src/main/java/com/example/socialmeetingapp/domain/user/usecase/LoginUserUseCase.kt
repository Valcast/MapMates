package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isEmpty() || password.isEmpty()) {
            return Result.Error("Email and password cannot be empty")
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.Error("Invalid email")
        }

        return userRepository.loginUser(email, password)
    }

}