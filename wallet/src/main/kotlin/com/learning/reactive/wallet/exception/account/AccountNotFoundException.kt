package com.learning.reactive.wallet.exception.account

import com.learning.reactive.wallet.exception.common.NotFoundException
import java.util.*

class AccountNotFoundException(id: UUID) : NotFoundException("Счет с идентификатором: $id не найден") {
}