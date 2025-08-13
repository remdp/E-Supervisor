package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.detail

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class VisitSupervisorDetailViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val visitsSupervisorsRepository: VisitsSupervisorsRepository
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
    //    reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as VisitSupervisorDetail)
            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)

    }

    private fun handleSuccess(value: VisitSupervisorDetail) {
        _viewState = _viewState.copy(isLoading = false, error = null, detailData = value)
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getVisitSupervisorDetail() {
        safeLaunch {
            visitsSupervisorsRepository.getVisitSupervisorDetail(id).collect {
                updateViewState(it)
            }
        }
    }

    fun expandStoreChecks(){
        if (_viewState.detailData?.storeChecks?.isNotEmpty() == true){
            _viewState = _viewState.copy(storeChecksExpand = !_viewState.storeChecksExpand)
            _viewStateEvent.publishEvent(_viewState)
        }
    }

    fun expandTasks(){
        if (_viewState.detailData?.tasks?.isNotEmpty() == true){
            _viewState = _viewState.copy(tasksExpand = !_viewState.tasksExpand)
            _viewStateEvent.publishEvent(_viewState)
        }
    }

    fun reload(){
        getVisitSupervisorDetail()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): VisitSupervisorDetailViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val storeChecksExpand: Boolean = false,
        val tasksExpand: Boolean = false,
        val detailData: VisitSupervisorDetail? = null,
    ) : BaseViewState()
}