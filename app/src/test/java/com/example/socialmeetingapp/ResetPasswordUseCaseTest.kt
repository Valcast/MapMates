package com.example.socialmeetingapp

import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.ResetPasswordUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResetPasswordUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val resetPasswordUseCase = ResetPasswordUseCase(userRepository)

    @Test
    fun `should return Result Error when email is empty`() = runTest {
        val email = ""
        val result = resetPasswordUseCase(email)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email cannot be empty")
    }

    @Test
    fun `should return Result Error when email is invalid`() = runTest {
        val email = "email"
        val result = resetPasswordUseCase(email)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Invalid email address")
    }

    @Test
    fun `should return Result Success when email is valid`() = runTest {
        val email = "user@example.com"

        coEvery { userRepository.resetPassword(email) } returns Result.Success(Unit)

        val result = resetPasswordUseCase(email)

        assert(result is Result.Success)
    }

    @Test
    fun `should return Result Error when no internet connection`() = runTest {
        val email = "user@example.com"

        coEvery { userRepository.resetPassword(email) } returns Result.Error("No internet connection")

        val result = resetPasswordUseCase(email)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "No internet connection")

    }

}