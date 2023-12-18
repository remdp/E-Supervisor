package com.euromix.esupervisor.app.utils.async

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Success
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

typealias fetcher<R> = suspend () -> R

fun <R> serverCallbackFlowFetcher(fetcher: fetcher<R>) =
    callbackFlow {
        try {
            trySend(Pending())
            trySend(Success(fetcher()))
        } catch (e: Exception) {
            trySend(Error(e))
        }
        awaitClose { }
    }
