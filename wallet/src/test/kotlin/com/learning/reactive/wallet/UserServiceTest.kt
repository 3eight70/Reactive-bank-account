package com.learning.reactive.wallet

import com.learning.reactive.wallet.dto.user.AuthResponseDto
import com.learning.reactive.wallet.dto.user.LoginUserRequestDto
import com.learning.reactive.wallet.dto.user.RegisterUserRequestDto
import com.learning.reactive.wallet.exception.common.CustomTimeoutException
import com.learning.reactive.wallet.exception.user.UserAlreadyExistsException
import com.learning.reactive.wallet.models.User
import com.learning.reactive.wallet.repository.reactive.ReactiveUserRepository
import com.learning.reactive.wallet.security.SecurityService
import com.learning.reactive.wallet.security.TokenDetails
import com.learning.reactive.wallet.service.UserService
import com.learning.reactive.wallet.service.impl.UserServiceImpl
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(classes = [TestcontainersConfiguration::class])
class UserServiceTest {
    private val userRepository: ReactiveUserRepository = mock()
    private val passwordEncoder = mock<PasswordEncoder>()
    private val securityService: SecurityService = mock()

    private val userService: UserService = UserServiceImpl(
        retries = 3,
        timeout = 5,
        userRepository = userRepository,
        securityService = securityService,
        passwordEncoder = passwordEncoder
    )

    @BeforeAll
    fun setUp() {
        whenever(passwordEncoder.encode(any())).thenReturn("encoded_password")
    }

    @Container
    private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:14-alpine")
        .withDatabaseName("testdb")
        .withUsername("user")
        .withPassword("password")

    @Test
    fun `should register a new user`() {
        val dto = RegisterUserRequestDto("test", "test@test.com", "password1")

        val login = dto.login ?: throw IllegalArgumentException("Login cannot be null")
        val email = dto.email ?: throw IllegalArgumentException("Email cannot be null")

        whenever(userRepository.findByEmail(email)).thenReturn(Mono.empty())
        whenever(userRepository.findByLogin(login)).thenReturn(Mono.empty())
        whenever(userRepository.save(any()))
            .thenReturn(Mono.just(User(UUID.randomUUID(), login, email, "encoded")))

        val userDto = userService.registerUser(dto)

        StepVerifier.create(userDto)
            .expectNextMatches { user ->
                user.email == email && user.login == login
            }
            .verifyComplete()
    }

    @Test
    fun `should handle user already exists by email`() {
        val dto = RegisterUserRequestDto("test", "test@test.com", "password1")
        val existingUser = User(UUID.randomUUID(), "test", "test@test.com", "password1")

        whenever(userRepository.findByEmail(dto.email!!)).thenReturn(Mono.just(existingUser))

        val userDto = userService.registerUser(dto)

        StepVerifier.create(userDto)
            .expectError(UserAlreadyExistsException::class.java)
            .verify()
    }

    @Test
    fun `should handle user already exists by login`() {
        val dto = RegisterUserRequestDto("test", "test@test.com", "password1")
        val existingUser = User(UUID.randomUUID(), "test", "test@test.com", "password1")

        whenever(userRepository.findByLogin(dto.login!!)).thenReturn(Mono.just(existingUser))

        val userDto = userService.registerUser(dto)

        StepVerifier.create(userDto)
            .expectError(UserAlreadyExistsException::class.java)
            .verify()
    }

    @Test
    fun `should login user`() {
        val dto = LoginUserRequestDto("test", "test1")
        val authResponse = AuthResponseDto(UUID.randomUUID(), "token", Date(), Date())

        whenever(securityService.authenticate(dto.login!!, dto.password!!))
            .thenReturn(
                Mono.just(
                    TokenDetails(
                        UUID.randomUUID(), authResponse.token, authResponse.issuedAt, authResponse.expiresAt
                    )
                )
            )

        val authResponseDto = userService.loginUser(dto)

        StepVerifier.create(authResponseDto)
            .expectNextMatches {response ->
                response.token == authResponse.token
            }
            .verifyComplete()
    }

    @Test
    fun `should handle timeout error`() {
        val dto = RegisterUserRequestDto("test", "test@test.com", "password")

        whenever(userRepository.findByEmail(dto.email!!)).thenReturn(Mono.never())

        val userDtoMono = userService.registerUser(dto)

        StepVerifier.create(userDtoMono)
            .expectError(CustomTimeoutException::class.java)
            .verify(Duration.ofSeconds(6))
    }
}