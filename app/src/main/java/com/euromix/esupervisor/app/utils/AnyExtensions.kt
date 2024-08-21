package com.euromix.esupervisor.app.utils

import java.text.DecimalFormat

/**
 * Безопасный каст к [T].
 */
inline fun <reified T> Any.safeAs(): T? = this as? T
fun isEven(number: Int): Boolean {
    return number % 2 == 0
}

fun Any?.toStringOrDefault(default: String = "N/A"): String {
    return if (this is Number) DecimalFormat("###,###.##").format(this)
        ?: default else this?.toString() ?: default
}
