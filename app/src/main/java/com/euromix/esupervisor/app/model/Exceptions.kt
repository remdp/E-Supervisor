package com.euromix.esupervisor.app.model

import com.euromix.esupervisor.app.enums.Field

open class AppException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
   // constructor(cause: Throwable, message: String?) : super(cause)
}

class EmptyFieldException(
    val field: Field
) : AppException()

class AuthException(
    cause: Throwable
   // message: String?
) : AppException(cause = cause)

class BackendException(
    val code: Int,
    cause: Throwable,
    override val message: String?
) : AppException(cause = cause)

//open class BackendException(
//    val code: Int,
//    message: String
//) : AppException(message)

class ConnectionException(cause: Throwable) : AppException(cause = cause)

class ParseBackendResponseException(cause: Throwable): AppException(cause = cause)

internal inline fun <T> wrapBackendExceptions(block: () -> T): T {
    try {
        return block.invoke()
    } catch (e: BackendException) {
        if (e.code == 401 && e.code == 404) {
            throw AuthException(e)
        } else {
            throw e
        }
    }
}