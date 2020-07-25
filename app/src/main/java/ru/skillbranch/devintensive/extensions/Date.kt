package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

const val SECONDS = 1000L
const val MINUTE = SECONDS * 60
const val HOUR = MINUTE * 60
const val DAY = HOUR * 24


interface Plural {
    fun plural(value: Int): String
}

enum class TimeUnits: Plural {
    SECOND {
        override fun plural(value: Int): String {
            return "$value " + when (value % 100) {
                in 5..20 -> "секунд"
                else -> when (value % 10) {
                    1 -> "сикунду"
                    in 2..4 -> "секунды"
                    in 5..9 -> "секунд"
                    else -> "секунд"
                }
            }
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            return "$value " + when (value % 100) {
                in 5..20 -> "минут"
                else -> when (value % 10) {
                    1 -> "минуту"
                    in 2..4 -> "минуты"
                    in 5..9 -> "минут"
                    else -> "минут"
                }
            }
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            return "$value " + when (value % 100) {
                in 5..20 -> "часов"
                else -> when (value % 10) {
                    1 -> "час"
                    in 2..4 -> "часа"
                    in 5..9 -> "часов"
                    else -> "часов"
                }
            }
        }
    },
    DAY {
        override fun plural(value: Int): String {
            return "$value " + when (value % 100) {
                in 5..20 -> "день"
                else -> when (value % 10) {
                    1 -> "день"
                    in 2..4 -> "дня"
                    in 5..9 -> "дней"
                    else -> "дней"
                }
            }
        }
    }
}

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String = SimpleDateFormat(pattern, Locale("ru")).format(this)

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    val time = this.time + when(units) {
        TimeUnits.SECOND -> value * SECONDS
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var diffTime = (date.time - this.time)
    val pastTense: Boolean = diffTime >= 0
    diffTime = diffTime.absoluteValue
    var diff = diffTime / SECONDS
    when {
        diff < 1 -> {
            return if (pastTense) "только что"
            else "через несколько секунд"
        }
        diff < 45 -> {
            return if (pastTense) "несколько секунд назад"
            else "через несколько секунд"
        }
        diff < 75 -> {
            return if (pastTense) "минуту назад"
            else "через минуту"
        }
    }
    diff = diffTime / MINUTE
    when {
        diff < 45 -> {
            val trueCase = when (diff % 100) {
                in 5..20 -> "минут"
                else -> when (diff % 10) {
                    1L -> "минуту"
                    in 2..4 -> "минуты"
                    in 5..9 -> "минут"
                    else -> "минут"
                }
            }
            return if (pastTense) "$diff $trueCase назад"
            else "через $diff $trueCase"
        }
        diff < 75 -> {
            return if (pastTense) "час назад"
            else "через час"
        }
    }
    diff = diffTime / HOUR
    when {
        diff < 22 -> {
            val trueCase = when (diff % 100) {
                in 5..20 -> "часов"
                else -> when (diff % 10) {
                    1L -> "час"
                    in 2..4 -> "часа"
                    in 5..9 -> "часов"
                    else -> "часов"
                }
            }
            return if (pastTense) "$diff $trueCase назад"
            else "через $diff $trueCase"
        }
        diff < 26 -> {
            return if (pastTense) "день назад"
            else "через день"
        }
    }
    diff = diffTime / DAY
    return if (diff < 360) {
        val trueCase = when (diff % 100) {
            in 5..20 -> "дней"
            else -> when (diff % 10) {
                1L -> "день"
                in 2..4 -> "дня"
                in 5..9 -> "дней"
                else -> "дней"
            }
        }
        if (pastTense) "$diff $trueCase назад"
        else "через $diff $trueCase"
    } else if (pastTense) "более года назад"
    else "более чем через год"
}