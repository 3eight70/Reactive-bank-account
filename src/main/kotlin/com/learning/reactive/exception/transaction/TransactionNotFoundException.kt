package com.learning.reactive.exception.transaction

import com.learning.reactive.exception.common.NotFoundException
import java.util.*

class TransactionNotFoundException(id: UUID) : NotFoundException("Транзакция с id: $id не найдена") {
}