package com.euromix.esupervisor.sources.account

import com.euromix.esupervisor.app.model.accounts.AccountSource
import com.euromix.esupervisor.app.model.accounts.entities.Account
import com.euromix.esupervisor.sources.account.entities.SignInRequestEntity
import com.euromix.esupervisor.sources.account.entities.SignInResponseEntity
import com.euromix.esupervisor.sources.account.entities.TokenEntity
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import kotlinx.coroutines.flow.Flow

class RetrofitAccountSource(config: RetrofitConfig) : BaseRetrofitSource(config), AccountSource {

    private val accountApi = retrofit.create(AccountApi::class.java)

    override suspend fun signIn(username: String, password: String): SignInResponseEntity {

        return wrapRetrofitException {
            val signInRequestEntity = SignInRequestEntity.createInstance(username, password)
            accountApi.signIn(signInRequestEntity)
        }

    }
}