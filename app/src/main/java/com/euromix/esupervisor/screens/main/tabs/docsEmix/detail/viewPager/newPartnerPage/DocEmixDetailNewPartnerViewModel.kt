package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import kotlinx.parcelize.Parcelize

class DocEmixDetailNewPartnerViewModel : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState = _viewState.share()

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }
}

@Parcelize
class ViewState(val workingName: String?, val innerDS: String?, val edrpou: String?) : Parcelable
