package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.topLevel

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.storeCheck.StoreCheckRepository
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevelRow
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreCheckTopLevelViewModel @Inject constructor(
    private val storeCheckRepository: StoreCheckRepository
) : BaseViewModel() {

    private var _viewState: ViewState? = null
    val viewState: ViewState?
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    private fun <T> updateViewState(result: Result<T>) {

        _viewState?.let {
            when (result) {
                is Pending -> handlePendingState()
                is Success -> handleSuccess(result.value as StoreCheckTopLevel)
                is Error -> handleError(result.error)
                else -> {}
            }
            _viewStateEvent.publishEvent(it)
        }
    }

    private fun handlePendingState() {
        _viewState = _viewState?.copy(isLoading = true, error = null)

    }

    private fun handleSuccess(value: StoreCheckTopLevel) {

        _viewState?.let { viewState ->
            _viewState = viewState.copy(
                isLoading = false,
                error = null,
                data = viewState.data.copy(
                    isBasis = value.isBasis,
                    pos = value.pos,
                    posBasis = value.posBasis,
                    availabilityPos = value.availabilityPos,
                    isCheckOut = value.isCheckOut,
//                    storeCheckFormat = value.storeCheckFormat,
//                    shelfShare = value.shelfShare,
                    rows = value.rows
                )
            )
        }

    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState?.copy(isLoading = false, error = error)
    }

    private fun getStoreCheckTopLevel() {
        safeLaunch {
            _viewState?.let { viewState ->
                storeCheckRepository.getStoreCheckTopLevel(viewState.data.extId).collect {
                    updateViewState(it)
                }
            }
        }
    }

    fun reload() {
        getStoreCheckTopLevel()
    }

    fun getListForSubmit(): List<StoreCheckTopLevelRow> = _viewState?.data?.rows ?: listOf()

    fun initViewState(extId: String) {
        _viewState = ViewState(data = StoreCheckTopLevel(extId = extId))
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val data: StoreCheckTopLevel
    ) : BaseViewState()

}