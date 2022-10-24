package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

class DocEmixDetailViewModel @AssistedInject constructor(
    @Assisted private val extId: String,
    accountRepository: AccountRepository,
    private val docEmixDetailRepository: DocEmixDetailRepository
) : BaseViewModel(accountRepository) {

    private val _docEmixDetail = MutableLiveData<Result<DocEmixDetail>>()
    val docEmixDetail = _docEmixDetail.share()

    init {
        getDocEmixDetail()
    }

    private fun getDocEmixDetail() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.getDocEmixDetail(extId).collect { result ->
                _docEmixDetail.value = result
            }
        }
    }

    fun acceptDocEmixDetail() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.acceptDocEmixDetail(extId).collect { result ->
                _docEmixDetail.value = result
            }
        }
    }

    fun rejectDocEmixDetail(reason: String) {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.rejectDocEmixDetail(extId, reason).collect { result ->
                _docEmixDetail.value = result
            }
        }
    }

    fun reload() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.reload(extId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(extId: String): DocEmixDetailViewModel
    }

}