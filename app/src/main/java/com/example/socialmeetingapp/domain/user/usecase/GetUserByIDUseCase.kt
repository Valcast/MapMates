package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class GetUserByIDUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): UserResult {
          return userRepository.getUser(id)
    }
}