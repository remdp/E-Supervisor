package com.euromix.esupervisor.app.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun beginCurrentMonth(): LocalDate = YearMonth.now().atDay(1)

fun endCurrentMonth(): LocalDate = YearMonth.now().atEndOfMonth()

fun LocalDate.toLong() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

fun LocalDate.toDate(): Date = Date.from(
    this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
)

fun formatTimeFromSeconds(seconds: Int): String {

    val localTime = LocalTime.ofSecondOfDay((if (seconds < 0) 0 else seconds).toLong())
    return localTime.toTextHMS()
}

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.toJsonString(): String =
    atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

fun LocalDateTime.toTextHMS(): String = format(DateTimeFormatter.ofPattern("HH:mm:ss"))

fun LocalDateTime.isTimeZero() = hour == 0 && minute == 0 && second == 0

fun LocalDateTime.toText(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
fun LocalDate.toText(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
fun LocalDateTime.toTextHM(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy', 'HH:mm"))
fun LocalTime.toTextHMS(): String = format(DateTimeFormatter.ofPattern("HH:mm:ss"))

fun Date.toJsonString(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK).format(this)

fun Date.toText(): String = SimpleDateFormat("dd.MM.yyyy", Locale.UK).format(this)

fun Date.toLocalDate(): LocalDate = toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

