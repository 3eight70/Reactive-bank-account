package com.learning.reactive.controller

import com.learning.reactive.dto.account.BankAccountDto
import com.learning.reactive.dto.transaction.ShortTransactionDto
import com.learning.reactive.dto.transaction.TransactionDto
import com.learning.reactive.dto.transaction.TransactionRequestDto
import com.learning.reactive.security.CustomPrincipal
import com.learning.reactive.service.AccountService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Контроллер, отвечающий за работу со счетами
 */
@RestController
@RequestMapping("/api/v1/account")
class AccountController(
    private val accountService: AccountService
) {
    /**
     * Метод, отвечающий за получение потока счетов пользователя
     *
     * @return Flux<BankAccountDto> - поток из дтошек счетов пользователя
     */
    @GetMapping
    fun getAccounts(authentication: Authentication): Flux<BankAccountDto> {
        return accountService.getAccounts(authentication.principal as CustomPrincipal)
    }

    /**
     * Метод, отвечающий за создание счета пользователем
     *
     * @return Mono<BankAccountDto> - dto созданного счета
     */
    @PostMapping
    fun createAccount(authentication: Authentication): Mono<BankAccountDto> {
        return accountService.createAccount(authentication.principal as CustomPrincipal)
    }

    /**
     * Метод, отвечающий за перевод денег со счета на счет пользователем
     *
     * @param accountIdFrom - идентификатор счета, откуда будет производиться перевод
     * @param accountIdWhere - идентификатор счета, куда требуется произвести перевод
     *
     * @return Mono<TransactionDto> - dto проведенной транзакции
     */
    @PostMapping("/transfer")
    fun transferMoney(
        authentication: Authentication,
        @RequestParam("accountIdFrom") accountIdFrom: UUID,
        @RequestParam("accountIdWhere") accountIdWhere: UUID,
        @RequestBody transactionRequestDto: TransactionRequestDto
    ): Mono<TransactionDto> {
        val principal = authentication.principal as CustomPrincipal
        return accountService.transferMoney(principal, transactionRequestDto, accountIdFrom, accountIdWhere)
    }

    /**
     * Метод, отвечающий за получение истории транзакций конкретного счета
     *
     * @param accountId - идентификатор счета
     *
     * @return Flux<ShortTransactionDto> - поток из dto транзакций
     */
    @GetMapping("/history")
    fun accountHistory(
        authentication: Authentication,
        @RequestParam("accountId") accountId: UUID
    ): Flux<ShortTransactionDto> {
        return accountService.accountHistory(authentication.principal as CustomPrincipal, accountId)
    }

    /**
     * Метод, отвечающий за получение информации о конкретной транзакции счета
     *
     * @param accountId - идентификатор счета
     * @param transactionId - идентификатор транзакции
     *
     * @return Mono<TransactionDto> - dto транзакции
     */
    @GetMapping("/transaction/{transactionId}")
    fun getTransactionInfo(
        authentication: Authentication,
        @RequestParam("accountId") accountId: UUID,
        @PathVariable("transactionId") transactionId: UUID
    ): Mono<TransactionDto> {
        return accountService.getTransactionInfo(authentication.principal as CustomPrincipal, accountId, transactionId)
    }
}