package com.learning.reactive.wallet

import org.springframework.boot.fromApplication


fun main(args: Array<String>) {
	fromApplication<WalletApplication>().with(TestcontainersConfiguration::class.java).run(*args)
}
