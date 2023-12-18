package com.euromix.esupervisor.app.common

import kotlinx.coroutines.flow.Flow

sealed class UseCaseResult<out Result>

abstract class OutUseCaseResult<out Result> : UseCaseResult<Result>() {

    protected abstract fun fetchResult(): Flow<Result>

    operator fun invoke(): Flow<Result> = fetchResult()
}