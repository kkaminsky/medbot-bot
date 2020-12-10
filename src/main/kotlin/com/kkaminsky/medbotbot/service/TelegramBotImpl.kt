package com.kkaminsky.medbotbot.service


import com.kkaminsky.medbotbot.config.TelegramClientConfig
import com.kkaminsky.medbotbot.dto.PlatformType
import com.kkaminsky.medbotbot.dto.ReceiptIdDto
import com.kkaminsky.medbotbot.dto.SyncAccountDto
import com.kkaminsky.medbotbot.dto.UserPlatformInfoDto
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import java.io.Serializable
import java.time.Instant
import java.util.*

@Service
@EnableConfigurationProperties(TelegramClientConfig::class)
class TelegramBotImpl constructor(
        private val telegramClientConfig: TelegramClientConfig,
        private val restTemplateService: RestTemplateService
) : TelegramBot, TelegramLongPollingBot() {



    override fun getBotUsername(): String {
        return telegramClientConfig.username
    }

    override fun getBotToken(): String {
        return telegramClientConfig.token
    }


    override fun <T : Serializable?, Method : BotApiMethod<T>?> executeMethod(method: Method?): T {
        try {
            return execute(method)
        } catch (e: TelegramApiRequestException) {
            throw Exception("Ошибка при обращении к апи телеграмма: " + e.apiResponse + ". " + e.localizedMessage)
        }
    }

    override fun createKeyboard(buttons: List<List<Pair<Pair<String, String>,Boolean>>>): InlineKeyboardMarkup {
        val markup = InlineKeyboardMarkup()
        markup.keyboard = buttons.map {listBotButton ->
            listBotButton.map { botButton ->
                val button = InlineKeyboardButton()
                if (botButton.second) {
                    button.url = botButton.first.second
                } else {
                    button.callbackData = botButton.first.second
                }
                button.text = botButton.first.first
                button
            }
        }
        return markup
    }

    data class TextReportDto(
            val text: String
    )

    override fun onUpdateReceived(p0: Update?) {
        if (p0!!.message != null) {
            if(p0.message.text == "/start" || p0.message.text == "Начать"){
                val res = restTemplateService.doPostRequest(
                        uri = "http://localhost:8787/api/v1/bot/user/sync-code",
                        payload = UserPlatformInfoDto(
                                id = p0.message.from.id.toString(),
                                platform = PlatformType.TELEGRAM,
                                username = p0.message.from.userName
                        ),
                        responseType = String::class.java
                )
                executeMethod(SendMessage(
                        p0.message.from.id.toLong(),
                        "Добро пожаловать в медицинский ассистент. Для начала пользования необходимо зарегистрироваться."
                ).also {
                    it.replyMarkup = createKeyboard(listOf(listOf(("Войти" to "http://127.0.0.1:8080/sync/platform?code=$res") to true)))
                })
            }
            if (p0.message.text.contains("/report")){
                val res = restTemplateService.doPostRequest(
                        uri = "http://localhost:8787/api/v1/bot/save/report/text",
                        payload = TextReportDto(
                                text = p0.message.text.replace("/report","")
                        ),
                        responseType = UUID::class.java
                )
                executeMethod(SendMessage(
                        p0.message.from.id.toLong(),
                        "Отчет принят"
                ))
            }
        }
        if (p0.callbackQuery != null) {
            if (p0.callbackQuery.data.contains("YES")){
                val res = restTemplateService.doPostRequest(
                        uri = "http://localhost:8787/api/v1/bot/bind",
                        payload = SyncAccountDto(
                                username = p0.callbackQuery.from.userName,
                                userMainId = UUID.fromString(p0.callbackQuery.data.split('_')[1]),
                                platformId = p0.callbackQuery.from.id.toString(),
                                platformType = PlatformType.TELEGRAM
                        ),
                        responseType = UUID::class.java
                )
                executeMethod(DeleteMessage(p0.callbackQuery.from.id.toLong(),p0.callbackQuery.message.messageId))
                executeMethod(SendMessage(
                        p0.callbackQuery.from.id.toLong(),
                        "Бот активирован и будет уведомлять вас, когда вам нужно будет принимать лекарства"
                ))
            }
            if (p0.callbackQuery.data.contains("FORGOT")){
                executeMethod(SendMessage(
                        p0.callbackQuery.from.id.toLong(),
                        "Впредь не забывй"
                ))
            }
            if (p0.callbackQuery.data.contains("CONSUME")){
                val res = restTemplateService.doPostRequest(
                        uri = "http://localhost:8787/api/v1/bot/save/report",
                        payload = ReceiptIdDto(
                                receiptId = UUID.fromString(p0.callbackQuery.data.split("_")[1])
                        ),
                        responseType = UUID::class.java
                )
                executeMethod(DeleteMessage(
                        p0.callbackQuery.from.id.toLong(),
                        p0.callbackQuery.message.messageId
                ))
                executeMethod(SendMessage(
                        p0.callbackQuery.from.id.toLong(),
                        "Отлично"
                ))
            }
        }
    }
}