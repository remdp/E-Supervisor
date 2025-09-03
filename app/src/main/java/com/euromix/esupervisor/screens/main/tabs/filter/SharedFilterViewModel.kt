package com.euromix.esupervisor.screens.main.tabs.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.euromix.esupervisor.app.model.filter.entities.FilterItem
import com.euromix.esupervisor.app.model.filter.entities.FilterSelection
import com.euromix.esupervisor.app.utils.share

class SharedFilterViewModel : ViewModel() {

    private var _filterValidator: ((filter: List<FilterItem>) -> FilterValidationEvent)? = null

    private val _filterResult = MutableLiveData<FilterSelection>()
    val filterResult = _filterResult.share()

    fun setFilterValidator(validator: (filter: List<FilterItem>) -> FilterValidationEvent) {
        _filterValidator = validator
    }

    fun validateFilter(filter: List<FilterItem>) =
        _filterValidator?.invoke(filter) ?: FilterValidationEvent.FilterValid

    fun postFilterResult(selection: FilterSelection) {
        _filterResult.value = selection
    }

    fun getFilterSelection() = _filterResult.value
}