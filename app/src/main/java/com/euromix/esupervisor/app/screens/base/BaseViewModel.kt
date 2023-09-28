package com.euromix.esupervisor.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

//todo get rid of accountRepository in constructor
open class BaseViewModel(val accountRepository: AccountRepository?) :
    ViewModel() {

    constructor() : this(null)

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    private fun publishBaseErrorRes(errorRes: Int) {
        _showErrorMessageResEvent.publishEvent(errorRes)
    }

    private fun publishBaseErrorString(error: String) {
        _showErrorMessageEvent.publishEvent(error)
    }

    fun <T> processBaseException(e: Exception?, message: T?) {

        if (e is AuthException) publishBaseErrorRes(R.string.invalid_username_or_password)
        else if (message is Int) publishBaseErrorRes(message)
        else if (message is String) publishBaseErrorString(message)
        else if (message == null) publishBaseErrorRes(R.string.connection_error)
        //  else if (e is BackendException) publishBaseErrorString(e.message)
        else if (e != null) {
            e.message?.let { publishBaseErrorString(it) }
        }
    }

    fun logout() {
        accountRepository?.logout()
    }

}