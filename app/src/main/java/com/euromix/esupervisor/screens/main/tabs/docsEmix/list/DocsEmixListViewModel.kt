package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.app.model.visits.entities.VisitsListSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.visits.list.VisitsListViewModel
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var currentJob: Job? = null

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        if (result !is Pending) currentJob = null

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

        currentJob?.cancel()

        currentJob = safeLaunch {
            docsEmixRepository.getDocsEmix(requestFromSelection()).collect { result ->
                updateViewState(result)
            }
        }
    }

    private fun requestFromSelection() = DocsEmixRequestEntity(
        startDate = _selection.period?.first?.dateToJsonString(),
        endDate = _selection.period?.second?.dateToJsonString(),
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