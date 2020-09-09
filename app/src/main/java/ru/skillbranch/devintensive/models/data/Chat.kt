package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
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
    private fun lastMessageDate(): Date? {
        //TODO()
        return Date()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private fun lastMessageShort(): Pair<String, String> {
        //TODO()
        return "Сообщений нет" to "@John_Doe"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private fun unreadableMessageCount(): Int {
        //TODO()
        return 0
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
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

