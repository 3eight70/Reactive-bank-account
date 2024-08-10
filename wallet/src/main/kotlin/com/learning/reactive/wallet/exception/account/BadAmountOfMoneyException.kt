package com.learning.reactive.wallet.exception.account

import com.learning.reactive.wallet.exception.common.BadRequestException

class BadAmountOfMoneyException : BadRequestException("Введена некорректная сумма денег")