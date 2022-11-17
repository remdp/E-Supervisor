package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DocEmixDetailVPViewModel @AssistedInject constructor(
    @Assisted inputDetailData: DocEmixDetail,
    accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _detailData = MutableLiveData<DocEmixDetail>()
    var detailData = _detailData.share()

    init {
        _detailData.value = inputDetailData
    }

    @AssistedFactory
    interface Factory {
        fun create(inputDetailData: DocEmixDetail): DocEmixDetailVPViewModel
    }
}