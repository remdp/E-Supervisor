package com.euromix.esupervisor.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.Singletons
import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.BackendException
import com.euromix.esupervisor.app.model.ConnectionException
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel() : ViewModel() {

    val accountRepository = Singletons.accountRepository

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()
//
    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()
//
//    private val _showAuthErrorAndRestartEvent = MutableUnitLiveEvent()
//    val showAuthErrorAndRestartEvent = _showAuthErrorAndRestartEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                // logError(e)
               // _showErrorMessageResEvent.publishEvent(R.string.connection_error)
                _showErrorMessageEvent.publishEvent("Connection error")
            } catch (e: BackendException) {
                //logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: AuthException) {
                //logError(e)
                //_showAuthErrorAndRestartEvent.publishEvent()
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: Exception) {
                //logError(e)
                //_showErrorMessageResEvent.publishEvent(R.string.internal_error)
                _showErrorMessageEvent.publishEvent("R.string.internal_error")
            }
        }
    }

    fun logout() {
        accountRepository.logout()
    }

}