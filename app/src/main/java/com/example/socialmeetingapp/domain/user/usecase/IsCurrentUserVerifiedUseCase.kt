package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class IsCurrentUserVerifiedUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Boolean {
        return userRepository.isCurrentUserVerified()
    }
}