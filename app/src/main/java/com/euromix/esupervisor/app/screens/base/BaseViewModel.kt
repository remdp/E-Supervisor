package com.euromix.esupervisor.app.screens.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

//todo get rid of accountRepository in constructor
open class BaseViewModel @Inject constructor() :
    ViewModel() {

    @Inject
    lateinit var accountRepository: AccountRepository

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    private var currentJob: Job? = null

    fun safeLaunch(cancelPreviousJob: Boolean = false, block: suspend () -> Unit) {

        if (cancelPreviousJob && currentJob?.isActive == true) {
            currentJob?.cancel()
            currentJob?.invokeOnCompletion {
                currentJob = viewModelScope.launch {
                    block()
                }
            }
        } else {
            currentJob = viewModelScope.launch {
                block()
            }
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
        accountRepository.logout()
    }

}