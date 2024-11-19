package com.example.socialmeetingapp

import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.LoginUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var loginUserUseCase: LoginUserUseCase

    @Before
    fun setup() {
        userRepository = mockk()
        loginUserUseCase = LoginUserUseCase(userRepository)
    }

    @Test
    fun `should return Result Error when email is empty`() = runTest {
        val email = ""
        val password = "password"
        val result = loginUserUseCase(email, password)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email and password cannot be empty")
    }

    @Test
    fun `should return Result Error when password is empty`() = runTest {
        val email = "email"
        val password = ""
        val result = loginUserUseCase(email, password)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email and password cannot be empty")
    }

    @Test
    fun `should return Result Error when email is invalid`() = runTest {
        val email = "email"
        val password = "password"

        val result = loginUserUseCase(email, password)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Invalid email")
    }

    @Test
    fun `should return Result Success when email and password are valid`() = runTest {
        val email = "user@example.com"
        val password = "password"

        coEvery { userRepository.loginUser("user@example.com", "password") } returns Result.Success(Unit)

        val result = loginUserUseCase(email, password)

        assert(result is Result.Success)
    }

    @Test
    fun `should return Result Error when login fails`() = runTest {
        val email = "user@example.com"
        val password = "password"

        coEvery { userRepository.loginUser("user@example.com", "password") } returns Result.Error("Email address or password is incorrect")

        val result = loginUserUseCase(email, password)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "Email address or password is incorrect")
        }

    @Test
    fun `should return Result Error when no internet connection`() = runTest {
        val email = "user@example.com"
        val password = "password"

        coEvery { userRepository.loginUser("user@example.com", "password") } returns Result.Error("No internet connection")

        val result = loginUserUseCase(email, password)

        assert(result is Result.Error)
        assert((result as Result.Error).message == "No internet connection")

    }
}