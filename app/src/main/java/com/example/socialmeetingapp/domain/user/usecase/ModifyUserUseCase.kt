package com.example.socialmeetingapp.domain.user.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import javax.inject.Inject

class ModifyUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(updateData: UserUpdateData): Result<Unit> {
        if (updateData.bio == null
            && updateData.username == null
            && updateData.createdAt == null
            && updateData.lastLogin == null
        ) {
            return Result.Error("No data to update")
        }

        if (updateData.bio != null && updateData.bio.length > 250) {
            return Result.Error("Bio is too long")
        }

        if (updateData.username != null && updateData.username.length > 20) {
            return Result.Error("Username is too long")
        }


        return userRepository.modifyUser(updateData.copy(updatedAt = Clock.System.now()))
    }

}