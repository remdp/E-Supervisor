package com.euromix.esupervisor.screens.main.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Field
import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _clearPasswordEvent = MutableUnitLiveEvent()
    val clearPasswordEvent = _clearPasswordEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signIn(userName: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            accountRepository?.signIn(userName, password)
            launchTabsScreen()
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } catch (e: BackendException) {
            clearPasswordField()
            processException(e, e.message)
        } catch (e: ConnectionException) {
            processException(e, R.string.communication_error)
        } catch (e: Exception) {
            processException(e, null)
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyUserNameError = e.field == Field.Username,
            emptyPasswordError = e.field == Field.Password,
            signInInProgress = false
        )
    }

    private fun <T> processException(e: Exception, message: T?) {

        _state.value = _state.requireValue().copy(signInInProgress = false)
        processBaseException(e, message)

    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun clearPasswordField() = _clearPasswordEvent.publishEvent()

    private fun launchTabsScreen() = _navigateToTabsEvent.publishEvent()

    data class State(
        val emptyUserNameError: Boolean = false,
        val emptyPasswordError: Boolean = false,
        val signInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signInInProgress
        val enableViews: Boolean get() = !signInInProgress
    }
}