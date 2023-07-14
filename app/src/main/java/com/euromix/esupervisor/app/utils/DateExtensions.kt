package com.euromix.esupervisor.app.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private fun LocalDate.toFormatString(format: String): String =
    format(DateTimeFormatter.ofPattern(format))

fun textDate(date: LocalDateTime): String =
    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun textDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))