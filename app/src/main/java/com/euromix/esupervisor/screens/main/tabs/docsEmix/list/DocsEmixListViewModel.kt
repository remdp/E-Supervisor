package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.dateToJsonString
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DocsEmixListViewModel @Inject constructor(
    private val docsEmixRepository: DocsEmixRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData(ViewState(needLoading = true))
    val viewState = _viewState.share()

    fun reload() {
        getDocsEmix()
    }

    fun updatePeriod(period: Pair<Date, Date>?) {
        _viewState.value = viewState.value?.copy(period = period, needLoading = true)
    }

    fun updateSelection(selection: DocsEmixSelection?) {
        _viewState.value = viewState.value?.copy(selection = selection, needLoading = true)
    }

    fun afterUpdateState(
        adapter: DocsEmixAdapter,
        binding: DocEmixListFragmentBinding,
        fragment: BaseFragment
    ) {

        viewState.value?.let { stateValue ->
            if (stateValue.needLoading) {
                getDocsEmix()
            } else {
                adapter.docsEmix = stateValue.items
            }

            fragment.designByResult(
                stateValue.result,
                binding.root,
                binding.vResult,
                binding.srl
            )
        }
    }

    private fun getDocsEmix() {
        viewModelScope.safeLaunch {
            val cf = docsEmixRepository.getDocsEmix(requestFromSelection())
            cf.collect { result ->
                updateResult(result)
            }
        }
    }

    private fun requestFromSelection(): DocsEmixRequestEntity {

        val state = viewState.value

        return DocsEmixRequestEntity(
            startDate = state?.period?.let { dateToJsonString(it.first) },
            endDate = state?.period?.let { dateToJsonString(it.second) },
            tradingAgentId = state?.selection?.tradingAgent?.id,
            partnerId = state?.selection?.partner?.id,
            operationType = state?.selection?.operationType?.id,
            status = state?.selection?.status?.id
        )
    }

    private fun updateItems(items: List<DocEmix>) {
        _viewState.value = viewState.value?.copy(items = items, result = Success(items))
    }

    private fun updateResult(result: Result<*>) {

        viewState.value?.let { stateValue ->
            when (result) {
                is Pending -> _viewState.value =
                    stateValue.copy(needLoading = false, result = Pending())

                is Success -> updateItems(result.value as List<DocEmix>)
                is Error -> _viewState.value =
                    stateValue.copy(
                        needLoading = false,
                        result = Error(result.error)
                    )

                else -> _viewState.value = stateValue.copy(needLoading = false)
            }
        }
    }


    data class ViewState(
        val period: Pair<Date, Date>? = null,
        val selection: DocsEmixSelection? = null,
        val items: List<DocEmix> = listOf(),
        val needLoading: Boolean = false,
        val result: Result<List<DocEmix>> = Empty()
    )
}