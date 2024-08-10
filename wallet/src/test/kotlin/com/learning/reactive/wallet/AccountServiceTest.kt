package com.learning.reactive.wallet

import com.learning.reactive.common.dto.DepositDto
import com.learning.reactive.wallet.dto.account.AccountBalanceDto
import com.learning.reactive.wallet.dto.account.BankAccountDto
import com.learning.reactive.wallet.dto.transaction.ShortTransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionEnum
import com.learning.reactive.wallet.dto.transaction.TransactionRequestDto
import com.learning.reactive.wallet.exception.account.BadAmountOfMoneyException
import com.learning.reactive.wallet.exception.account.NotEnoughBalanceException
import com.learning.reactive.wallet.exception.common.BadRequestException
import com.learning.reactive.wallet.exception.common.ForbiddenException
import com.learning.reactive.wallet.models.Account
import com.learning.reactive.wallet.models.Transaction
import com.learning.reactive.wallet.repository.reactive.ReactiveAccountRepository
import com.learning.reactive.wallet.repository.reactive.ReactiveTransactionRepository
import com.learning.reactive.wallet.security.CustomPrincipal
import com.learning.reactive.wallet.service.impl.AccountServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(classes = [TestcontainersConfiguration::class])
class AccountServiceTest {
    private val accountRepository: ReactiveAccountRepository = mock()
    private val transactionRepository: ReactiveTransactionRepository = mock()
    private val redisTemplate: ReactiveRedisTemplate<String, AccountBalanceDto> = mock()

    private val accountService = AccountServiceImpl(
        accountRepository = accountRepository,
        retries = 3,
        timeout = 5,
        redisTemplate = redisTemplate,
        transactionRepository = transactionRepository
    )

    @Test
    fun `should get all accounts for the user`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test")
        val account1 = Account(UUID.randomUUID(), userId, BigDecimal.ZERO)
        val account2 = Account(UUID.randomUUID(), userId, BigDecimal.TEN)
        val accountDto1 = BankAccountDto(account1.id)
        val accountDto2 = BankAccountDto(account2.id)

        whenever(accountRepository.findAllByUserId(userId)).thenReturn(Flux.just(account1, account2))

        val result = accountService.getAccounts(principal)

        StepVerifier.create(result)
            .expectNext(accountDto1)
            .expectNext(accountDto2)
            .verifyComplete()
    }

    @Test
    fun `should create a new account`() {
        val userId = UUID.randomUUID()
        val account = Account(UUID.randomUUID(), userId, BigDecimal.ZERO)
        val accountDto = BankAccountDto(account.id)

        whenever(accountRepository.save(any<Account>())).thenReturn(Mono.just(account))

        val result = accountService.createAccount(CustomPrincipal(userId, "test", "test@test.com"))

        StepVerifier.create(result)
            .expectNext(accountDto)
            .verifyComplete()
    }

    @Test
    fun `should transfer money between accounts`() {
        val userId = UUID.randomUUID()
        val accountIdFrom = UUID.randomUUID()
        val accountIdWhere = UUID.randomUUID()
        val amount = BigDecimal.TEN

        val accountFrom = Account(accountIdFrom, userId, BigDecimal.valueOf(100))
        val accountWhere = Account(accountIdWhere, userId, BigDecimal.ZERO)
        val transactionRequest = TransactionRequestDto(amount)

        val valueOperations: ReactiveValueOperations<String, AccountBalanceDto> = mock()

        whenever(accountRepository.findById(accountIdFrom)).thenReturn(Mono.just(accountFrom))
        whenever(accountRepository.findById(accountIdWhere)).thenReturn(Mono.just(accountWhere))
        whenever(accountRepository.save(any<Account>()))
            .thenReturn(
                Mono.just(accountFrom.copy(balance = accountFrom.balance.subtract(amount))),
                Mono.just(accountWhere.copy(balance = accountWhere.balance.add(amount)))
            )

        whenever(transactionRepository.save(any<Transaction>())).thenReturn(
            Mono.just(
                Transaction(
                    UUID.randomUUID(),
                    accountIdFrom,
                    accountIdWhere,
                    amount
                )
            )
        )

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        whenever(valueOperations.set(any<String>(), any<AccountBalanceDto>()))
            .thenReturn(Mono.just(true))

        val result = accountService.transferMoney(
            CustomPrincipal(userId, "test", "test@test.com"),
            transactionRequest,
            accountIdFrom,
            accountIdWhere
        )

        StepVerifier.create(result)
            .expectNextMatches {
                it.amount == amount && it.accountIdWhere == accountIdWhere && it.accountIdFrom == accountIdFrom
            }
            .verifyComplete()
    }

    @Test
    fun `should get account history`() {
        val accountId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val transaction = Transaction(
            UUID.randomUUID(),
            null,
            accountId,
            BigDecimal.TEN,
            LocalDateTime.now()
        )
        val shortTransaction = ShortTransactionDto(
            transaction.id,
            transaction.amount,
            transaction.timestamp,
            TransactionEnum.DEPOSIT
        )

        whenever(accountRepository.findById(accountId)).thenReturn(
            Mono.just(
                Account(
                    accountId,
                    userId,
                    BigDecimal.ZERO
                )
            )
        )

        whenever(transactionRepository.findAllByAccountId(accountId)).thenReturn(Flux.just(transaction))

        val result = accountService.accountHistory(principal, accountId)

        StepVerifier.create(result)
            .expectNext(shortTransaction)
            .verifyComplete()
    }

    @Test
    fun `should get transaction information`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val transaction = Transaction(transactionId, accountId, UUID.randomUUID(), BigDecimal.TEN, LocalDateTime.now())
        val transactionDto = TransactionDto(
            transaction.id,
            transaction.accountIdFrom!!,
            transaction.accountIdWhere,
            transaction.amount,
            transaction.timestamp,
            TransactionEnum.TRANSFER
        )

        whenever(accountRepository.findById(accountId)).thenReturn(
            Mono.just(
                Account(
                    accountId,
                    userId,
                    BigDecimal.ZERO
                )
            )
        )
        whenever(transactionRepository.findById(transactionId)).thenReturn(Mono.just(transaction))

        val result = accountService.getTransactionInfo(
            CustomPrincipal(userId, "test", "test@test.com"),
            accountId,
            transactionId
        )

        StepVerifier.create(result)
            .expectNext(transactionDto)
            .verifyComplete()
    }

    @Test
    fun `should check account balance from Redis`() {
        val accountId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val balance = BigDecimal.valueOf(100)

        val valueOperations: ReactiveValueOperations<String, AccountBalanceDto> = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        whenever(redisTemplate.hasKey(accountId.toString())).thenReturn(Mono.just(true))
        whenever(valueOperations.get(accountId.toString())).thenReturn(
            Mono.just(
                AccountBalanceDto(
                    userId,
                    accountId,
                    balance
                )
            )
        )

        val result = accountService.checkAccountBalance(
            CustomPrincipal(
                userId,
                "test",
                "test@test.com"
            ),
            accountId
        )

        StepVerifier.create(result)
            .expectNext(balance)
            .verifyComplete()
    }

    @Test
    fun `should process a deposit`() {
        val accountId = UUID.randomUUID()
        val depositAmount = BigDecimal.valueOf(100)
        val depositDto = DepositDto(accountId, depositAmount)
        val account = Account(accountId, UUID.randomUUID(), BigDecimal.ZERO)

        val valueOperations: ReactiveValueOperations<String, AccountBalanceDto> = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        whenever(valueOperations.set(eq(accountId.toString()), any<AccountBalanceDto>())).thenReturn(Mono.just(true))

        whenever(accountRepository.findById(accountId)).thenReturn(Mono.just(account))
        whenever(accountRepository.save(any<Account>())).thenReturn(Mono.just(account.copy(balance = depositAmount)))
        whenever(transactionRepository.save(any<Transaction>())).thenReturn(
            Mono.just(
                Transaction(
                    UUID.randomUUID(),
                    null,
                    accountId,
                    depositAmount,
                    LocalDateTime.now()
                )
            )
        )

        val result = accountService.processDeposit(depositDto)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `should throw BadRequestException when transferring money to the same account`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val accountId = UUID.randomUUID()

        val transactionRequestDto = TransactionRequestDto(BigDecimal.TEN)

        val result = accountService.transferMoney(
            principal,
            transactionRequestDto,
            accountId,
            accountId
        )

        StepVerifier.create(result)
            .expectError(BadRequestException::class.java)
            .verify()
    }

    @Test
    fun `should throw BadAmountOfMoneyException when transferring money equals zero`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val accountIdFrom = UUID.randomUUID()
        val accountIdWhere = UUID.randomUUID()

        val transactionRequestDto = TransactionRequestDto(BigDecimal.ZERO)

        whenever(accountRepository.findById(accountIdFrom)).thenReturn(
            Mono.just(
                Account(
                    accountIdFrom,
                    userId,
                    BigDecimal.TEN
                )
            )
        )
        whenever(accountRepository.findById(accountIdWhere)).thenReturn(
            Mono.just(
                Account(
                    accountIdWhere,
                    userId,
                    BigDecimal.TEN
                )
            )
        )

        val result = accountService.transferMoney(
            principal,
            transactionRequestDto,
            accountIdFrom,
            accountIdWhere
        )

        StepVerifier.create(result)
            .expectError(BadAmountOfMoneyException::class.java)
            .verify()
    }

    @Test
    fun `should throw NotEnoughBalanceException when balance is insufficient`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val accountIdFrom = UUID.randomUUID()
        val accountIdWhere = UUID.randomUUID()
        val amount = BigDecimal.TEN

        val accountFrom = Account(accountIdFrom, userId, BigDecimal.ZERO)
        val accountWhere = Account(accountIdWhere, userId, BigDecimal.ZERO)
        val transactionRequestDto = TransactionRequestDto(amount)

        whenever(accountRepository.findById(accountIdFrom)).thenReturn(Mono.just(accountFrom))
        whenever(accountRepository.findById(accountIdWhere)).thenReturn(Mono.just(accountWhere))

        val result = accountService.transferMoney(
            principal,
            transactionRequestDto,
            accountIdFrom,
            accountIdWhere
        )

        StepVerifier.create(result)
            .expectError(NotEnoughBalanceException::class.java)
            .verify()
    }

    @Test
    fun `should throw ForbiddenException when accessing history of account not owned by user`() {
        val userId = UUID.randomUUID()
        val wrongUserId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val accountId = UUID.randomUUID()
        val wrongAccount = Account(accountId, wrongUserId, BigDecimal.ZERO)

        whenever(accountRepository.findById(accountId)).thenReturn(Mono.just(wrongAccount))

        val result = accountService.accountHistory(principal, accountId)

        StepVerifier.create(result)
            .expectError(ForbiddenException::class.java)
            .verify()
    }

    @Test
    fun `should return account balance from repository when Redis is not available`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test@test.com")
        val accountId = UUID.randomUUID()
        val accountBalance = BigDecimal.TEN

        val account = Account(accountId, userId, accountBalance)

        val valueOperations: ReactiveValueOperations<String, AccountBalanceDto> = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        whenever(valueOperations.set(eq(accountId.toString()),
            any<AccountBalanceDto>())).thenReturn(Mono.just(false))

        whenever(redisTemplate.hasKey(accountId.toString())).thenReturn(Mono.just(false))
        whenever(accountRepository.findById(accountId)).thenReturn(Mono.just(account))

        val result = accountService.checkAccountBalance(principal, accountId)

        StepVerifier.create(result)
            .expectNext(accountBalance)
            .verifyComplete()
    }
}