package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DocEmixDetailViewModel @AssistedInject constructor(
    @Assisted private val extId: String,
    private val docEmixDetailRepository: DocEmixDetailRepository
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as DocEmixDetail)
            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: DocEmixDetail) {
        _viewState = _viewState.copy(isLoading = false, error = null, docEmixDetail = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getDocEmixDetail() {
        safeLaunch {
            docEmixDetailRepository.getDocEmixDetail(extId).collect { result ->
                updateViewState(result)
            }
        }
    }

    private fun setUpdateParentList(){
        _viewState = _viewState.copy(updateParentList = true)
    }

    fun reload() {
        getDocEmixDetail()
    }

    fun publishViewState(){
        _viewStateEvent.publishEvent()
    }

    fun acceptDocEmixDetail() {
        safeLaunch {
            docEmixDetailRepository.acceptDocEmixDetail(extId).collect { result ->
                setUpdateParentList()
                updateViewState(result)
            }
        }
    }

    fun rejectDocEmixDetail(reason: String) {
        safeLaunch {
            docEmixDetailRepository.rejectDocEmixDetail(extId, reason).collect { result ->
                setUpdateParentList()
                updateViewState(result)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(extId: String): DocEmixDetailViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val updateParentList: Boolean = false,
        val docEmixDetail: DocEmixDetail? = null
    ) : BaseViewState()
}