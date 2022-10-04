package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share

class DocEmixDetailTradeConditionViewModel() :
    BaseViewModel() {

    private val _rowsTradeConditions = MutableLiveData<List<RowTradeCondition>>()
    val rowsTradeConditions = _rowsTradeConditions.share()

    fun setRowsTradeConditions(rows: List<RowTradeCondition>){
        _rowsTradeConditions.value = rows
    }

}