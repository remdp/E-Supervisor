package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.storeCheck.StoreCheckRepository
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckVisit
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StoreCheckListViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val storeCheckRepository: StoreCheckRepository
) :
    BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
    //    reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        _viewState.let {
            when (result) {
                is Pending -> handlePendingState()
                is Success -> handleSuccess(result.value as List<StoreCheckVisit>)
                is Error -> handleError(result.error)
                else -> {}
            }
            _viewStateEvent.publishEvent(it)
        }

    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)

    }

    private fun handleSuccess(value: List<StoreCheckVisit>) {
        _viewState = _viewState.copy(isLoading = false, error = null, storeChecksVisit = value)
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }


    private fun getStoreChecksVisit() {
        safeLaunch {
            storeCheckRepository.getStoreChecksVisit(id).collect {
                updateViewState(it)
            }
        }
    }

    fun getListForSubmit(): List<StoreCheckVisit> {
        return _viewState.storeChecksVisit
    }

    fun reload() {
        getStoreChecksVisit()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): StoreCheckListViewModel
    }
//
//    fun initViewState() {
//        _viewState = ViewState()
//    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        //  val id: String = "",
        val storeChecksVisit: List<StoreCheckVisit> = emptyList()
    ) : BaseViewState()
}