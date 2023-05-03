package com.euromix.esupervisor.screens.main.tabs

import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabsViewModel @Inject constructor(accountRepository: AccountRepository) :
    BaseViewModel(accountRepository) {

    fun getCurrentRole() = accountRepository?.getCurrentRole() ?: Role.SUPERVISOR
}