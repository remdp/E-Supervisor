package com.euromix.esupervisor.screens.main.tabs.docsEmix.selection

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.Const.MIN_LENGTH_SEARCH_STRING
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocsSelectionViewModel @Inject constructor(
    private val resManager: ResourceManager,
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    private val _foundTradingAgents = MutableLiveData<Result<List<ServerPair>>>()
    val foundTradingAgents = _foundTradingAgents.share()

    private val _foundPartners = MutableLiveData<Result<List<ServerPair>>>()
    val foundPartners = _foundPartners.share()

    private val _selection = MutableLiveData<DocsEmixSelection?>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    init {
        _errorsMinLength.value = ErrorsMinLength()
    }

    fun findTradingAgents(searchString: String) {
        if (verifyMinLength(searchString, 0)) {
            safeLaunch {
                searchRepository.findTradingAgents(searchString).collect {
                    _foundTradingAgents.value = it
                }
            }
        }
    }

    fun findPartners(searchString: String) {
        if (verifyMinLength(searchString, 1)) {
            safeLaunch {
                searchRepository.findPartners(searchString).collect {
                    _foundPartners.value = it
                }
            }
        }
    }

    fun updateTradingAgentSelection(tradingAgent: ServerPair?) {
        _selection.value =
            if (_selection.value == null) DocsEmixSelection(tradingAgent = tradingAgent)
            else _selection.value!!.copy(tradingAgent = tradingAgent)
    }

    fun updatePartnerSelection(partner: ServerPair?) {
        _selection.value = if (_selection.value == null) DocsEmixSelection(partner = partner)
        else _selection.value!!.copy(partner = partner)
    }

    fun updateOperationTypeSelection(operationType: ServerPair? = null) {
        _selection.value =
            if (_selection.value == null) DocsEmixSelection(operationType = operationType)
            else _selection.value!!.copy(operationType = operationType)
    }

    fun updateStatusSelection(status: ServerPair? = null) {
        _selection.value = if (_selection.value == null) DocsEmixSelection(status = status)
        else _selection.value!!.copy(status = status)
    }

    fun checkOperationTypeEmpty(): Boolean = _selection.value?.operationType == null
    fun checkStatusEmpty(): Boolean = _selection.value?.status == null


    fun updateSelection(selection: DocsEmixSelection?) {
        _selection.value = selection
    }

    fun clearSelection() {
        _selection.value = null
    }

    fun getOperationTypesForChoose(): List<ServerPair> {

        val operationTypesForChoose = mutableListOf<ServerPair>()

        var count = 0
        DocEmixOperationType.operationTypes().forEach {
            operationTypesForChoose.add(
                ServerPair(
                    count.toString(),
                    resManager.getString(it.nameStringRes())
                )
            )
            count++
        }
        return operationTypesForChoose
    }

    fun getStatusesForChoose(): List<ServerPair> {

        val statusesForChoose = mutableListOf<ServerPair>()

        var count = 0
        Status.statuses().forEach {
            statusesForChoose.add(
                ServerPair(
                    count.toString(),
                    resManager.getString(it.nameStringRes())
                )
            )
            count++
        }
        return statusesForChoose
    }

    private fun verifyMinLength(searchString: String, indexVerifyField: Int): Boolean {

        return (searchString.length >= MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                when (indexVerifyField) {
                    0 -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthTradingAgentError = true)

                    else -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthPartnerError = true)
                }
            }
        }
    }
}

data class ErrorsMinLength(
    val minLengthTradingAgentError: Boolean = false,
    val minLengthPartnerError: Boolean = false
)