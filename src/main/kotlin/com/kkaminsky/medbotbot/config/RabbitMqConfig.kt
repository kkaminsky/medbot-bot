package com.kkaminsky.medbotbot.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@EnableRabbit
@Configuration
class RabbitMqConfig constructor(
        private val connectionFactory: org.springframework.amqp.rabbit.connection.ConnectionFactory
) {

    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = jsonMessageConverter()
        return template
    }

    @Bean
    fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }


    @Bean
    fun testAccountSync(): Queue {
        return Queue("medbot-account-sync")
    }

    @Bean
    fun reportQueue(): Queue {
        return Queue("medbot-report")
    }

}