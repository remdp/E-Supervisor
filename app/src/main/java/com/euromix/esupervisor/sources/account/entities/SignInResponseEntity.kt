package com.euromix.esupervisor.sources.account.entities

data class SignInResponseEntity(val data: TokenEntity)

data class TokenEntity(
    val access_token: String,
    val token_type: String
)