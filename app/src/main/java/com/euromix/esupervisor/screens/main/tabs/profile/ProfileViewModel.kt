package ua.cn.stu.navcomponent.tabs.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.Singletons
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import kotlinx.coroutines.launch

class ProfileViewModel(
    accountRepository: AccountRepository = Singletons.accountRepository
) : BaseViewModel() {

    private val _account = MutableLiveData<String>()
    val account = _account.share()

    init {
        _account.value = accountRepository.getCurrentUser()
    }
}