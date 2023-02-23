package com.euromix.esupervisor.app.model.account

import com.euromix.esupervisor.sources.account.entities.SignInResponseEntity

interface AccountSource {

    suspend fun signIn(username: String, password: String): SignInResponseEntity

}