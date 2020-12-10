package com.kkaminsky.medbotbot.service

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.generics.LongPollingBot
import java.io.Serializable

interface TelegramBot : LongPollingBot {
    fun <T : Serializable?, Method : BotApiMethod<T>?> executeMethod(method: Method?): T
    fun createKeyboard(buttons: List<List<Pair<Pair<String, String>,Boolean>>>): InlineKeyboardMarkup
}