package com.euromix.esupervisor.app.model.accounts

import com.euromix.esupervisor.app.model.accounts.entities.Account
import com.euromix.esupervisor.sources.account.entities.SignInResponseEntity
import kotlinx.coroutines.flow.Flow

interface AccountSource {

    suspend fun signIn(username: String, password: String): SignInResponseEntity

}