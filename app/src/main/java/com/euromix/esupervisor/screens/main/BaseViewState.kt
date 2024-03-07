package com.euromix.esupervisor.screens.main

open class BaseViewState(
    open val isLoading: Boolean = false,
    open val error: Throwable? = null
)
