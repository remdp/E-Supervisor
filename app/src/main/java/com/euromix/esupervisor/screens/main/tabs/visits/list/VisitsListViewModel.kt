package com.euromix.esupervisor.screens.main.tabs.visits.list

import android.util.Log
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.visits.VisitsRepository
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visits.entities.VisitsRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VisitsListViewModel @Inject constructor(private val visitsRepository: VisitsRepository) :
    BaseViewModel() {

    private var _viewState: ViewState = ViewState()
    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {
        when (result) {
            is Pending -> {
                handlePendingState()
            }

            is Success -> {
                handleSuccess(result.value as List<Visit>)
            }

            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true)
    }

    private fun handleSuccess(value: List<Visit>) {
        _viewState = _viewState.copy(
            isLoading = false,
            items = value.map { it }
        )
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getVisits() {

        safeLaunch {
            visitsRepository.getVisits(VisitsRequestEntity()).collect {
                Log.d("result", it.toString())
                updateViewState(it)
            }
        }

    }

    fun reload() {
        getVisits()
    }

    data class ViewState(
        val period: Pair<Date, Date>? = null,
        // val selection: DocsEmixSelection? = null,
        val items: List<Visit> = listOf(),
        override val isLoading: Boolean = false,
        val result: Result<List<DocEmix>> = Empty(),
        override val error: Throwable? = null
    ): BaseViewState()
}