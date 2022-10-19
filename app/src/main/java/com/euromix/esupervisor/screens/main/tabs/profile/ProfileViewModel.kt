package ua.cn.stu.navcomponent.tabs.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
     accountRepository: AccountRepository
) : BaseViewModel() {

    private val _account = MutableLiveData<String>()
    val account = _account.share()

    init {
        _account.value = accountRepository.getCurrentUser()
    }
}