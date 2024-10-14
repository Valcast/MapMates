package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import java.util.Date
import javax.inject.Inject

class ModifyUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(updateData: UserUpdateData): UserResult {
        if (updateData.bio == null && updateData.username == null && updateData.lastLogin == null && updateData.createdAt == null && updateData.updatedAt == null) {
            return UserResult.Error("No data to update")
        }

        if (updateData.bio != null && updateData.bio.length > 250) {
            return UserResult.Error("Bio is too long")
        }

        if (updateData.username != null && updateData.username.length > 20) {
            return UserResult.Error("Username is too long")
        }


        return userRepository.modifyUser(updateData.copy(updatedAt = Date(System.currentTimeMillis())))
    }

}