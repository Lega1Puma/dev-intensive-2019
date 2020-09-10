package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.extensions.truncate
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat (
    val id: String,
    val title: String,
    var members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {

    enum class ChatType{
        SINGLE,
        GROUP,
        ARCHIVE
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date? {
        val lastMessage: BaseMessage? = messages.lastOrNull()
        return lastMessage?.date
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String, String> {
        val noMessage = "Сообщений нет"
        val lastMessage = messages.lastOrNull() ?: return noMessage to ""
        val author: String = lastMessage.from.firstName ?: ""
        val message: String = when(lastMessage) {
            is TextMessage -> lastMessage.text?.truncate(127) ?: noMessage
            is ImageMessage -> "${lastMessage.from.firstName} - отправил фото"
            else -> noMessage
        }
        return message to author
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int {
        return messages.filter {
            !it.isReaded
        }.size
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return when {
            isSingle() -> {
                val user = members.first()
                ChatItem(
                    id,
                    user.avatar,
                    Utils.toInitials(user.firstName, user.lastName) ?: "??",
                    "${user.firstName ?: ""} ${user.lastName ?: ""}",
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    user.isOnline,
                    ChatType.SINGLE,
                    user.firstName
                )
            }
            else -> {
                ChatItem(
                    id,
                    null,
                    "",
                    title,
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    false,
                    ChatType.GROUP,
                    lastMessageShort().second
                )
            }
        }
    }

}

