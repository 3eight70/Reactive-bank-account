package com.learning.reactive

import com.learning.reactive.atm.AtmApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<AtmApplication>().with(TestcontainersConfiguration::class).run(*args)
}
