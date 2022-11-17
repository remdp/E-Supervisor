package com.euromix.esupervisor.app.utils.async

import com.euromix.esupervisor.app.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking

typealias SuspendValueLoader<A, T> = suspend (A) -> T?

/**
 * The same as [LazyListenersSubject] but adapted for using with kotlin flows.
 * @see LazyListenersSubject
 */
class LazyFlowSubject<A : Any, T : Any>(
    private val loader: SuspendValueLoader<A, T>
) {

    private val lazyListenersSubject = LazyListenersSubject<A, T> { arg ->
        runBlocking {
            loader.invoke(arg)
        }
    }

    /**
     * @see LazyListenersSubject.reloadArgument
     */
    fun reloadArgument(argument: A, silentMode: Boolean = false) {
        lazyListenersSubject.reloadArgument(argument, silentMode)
    }

    /**
     * @see LazyListenersSubject.addListener
     * @see LazyListenersSubject.removeListener
     */
    fun listen(argument: A): Flow<Result<T>> = callbackFlow {
        val listener: ValueListener<T> = { result ->
            trySend(result)
        }
        lazyListenersSubject.addListener(argument, listener)
        awaitClose {
            lazyListenersSubject.removeListener(argument, listener)
        }
    }

}