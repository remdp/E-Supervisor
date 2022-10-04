package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.Singletons
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.model.Result
import kotlinx.coroutines.launch

class DocsEmixListViewModel(
    private val docsEmixRepository: DocsEmixRepository = Singletons.docsEmixRepository
) : BaseViewModel() {

    private val _docsEmix = MutableLiveData<Result<List<DocEmix>>>()
    val docsEmix = _docsEmix.share()

    init {
        getDocsEmix()
    }

    fun getDocsEmix() {
        viewModelScope.launch {

            val cf = docsEmixRepository.getDocsEmix()
            cf.collect { result ->
                _docsEmix.value = result
            }
        }
    }

    fun reload() {
        docsEmixRepository.reloadDocsEmix()
    }
}