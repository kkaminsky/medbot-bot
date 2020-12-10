package com.kkaminsky.medbotbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram")
class TelegramClientConfig {
    lateinit var botId: String
    lateinit var username: String
    lateinit var token: String
}