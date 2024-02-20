package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DocEmixDetailViewModel @AssistedInject constructor(
    @Assisted private val extId: String,
    private val docEmixDetailRepository: DocEmixDetailRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData(ViewState(needLoading = true))
    val viewState = _viewState.share()

    fun reload() {
        safeLaunch {
            getDocEmixDetail()
        }
    }

    private fun updateResult(result: Result<*>) {
        viewState.value?.let { stateValue ->
            when (result) {
                is Pending -> _viewState.value =
                    stateValue.copy(needLoading = false, result = Pending())

                is Success -> _viewState.value =
                    viewState.value?.copy(result = Success(result.value as DocEmixDetail))

                is Error -> _viewState.value =
                    stateValue.copy(
                        needLoading = false,
                        result = Error(result.error)
                    )
                else -> _viewState.value = stateValue.copy(needLoading = false)
            }
        }
    }
    fun afterUpdateState() {
        viewState.value?.let { stateValue ->
            if (stateValue.needLoading) {
                getDocEmixDetail()
            }
        }
    }
    private fun getDocEmixDetail() {
        safeLaunch {
            docEmixDetailRepository.getDocEmixDetail(extId).collect { result ->
                updateResult(result)
            }
        }
    }
    fun acceptDocEmixDetail() {
        safeLaunch {
            docEmixDetailRepository.acceptDocEmixDetail(extId).collect { result ->
                updateResult(result)
            }
        }
    }
    fun rejectDocEmixDetail(reason: String) {
        safeLaunch {
            docEmixDetailRepository.rejectDocEmixDetail(extId, reason).collect { result ->
                updateResult(result)
            }
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(extId: String): DocEmixDetailViewModel
    }
    data class ViewState(
        val needLoading: Boolean = false,
        val result: Result<DocEmixDetail> = Empty()
    )
}