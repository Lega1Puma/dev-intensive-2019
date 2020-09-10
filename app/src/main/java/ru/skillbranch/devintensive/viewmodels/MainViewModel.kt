package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel: ViewModel() {


    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chatItems: LiveData<List<ChatItem>> = Transformations.map(chatRepository.loadChats()){ chats->
        return@map chats.filter {
            !it.isArchived
        }.map {
            it.toChatItem()
        }.sortedBy {
            it.id.toInt()
        }
    }
    private var archiveChatItem = ChatItem(
        "-1",
        null,
        "",
        "",
        "",
        0,
        null,
        false,
        Chat.ChatType.ARCHIVE,
        ""
    )

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val chats = chatItems.value!!
            result.value = if (queryStr.isEmpty()) chats
            else chats.filter {
                it.title.contains(queryStr, true)
            }
        }
        result.addSource(chatItems) {
            filterF.invoke()
        }
        result.addSource(query) {
            filterF.invoke()
        }

        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))

//        var messageCount = 0
//        val copy = chatItems.value!!.toMutableList()
//        val archiveItem = copy.filter {
//            it.id == "-1"
//        }
//        if (archiveItem.isEmpty()) {
//            messageCount = archiveItem.first().messageCount
//        }
//        val archiveChatItem = ChatItem(
//            "-1",
//            null,
//            "",
//            "",
//            chat.lastMessageShort().first,
//            messageCount + chat.unreadableMessageCount(),
//            chat.lastMessageDate()?.shortFormat(),
//            false,
//            Chat.ChatType.ARCHIVE,
//            chat.lastMessageShort().second
//        )
//        copy.add(0, archiveChatItem)
//
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

}