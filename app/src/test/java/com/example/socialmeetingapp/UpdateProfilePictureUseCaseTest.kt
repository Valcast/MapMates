package com.example.socialmeetingapp

import android.content.ContentResolver
import android.net.Uri
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.UploadProfilePictureUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateProfilePictureUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val contentResolver = mockk<ContentResolver>()
    private val updateProfilePictureUseCase = UploadProfilePictureUseCase(userRepository, contentResolver)

    @Test
    fun `should return Result Error when imageUri is empty`() = runTest {
        val imageUri = mockk<Uri>()
        every { imageUri == Uri.EMPTY } returns true

        val result = updateProfilePictureUseCase(imageUri)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Image cannot be empty")
    }

    @Test
    fun `should return Result Error when imageUri scheme is null`() = runTest {
        val imageUri = mockk<Uri>()
        every { imageUri.scheme } returns null

        val result = updateProfilePictureUseCase(imageUri)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Invalid image")
    }

    @Test
    fun `should return Result Error when imageUri scheme is invalid`() = runTest {
        val imageUri = mockk<Uri>()
        every { imageUri.scheme } returns "file"

        val result = updateProfilePictureUseCase(imageUri)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message == "Invalid image")
    }


    @Test
    fun `should return Result Error when imageUri is not an image`() = runTest {
        val imageUri = mockk<Uri>()
        every { imageUri.scheme } returns "content"
        every { contentResolver.getType(imageUri) } returns "application/pdf"

        val result = updateProfilePictureUseCase(imageUri)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Selected file is not an image")
    }

    @Test
    fun `should proceed when imageUri is valid`() = runTest {
        val imageUri = mockk<Uri>()
        every { imageUri.scheme } returns "content"
        every { contentResolver.getType(imageUri) } returns "image/jpeg"

        coEvery { userRepository.updateProfilePicture(imageUri) } returns Result.Success(imageUri)

        val result = updateProfilePictureUseCase(imageUri)

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data == imageUri)
    }


}