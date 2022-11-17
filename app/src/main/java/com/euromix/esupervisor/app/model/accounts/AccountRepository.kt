package com.euromix.esupervisor.app.model.accounts

import com.euromix.esupervisor.app.enums.Field
import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.settings.AppSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountSource: AccountSource,
    private val appSettings: AppSettings
) {

    fun isSignedIn(): Boolean {
        return appSettings.getCurrentToken() != null
    }

    suspend fun signIn(userName: String, password: String) {

        if (userName.isBlank()) throw EmptyFieldException(Field.Username)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        appSettings.setCurrentUserName(userName)

        val token = try {
            accountSource.signIn(userName, password)
        } catch (e: Exception) {
            throw e
        }
        appSettings.setCurrentToken(token.data.access_token)
    }

    fun getCurrentUser(): String {
        return appSettings.getCurrentUserName()
    }

    fun logout() {
        appSettings.setCurrentToken(null)
        appSettings.setCurrentUserName(null)
    }
}