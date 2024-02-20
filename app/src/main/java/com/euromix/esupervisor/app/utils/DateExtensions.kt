package com.euromix.esupervisor.app.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

private fun LocalDate.toFormatString(format: String): String =
    format(DateTimeFormatter.ofPattern(format))

fun LocalDate.toLong() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun textDate(date: LocalDateTime): String =
    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun textDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun LocalDate.dateToJsonString(): String =
    atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

fun LocalDate.dateToString(): String = format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun Date.dateToJsonString(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(this)

fun Date.dateToString(): String = SimpleDateFormat("dd.MM.yyyy").format(this)