package com.euromix.esupervisor.sources.account.entities

data class SignInRequestEntity(
    val identity: IdentityEntity
) {

    companion object {
        fun createInstance(login: String, password: String): SignInRequestEntity {
            val identity = IdentityEntity(login, password)
            return SignInRequestEntity(identity)
        }
    }

}

data class IdentityEntity(
    val login: String,
    val password: String
)
