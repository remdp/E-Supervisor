package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.ImagesReactions
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImagesViewModel @AssistedInject constructor(
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
            is Success -> handleSuccess(result.value as ImagesReactions)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: ImagesReactions) {
        _viewState = _viewState.copy(isLoading = false, error = null, imagesReactions = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getImagesLikes() {
        safeLaunch {
            docEmixDetailRepository.getDocLikes(extId).collect { result ->
                updateViewState(result)
            }
        }
    }

    fun reload() {
        getImagesLikes()
    }

    fun react(reaction: ImageReactionRequestEntity) {
        safeLaunch {
            docEmixDetailRepository.react(extId, reaction).collect { result ->
                updateViewState(result)
            }
        }
    }

    fun clearCreationDislikeTaskMessage(){
        _viewState = _viewState.copy()
    }

    @AssistedFactory
    interface Factory {
        fun create(extId: String): ImagesViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val imagesReactions: ImagesReactions? = null
    ): BaseViewState()
}