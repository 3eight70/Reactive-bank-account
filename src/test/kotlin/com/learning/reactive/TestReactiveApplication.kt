package com.learning.reactive

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ReactiveApplication>().with(TestcontainersConfiguration::class).run(*args)
}
