package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.humanizeDiff
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(
    val id: String,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var rating: Int = 0,
    var respect: Int = 0,
    var lastVisit: Date? = Date(),
    var isOnline: Boolean = false
) {
    fun toUserItem(): UserItem {
        val lastActivity = when{
            lastVisit == null -> "Ещё ни разу не заходил"
            isOnline -> "online"
            else -> "Последний раз был ${lastVisit!!.humanizeDiff()}"
        }
        return UserItem(
            id,
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            Utils.toInitials(firstName, lastName),
            avatar,
            lastActivity,
            false,
            isOnline
        )
    }

    constructor(id: String, firstName: String?, lastName: String?): this(id = id, firstName = firstName, lastName = lastName, avatar = null)

    constructor(builder: Builder): this (
        id = builder.id ?: (lastId++).toString(),
        firstName = builder.firstName,
        lastName = builder.lastName,
        avatar = builder.avatar,
        rating = builder.rating,
        respect = builder.respect,
        lastVisit = builder.lastVisit,
        isOnline = builder.isOnline
    )

    companion object Factory {

        private var lastId: Int = -1

        fun makeUser(fullName: String?): User {
            lastId++
            val (firstName, lastName) = Utils.parseFullName(fullName)
            return User(id = "$lastId", firstName = firstName, lastName = lastName)
        }

    }

    class Builder {
        var id: String? = null
            private set
        var firstName: String? = null
            private set
        var lastName: String? = null
            private set
        var avatar: String? = null
            private set
        var rating: Int = 0
            private set
        var respect: Int = 0
            private set
        var lastVisit: Date? = Date()
            private set
        var isOnline: Boolean = false
            private set

        fun id(id: String) = apply { this.id = id }

        fun firstName(value: String) = apply { this.firstName = value }

        fun lastName(value: String) = apply { this.lastName = value }

        fun avatar(value: String) = apply { this.avatar = value }

        fun rating(value: Int) = apply { this.rating = value }

        fun respect(value: Int) = apply { this.respect = value }

        fun lastVisit(value: Date) = apply { this.lastVisit = value }

        fun isOnline(value: Boolean) = apply { this.isOnline = value }

        fun build() = User(this)
    }

}