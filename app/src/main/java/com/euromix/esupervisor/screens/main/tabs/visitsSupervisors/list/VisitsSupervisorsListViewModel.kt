package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.visits.list.VisitsListViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisitsSupervisorsListViewModel @Inject constructor(private val visitsSupervisorsRepository: VisitsSupervisorsRepository) :
    BaseViewModel() {

    private var _viewState: ViewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as List<VisitSupervisor>)
            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)

    }

    private fun handleSuccess(value: List<VisitSupervisor>) {
        _viewState = _viewState.copy(isLoading = false, error = null, visitsSupervisors = value)
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getVisitsSupervisors() {
        safeLaunch {
            visitsSupervisorsRepository.getVisitsSupervisors().collect {
                updateViewState(it)
            }
        }
    }

    fun reload(){
        getVisitsSupervisors()
    }

    fun getListForSubmit(): List<VisitSupervisor>  = _viewState.visitsSupervisors

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val visitsSupervisors: List<VisitSupervisor> = listOf(),
    ) : BaseViewState()
}