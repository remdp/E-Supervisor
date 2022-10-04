package com.euromix.esupervisor.sources.account

import com.euromix.esupervisor.sources.account.entities.SignInRequestEntity
import com.euromix.esupervisor.sources.account.entities.SignInResponseEntity
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {

    @POST("sign_in")
    suspend fun signIn(@Body body: SignInRequestEntity): SignInResponseEntity
}