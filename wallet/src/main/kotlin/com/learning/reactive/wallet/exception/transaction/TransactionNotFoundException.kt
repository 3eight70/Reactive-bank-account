package com.learning.reactive.wallet.exception.transaction

import com.learning.reactive.wallet.exception.common.NotFoundException
import java.util.*

class TransactionNotFoundException(id: UUID) : NotFoundException("Транзакция с id: $id не найдена") {
}