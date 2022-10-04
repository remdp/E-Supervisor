package com.euromix.esupervisor.app.model

sealed class Result<T> {

//    fun <R> map(mapper: ((T) -> R)? = null): Result<R> {
//
//        return when (this) {
//            is Success<T> -> {
//                if (mapper == null) {
//                    throw IllegalStateException("Can't map Success<T> result without mapper.")
//                } else {
//                    Success(mapper(this.value))
//                }
//            }
//            is Error<T> -> Error(this.error)
//            is Empty<T> -> Empty()
//            is Pending<T> -> Pending()
//        }
//
//    }
}

class Success<T>(val value: T) : Result<T>()

class Error<T>(val error: Throwable) : Result<T>()

class Empty<T> : Result<T>()

class Pending<T> : Result<T>()

