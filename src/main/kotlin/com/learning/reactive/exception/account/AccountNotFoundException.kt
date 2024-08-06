package com.learning.reactive.exception.account

import com.learning.reactive.exception.common.NotFoundException
import java.util.*

class AccountNotFoundException(id: UUID) : NotFoundException("Счет с идентификатором: $id не найден") {
}