package com.euromix.esupervisor.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
     accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _account = MutableLiveData<String>()
    val account = _account.share()

    private val _role = MutableLiveData<Role>()
    val role = _role.share()

    init {
        _account.value = accountRepository.getCurrentUser()
        _role.value = getCurrentRole()
    }

    fun getCurrentRole() = accountRepository?.getCurrentRole() ?: Role.SUPERVISOR
}