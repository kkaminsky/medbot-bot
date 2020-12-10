package com.kkaminsky.medbotbot.service


import com.kkaminsky.medbotbot.dto.HealthReportDto
import com.kkaminsky.medbotbot.dto.SyncAccountDto
import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage


@EnableRabbit
@Component
class Listener(
        private val telegramBot: TelegramBot
) {


    private val logger = LoggerFactory.getLogger(this.javaClass)


    @RabbitListener(queues = arrayOf("medbot-account-sync"))
    fun receiveQueueResponse(answer: SyncAccountDto, channel: Channel, @Header(AmqpHeaders.DELIVERY_TAG) tag: Long) {
        try {
            telegramBot.executeMethod(SendMessage(
                    answer.platformId.toLong(),
                    "Связать аккаунт с " + answer.username + " ?"
            ).also {
                it.replyMarkup = telegramBot.createKeyboard(
                        listOf(listOf(("Да" to "YES_${answer.userMainId}") to false, ("Нет" to "NO") to false)))
            })
        } catch (ex: java.lang.RuntimeException) {
            channel.basicAck(tag, false)
        }
    }

    @RabbitListener(queues = arrayOf("medbot-report"))
    fun trelloAccountSync(answer: HealthReportDto, channel: Channel, @Header(AmqpHeaders.DELIVERY_TAG) tag: Long) {
        try {
            telegramBot.executeMethod(SendMessage(
                    answer.userPlatformId.toLong(),
                    "Вы приняли лекраство " + answer.drugName + " ?"
            ).also {
                it.replyMarkup = telegramBot.createKeyboard(
                        listOf(
                                listOf(
                                        ("Да" to "CONSUME_${answer.receiptId}") to false,
                                        ("Нет" to "FORGOT_${answer.receiptId}") to false
                                )
                        )
                )
            })

        } catch (ex: java.lang.RuntimeException) {
            channel.basicAck(tag, false)
        }
    }
}