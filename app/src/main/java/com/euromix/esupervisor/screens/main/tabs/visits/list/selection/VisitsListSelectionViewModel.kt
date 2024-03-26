package com.euromix.esupervisor.screens.main.tabs.visits.list.selection

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.visits.entities.VisitsListSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisitsListSelectionViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    BaseViewModel() {

    private val _foundTradingAgents = MutableLiveData<Result<List<ServerPair>>>()
    val foundTradingAgents = _foundTradingAgents.share()

    private val _selection = MutableLiveData<VisitsListSelection>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    init {
        _errorsMinLength.value = ErrorsMinLength()
    }

    private fun verifyMinLength(searchString: String): Boolean {

        return (searchString.length >= Const.MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                _errorsMinLength.value =
                    _errorsMinLength.value?.copy(minLengthTradingAgentError = true)
            }
        }
    }
    fun findTradingAgents(searchString: String) {
        if (verifyMinLength(searchString)) {
            safeLaunch {
                searchRepository.findTradingAgents(searchString).collect {
                    _foundTradingAgents.value = it
                }
            }
        }
    }

    fun updateTradingAgentSelection(tradingAgent: ServerPair?) {
        _selection.value =
            _selection.value?.copy(
                tradingAgent = tradingAgent
            )
    }

    fun initSelection(selection: VisitsListSelection) {
        _selection.value = selection
    }

    fun clearSelection() {

        _selection.value?.let {
            _selection.value = VisitsListSelection()
        }
    }
}

data class ErrorsMinLength(
    val minLengthTradingAgentError: Boolean = false
)