package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class GetUserByIDUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): Result<User> {
          return userRepository.getUser(id)
    }
}