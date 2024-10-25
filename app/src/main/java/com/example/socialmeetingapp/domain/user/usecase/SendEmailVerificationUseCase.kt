package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.sendEmailVerification()
    }
}