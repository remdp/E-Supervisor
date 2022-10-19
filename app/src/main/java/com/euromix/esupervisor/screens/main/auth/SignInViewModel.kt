package ua.cn.stu.navcomponent.tabs.screens.main.auth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.accounts.AccountRepository
import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    accountRepository: AccountRepository,
    private val context: Context
) : BaseViewModel() {


    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _clearPasswordEvent = MutableUnitLiveEvent()
    val clearPasswordEvent = _clearPasswordEvent.share()

    private val _showAuthErrorToastEvent = MutableLiveEvent<String>()
    val showAuthToastEvent = _showAuthErrorToastEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signIn(userName: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            accountRepository.signIn(userName, password)
            launchTabsScreen()
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } catch (e: AuthException) {
      //      processAuthException()
        } catch (e: AuthExceptionWithMessage) {
            processAuthException(e.message)
        }
        catch (e: ConnectionException){
            processException(e, context.getString(R.string.communication_error))
        }
        catch (e: Exception) {
            processException(e)
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyUserNameError = e.field == Field.Username,
            emptyPasswordError = e.field == Field.Password,
            signInInProgress = false
        )
    }

    private fun processAuthException(message: String) {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        clearPasswordField()
        showAuthErrorToast(message)
    }

    private fun processException(e: Exception, message: String? = null) {
        _state.value = _state.requireValue().copy(signInInProgress = false)
        with(_showErrorToastEvent) {
            publishEvent( message?: e.cause?.message.toString())
        }
    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun clearPasswordField() = _clearPasswordEvent.publishEvent()

    private fun showAuthErrorToast(message: String) = _showAuthErrorToastEvent.publishEvent(message)

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