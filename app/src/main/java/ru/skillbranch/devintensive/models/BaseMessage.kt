package ru.skillbranch.devintensive.models

import java.util.*

abstract class BaseMessage (
    val id: String,
    val from: User?,
    val chat: Chat?,
    val isIncoming: Boolean = false,
    val date: Date = Date()
) {
    abstract fun formatMessage() :String

    companion object AbstractFactory {

        private var lastId: Int = -1

        fun makeMessage(from: User?, chat: Chat, date: Date = Date(), payload: String, type: String = "text", isIncoming: Boolean = false): BaseMessage {

            lastId++

            return when(type) {
                "image" -> ImageMessage(id = "$lastId", from = from, chat = chat, date = date, image = payload, isIncoming = isIncoming)
                else -> TextMessage(id = "$lastId", from = from, chat = chat, date = date, text = payload, isIncoming = isIncoming)
            }

        }
    }
}