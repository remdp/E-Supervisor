package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.formattedDate
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DocsEmixListViewModel @Inject constructor(
    private val docsEmixRepository: DocsEmixRepository
) : BaseViewModel() {

    private val _docsEmix = MutableLiveData<Result<List<DocEmix>>>()
    val docsEmix = _docsEmix.share()

    private val _selection = MutableLiveData<DocsEmixSelection?>()
    val selection = _selection.share()

    private fun getDocsEmix() {
        viewModelScope.safeLaunch {

            val cf = docsEmixRepository.getDocsEmix(requestFromSelection())
            cf.collect { result ->
                _docsEmix.value = result
            }
        }
    }

    private fun requestFromSelection() =
        DocsEmixRequestEntity(
            startDate = _selection.value?.period?.let { formattedDate(it.first) },
            endDate = _selection.value?.period?.let { formattedDate(it.second) },
            tradingAgentId = _selection.value?.tradingAgent?.id,
            partnerId = _selection.value?.partner?.id,
            operationType = _selection.value?.operationType?.id,
            status = _selection.value?.status?.id
        )

    fun reload() { getDocsEmix() }

    fun updatePeriod(period: Pair<Date, Date>?) {
        _selection.value = if (_selection.value == null) DocsEmixSelection(
            period = period ?: _selection.value?.period
        )
        else _selection.value!!.copy(period = period)
    }

    fun updateSelection(selection: DocsEmixSelection?) {
        if ((_selection.value == null && selection == null) || selection != null)
            _selection.value = selection
    }
}