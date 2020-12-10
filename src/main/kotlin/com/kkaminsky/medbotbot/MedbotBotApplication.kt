package com.kkaminsky.medbotbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.ApiContextInitializer

@SpringBootApplication
class MedbotBotApplication

fun main(args: Array<String>) {
	ApiContextInitializer.init()
	runApplication<MedbotBotApplication>(*args)
}
