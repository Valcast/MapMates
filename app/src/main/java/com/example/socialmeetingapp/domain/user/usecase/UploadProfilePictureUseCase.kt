package com.example.socialmeetingapp.domain.user.usecase

import android.content.ContentResolver
import android.net.Uri
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val contentResolver: ContentResolver
) {
    suspend operator fun invoke(imageUri: Uri): Result<Uri> {

        if (imageUri == Uri.EMPTY) {
            return Result.Error("Image cannot be empty")
        }

        if (imageUri.scheme == null || imageUri.scheme != "content") {
            return Result.Error("Invalid image")
        }

        val mimeType = contentResolver.getType(imageUri)
        if (mimeType?.startsWith("image/") != true) {
            return Result.Error("Selected file is not an image")
        }

        return userRepository.uploadProfilePicture(imageUri)
    }

}