package com.euromix.esupervisor.app.utils

import android.content.Context
import com.euromix.esupervisor.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDate.toFormatString(format: String): String = format(DateTimeFormatter.ofPattern(format))

fun textDate(date: LocalDateTime, context: Context): String =
    date.toLocalDate().toFormatString(context.getString(R.string.day_month_year_date_format))

//fun Date.textDate(date: Date, context: Context): String =
//    date.toLocalDate().toFormatString(context.getString(R.string.day_month_year_date_format))