package com.euromix.esupervisor.app.model.settings

interface AppSettings {

    fun getCurrentToken(): String?

    fun setCurrentToken(token: String?)

    fun setCurrentUserName(userName: String?)

    fun getCurrentUserName(): String

}