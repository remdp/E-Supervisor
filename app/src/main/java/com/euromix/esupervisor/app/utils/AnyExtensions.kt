package com.euromix.esupervisor.app.utils

/**
 * Безопасный каст к [T].
 */
inline fun <reified T> Any.safeAs(): T? = this as? T
fun isEven(number: Int): Boolean {
    return number % 2 == 0
}