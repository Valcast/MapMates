package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): UserResult {
        return userRepository.getCurrentUser()
    }
}