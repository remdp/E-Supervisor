package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImageViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val docEmixDetailRepository: DocEmixDetailRepository,
) : BaseViewModel() {

    private val _viewState = MutableLiveData(ViewState())
    val viewState = _viewState.share()

    init {
        getImageReactions()
    }

    private fun getImageReactions() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.getImageReactions(id).collect {
                _viewState.value = _viewState.value?.copy(resultReactions = it)
            }
        }
    }

    fun reload() {
        getImageReactions()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): ImageViewModel
    }

    data class ViewState(
        val resultReactions: Result<List<ImageReaction>> = Empty()
    )
}