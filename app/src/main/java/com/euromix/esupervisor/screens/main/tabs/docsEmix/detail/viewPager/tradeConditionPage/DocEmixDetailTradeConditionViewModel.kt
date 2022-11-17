package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocEmixDetailTradeConditionViewModel @Inject constructor(accountRepository: AccountRepository) :
    BaseViewModel(accountRepository) {

    private val _rowsTradeConditions = MutableLiveData<List<RowTradeCondition>>()
    val rowsTradeConditions = _rowsTradeConditions.share()

    fun setRowsTradeConditions(rows: List<RowTradeCondition>) {
        _rowsTradeConditions.value = rows
    }

}