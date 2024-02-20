package com.euromix.esupervisor.app.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

fun String.toDate(): Date = Date.from(LocalDateTime.parse(this).toInstant(ZoneOffset.UTC))