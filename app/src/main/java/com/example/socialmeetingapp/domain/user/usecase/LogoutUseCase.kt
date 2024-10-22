package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke() {
        return userRepository.logout()
    }
}