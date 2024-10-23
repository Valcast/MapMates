package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isEmpty()) {
            return Result.Error("Email cannot be empty")
        }

        if (!email.contains("@")) {
            return Result.Error("Invalid email address")
        }


        return userRepository.resetPassword(email)
    }
}