package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImageViewModel @AssistedInject constructor(
    @Assisted private val id: String?,
    private val docEmixDetailRepository: DocEmixDetailRepository,
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        if (!id.isNullOrEmpty()) {
            getImageReactions()
        }
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as List<ImageReaction>)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: List<ImageReaction>) {
        _viewState = _viewState.copy(isLoading = false, error = null, reactions = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getImageReactions() {
        val safeId = id ?: return

        safeLaunch {
            docEmixDetailRepository.getImageReactions(safeId).collect {
                updateViewState(it)
            }
        }
    }

    fun reload() {
        getImageReactions()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String?): ImageViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val reactions: List<ImageReaction> = listOf()
    ) : BaseViewState()
}