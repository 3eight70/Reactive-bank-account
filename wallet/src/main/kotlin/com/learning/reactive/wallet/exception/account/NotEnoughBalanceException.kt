package com.learning.reactive.wallet.exception.account

import com.learning.reactive.wallet.exception.common.BadRequestException

class NotEnoughBalanceException : BadRequestException("На счете недостаточно средств") {
}