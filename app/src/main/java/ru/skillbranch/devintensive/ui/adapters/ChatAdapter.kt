package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import kotlinx.android.synthetic.main.item_chat_archive.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem

class ChatAdapter(private val listener: (ChatItem)->Unit): RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()
    private var archiveItem: ChatItem? = null
    private var messageCount = 0
//    private var archiveFlag = false

    override fun getItemViewType(position: Int): Int = when(items[position].chatType) {
        Chat.ChatType.ARCHIVE -> ARCHIVE_TYPE
        Chat.ChatType.SINGLE -> SINGLE_TYPE
        Chat.ChatType.GROUP -> GROUP_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            ARCHIVE_TYPE -> ArchiveViewHolder(inflater.inflate(R.layout.item_chat_archive, parent, false))
            else -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(data: List<ChatItem>) {
        if (archiveItem != null) {
            val copy = data.toMutableList()
            copy.add(0, archiveItem!!)
            updateChatData(copy.toList())
        } else {
            updateChatData(data)
        }
    }

    fun updateArchiveData(data: List<ChatItem>) {
        val copy: MutableList<ChatItem>
        val lastItem = data.lastIndex
        if (data.isNotEmpty()) {
            messageCount = 0
            for (chat in data) {
                messageCount += chat.messageCount
            }
            copy = items.toMutableList()
            if (archiveItem != null) copy.removeAt(0)
            archiveItem = ChatItem(
                "-1",
                null,
                "",
                "",
                data.last().shortDescription,
                messageCount,
                data.last().lastMessageDate,
                false,
                Chat.ChatType.ARCHIVE,
                data.last().author
            )
            copy.add(0, archiveItem!!)
        } else {
            archiveItem = null
            copy = items.toMutableList()
            copy.removeAt(0)
        }
        updateChatData(copy.toList())
    }

    private fun updateChatData(data: List<ChatItem>) {
        val diffCallback = object :DiffUtil.Callback() {
            override fun getOldListSize(): Int =items.size

            override fun getNewListSize(): Int = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = items[oldItemPosition].id == data[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean  = items[oldItemPosition].hashCode() == data[newItemPosition].hashCode()

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    abstract class ChatItemViewHolder(convertView: View): RecyclerView.ViewHolder(convertView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(convertView: View): ChatItemViewHolder(convertView), ItemTouchViewHolder {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            if (item.avatar == null) {
                Glide.with(itemView)
                    .clear(iv_avatar_single)
                iv_avatar_single.setInitials(item.initials)
            } else {
                Glide.with(itemView)
                    .load(item.avatar)
                    .into(iv_avatar_single)
            }
            sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE
            with(tv_date_single) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            with(tv_counter_single) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            tv_title_single.text = item.title
            tv_message_single.text = item.shortDescription
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class GroupViewHolder(convertView: View): ChatItemViewHolder(convertView), ItemTouchViewHolder {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            iv_avatar_group.setInitials(item.title[0].toString())
            with(tv_date_group) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            with(tv_counter_group) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription
            with(tv_message_author) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.author
            }
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class ArchiveViewHolder(convertView: View): ChatItemViewHolder(convertView) {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            with(tv_date_archive) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            with(tv_counter_archive) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            tv_message_archive.text = item.shortDescription
            with(tv_message_author_archive) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.author
            }
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }
}