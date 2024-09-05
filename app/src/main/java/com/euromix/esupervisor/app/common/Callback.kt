package com.euromix.esupervisor.app.common

/** A functional interface that returns a value. */
typealias Supplier<T> = () -> T

/** A functional interface that returns `true` or `false` for a given input value. */
typealias Predicate<T> = (T) -> Boolean

/** A functional interface that takes a single value. */
typealias Consumer<T> = (T) -> Unit

/** A functional interface that takes two values. */
typealias BiConsumer<T1, T2> = (T1, T2) -> Unit

/** A functional interface that takes three values. */
typealias Consumer3<T1, T2, T3> = (T1, T2, T3) -> Unit

/** A functional interface that has no input or output value. */
typealias Action = () -> Unit