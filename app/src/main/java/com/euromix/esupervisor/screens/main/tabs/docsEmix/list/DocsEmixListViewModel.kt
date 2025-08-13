package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DocsEmixListViewModel @Inject constructor(
    private val docsEmixRepository: DocsEmixRepository
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    private var _selection = DocsEmixSelection()
    val selection
        get() = _selection
    private val _selectionEvent = MutableLiveEvent<DocsEmixSelection>()
    val selectionEvent = _selectionEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as List<DocEmix>)
            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: List<DocEmix>) {
        _viewState = _viewState.copy(isLoading = false, error = null, docsEmix = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getDocsEmix() {

        safeLaunch {
            docsEmixRepository.getDocsEmix(requestFromSelection()).collect { result ->
                updateViewState(result)
            }
        }
    }

    private fun requestFromSelection() = DocsEmixRequestEntity(
        startDate = _selection.period?.first?.toJsonString(),
        endDate = _selection.period?.second?.toJsonString(),
        tradingAgentId = _selection.tradingAgent?.id,
        partnerId = _selection.partner?.id,
        operationType = selection.operationType?.id,
        status = selection.status?.id
    )

    fun reload() {
        getDocsEmix()
    }

    fun changePeriod(period: Pair<Date, Date>?) {
        _selection = _selection.copy(period = period)
        _selectionEvent.publishEvent(_selection)
    }

    fun updateSelection(selection: DocsEmixSelection? = null) {
        _selection = selection ?: _selection
        _selectionEvent.publishEvent(_selection)
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val period: Pair<Date, Date>? = null,
        val selection: DocsEmixSelection? = null,
        val docsEmix: List<DocEmix> = listOf()
    ) : BaseViewState()
}