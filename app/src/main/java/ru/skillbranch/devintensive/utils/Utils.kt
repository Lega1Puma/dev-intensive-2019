package ru.skillbranch.devintensive.utils

object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        var firstName:String? = null
        var lastName:String? = null

        if (fullName !== null) {
            if (!fullName.isBlank()) {
                val newFullName: String = fullName.trimStart().trimEnd().replace("\\s+".toRegex(), " ")
                val parts = newFullName.split(" ")
                firstName = parts[0]
                if (parts.size != 1) {
                    lastName = newFullName.substringAfter(' ')
                }
            }
        }

        return firstName to lastName
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var initials: String? = ""
        if (firstName !== null) {
            if (!firstName.isBlank()) {
                initials += firstName.trimStart()[0].toUpperCase()
            }
        }
        if (lastName !== null) {
            if (!lastName.isBlank()) {
                initials += lastName.trimStart()[0].toUpperCase()
            }
        }
        return if (initials === "") null
        else initials
    }

    fun transliteration(payload: String, divider: String = " "):String {
        val symbols: Map<String, String> = mapOf(
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "д" to "d",
            "е" to "e",
            "ё" to "e",
            "ж" to "zh",
            "з" to "z",
            "и" to "i",
            "й" to "i",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "ф" to "f",
            "х" to "h",
            "ц" to "c",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "sh'",
            "ъ" to "",
            "ы" to "i",
            "ь" to "",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya"
        )
        var newString = ""
        for (i in payload.indices) {
            newString += when {
                payload[i] == ' ' -> divider
                payload[i].isUpperCase() -> symbols[payload[i].toLowerCase().toString()]?.capitalize() ?: payload[i].toString()
                else -> symbols[payload[i].toString()] ?: payload[i].toString()
            }
        }
        return newString
    }
}