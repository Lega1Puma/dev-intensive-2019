package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    val text = this.trim()
    if (text.length <= length) return text
    var newText = ""
    for (i in 0 until length) {
        newText += text[i].toString()
    }
    return "${newText.trimEnd()}..."
}

fun String.stripHtml():String {
    val text = this
    var newText = ""
    var tagFlag = false
    var escapeFlag = false
    for (i in text.indices) {
        if (text[i] == '<') tagFlag = true
        else if (!tagFlag) {
            if (text[i] == '&') escapeFlag = true
            else if (!escapeFlag) newText += text[i].toString()
            else if (text[i] == ';') escapeFlag = false
        }
        else if (text[i] == '>') tagFlag = false
    }
    return newText.replace("\\s+".toRegex(), " ")
}