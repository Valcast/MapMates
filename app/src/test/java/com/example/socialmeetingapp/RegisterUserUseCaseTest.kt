package com.example.socialmeetingapp

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.RegisterUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RegisterUserUseCaseTest {
    val userRepository = mockk<UserRepository>()
    val registerUserUseCase = RegisterUserUseCase(userRepository)

    @Test
    fun `should return Result Error when email is empty`() = runTest {
        val email = ""
        val password = "password"
        val confirmPassword = "password"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email and password cannot be empty")
    }

    @Test
    fun `should return Result Error when password is empty`() = runTest {
        val email = "email"
        val password = ""
        val confirmPassword = "password"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email and password cannot be empty")
    }

    @Test
    fun `should return Result Error when email is invalid`() = runTest {
        val email = "email"
        val password = "password"
        val confirmPassword = "password"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Invalid email address")
    }

    @Test
    fun `should return Result Error when password and confirm password do not match`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val confirmPassword = "password1"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Passwords do not match")
    }

    @Test
    fun `should return Result Error when password is less than 6 characters long`() = runTest {
        val email = "user@example.com"
        val password = "pass"
        val confirmPassword = "pass"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Password must be at least 6 characters long")
    }

    @Test
    fun `should return Result Error when password does not contain a digit`() = runTest {
        val email = "user@example.com"
        val password = "Password"
        val confirmPassword = "Password"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Password must contain at least one digit")
    }

    @Test
    fun `should return Result Error when password does not contain an uppercase letter`() = runTest {
        val email = "user@example.com"
        val password = "passwo2@rd"
        val confirmPassword = "passwo2@rd"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Password must contain at least one uppercase letter")
    }

    @Test
    fun `should return Result Error when password does not contain a lowercase letter`() = runTest {
        val email = "user@example.com"
        val password = "PASS@2WORD"
        val confirmPassword = "PASS@2WORD"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Password must contain at least one lowercase letter")
    }

    @Test
    fun `should return Result Error when password does not contain a special character`() = runTest {
        val email = "user@example.com"
        val password = "Pass2word"
        val confirmPassword = "Pass2word"
        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Password must contain at least one special character")
    }


    @Test
    fun `should return Result Success when email, password and confirm password are valid`() = runTest {
        val email = "user@example.com"
        val password = "8rokMlsP8Wrl!"
        val confirmPassword = "8rokMlsP8Wrl!"

        coEvery { userRepository.registerUser(email, password) } returns Result.Success(Unit)

        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Success)
    }

    @Test
    fun `should return Result Error when registration fails`() = runTest {
        val email = "user@example.com"
        val password = "8rokMlsP8Wrl!"
        val confirmPassword = "8rokMlsP8Wrl!"

        coEvery { userRepository.registerUser(email, password) } returns Result.Error("Unknown error")

        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Unknown error")
    }

    @Test
    fun `should return Result Error when there is no internet connection`() = runTest {
        val email = "user@example.com"
        val password = "8rokMlsP8Wrl!"
        val confirmPassword = "8rokMlsP8Wrl!"

        coEvery {
            userRepository.registerUser(
                email,
                password
            )
        } returns Result.Error("No internet connection")

        val result = registerUserUseCase(email, password, confirmPassword)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "No internet connection")
    }


}