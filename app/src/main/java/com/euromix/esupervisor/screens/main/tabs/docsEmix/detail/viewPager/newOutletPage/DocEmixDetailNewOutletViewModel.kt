package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@HiltViewModel
class DocEmixDetailNewOutletViewModel @Inject constructor(accountRepository: AccountRepository) :
    BaseViewModel(accountRepository) {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState = _viewState.share()

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }
}

@Parcelize
class ViewState(
    val outletName: String?,
    val institutionType: String?,
    val institutionNumber: String?,
    val generalOutlet: String?,
    val newGeneralOutlet: Boolean?,
    val outletFormat: String?,
    val locality: String?,
    val street: String?,
    val houseNumber: String?
) : Parcelable