package com.euromix.esupervisor.screens.main.tabs.filter

sealed class FilterValidationEvent {
    data object FilterValid : FilterValidationEvent()
    data class FilterNotValid(val title: String, val errorMessage: String): FilterValidationEvent()
}