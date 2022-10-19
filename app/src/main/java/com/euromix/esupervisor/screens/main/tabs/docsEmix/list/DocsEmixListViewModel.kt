package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
//import com.euromix.esupervisor.app.Singletons.docsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocsEmixListViewModel @Inject constructor(
    //private val docsEmixRepository: DocsEmixRepository = Singletons.docsEmixRepository
    private val docsEmixRepository: DocsEmixRepository
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