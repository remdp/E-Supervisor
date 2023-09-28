package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.ImagesReactions
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImagesViewModel @AssistedInject constructor(
    @Assisted private val extId: String,
    private val docEmixDetailRepository: DocEmixDetailRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData(ViewState(needLoading = true))
    val viewState = _viewState.share()

    private fun updateResult(result: Result<*>) {
        viewState.value?.let { stateValue ->
            when (result) {
                is Pending -> _viewState.value = stateValue.copy(
                    needLoading = false, imagesResult = Pending()
                )

                is Success -> {

                    (result.value as? ImagesReactions)?.let {
                        _viewState.value = viewState.value?.copy(imagesResult = Success(it))
                    }
                }

                is Error -> _viewState.value = stateValue.copy(
                    needLoading = false,
                    imagesResult = Error(result.error)
                )

                else -> _viewState.value =
                    stateValue.copy(needLoading = false)
            }
        }
    }

    private fun getImagesLikes() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.getDocLikes(extId).collect { result ->
                updateResult(result)
            }
        }
    }

    fun react(reaction: ImageReactionRequestEntity) {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.react(extId, reaction).collect { result ->
                updateResult(result)
            }
        }
    }

    fun afterUpdateState() {
        viewState.value?.let { stateValue ->
            if (stateValue.needLoading) {
                getImagesLikes()
            }
        }
    }

    fun reload() {
        getImagesLikes()
    }

    @AssistedFactory
    interface Factory {
        fun create(extId: String): ImagesViewModel
    }

    data class ViewState(
        val needLoading: Boolean = true,
        val imagesResult: Result<ImagesReactions> = Empty(),
        val creationDislikeTaskMessage: String = ""
    )
}