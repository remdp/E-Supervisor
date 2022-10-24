package com.euromix.esupervisor.app.screens.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.AuthExceptionWithMessage
import com.euromix.esupervisor.app.model.BackendException
import com.euromix.esupervisor.app.model.ConnectionException
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.requireValue
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel (val accountRepository: AccountRepository) :
    ViewModel() {

   private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                processBaseException(null, R.string.connection_error)
            } catch (e: BackendException) {
                processBaseException(e, null)
            } catch (e: AuthExceptionWithMessage) {
                _showErrorMessageEvent.publishEvent(e.message ?: "")
                processBaseException(e, null)
            } catch (e: Exception) {
                processBaseException(e, null)
            }
        }
    }

    private fun publishBaseErrorRes(errorRes: Int){
        _showErrorMessageResEvent.publishEvent(errorRes)
    }
    private fun publishBaseErrorString(error: String){
        _showErrorMessageEvent.publishEvent(error)
    }

    fun <T> processBaseException(e: Exception?, message: T?) {

        if (message is Int) publishBaseErrorRes(message)
        else if (message is String)publishBaseErrorString(message)
        else if (e is AuthExceptionWithMessage) publishBaseErrorString(e.message)
        else if (e != null) {
            e.message?.let { publishBaseErrorString(it) }
        }

    }

    fun logout() {
        accountRepository.logout()
    }

}