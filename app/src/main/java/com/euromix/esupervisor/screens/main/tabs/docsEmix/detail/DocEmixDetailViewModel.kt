package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.databinding.TabHeaderBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.VPFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
        viewModelScope.safeLaunch {
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
        viewModelScope.safeLaunch {
            docEmixDetailRepository.getDocEmixDetail(extId).collect { result ->
                updateResult(result)
            }
        }
    }
    fun acceptDocEmixDetail() {
        viewModelScope.safeLaunch {
            docEmixDetailRepository.acceptDocEmixDetail(extId).collect { result ->
                updateResult(result)
            }
        }
    }
    fun rejectDocEmixDetail(reason: String) {
        viewModelScope.safeLaunch {
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