package com.euromix.esupervisor.app.model.settings

import com.euromix.esupervisor.app.enums.Role

interface AppSettings {

    fun getCurrentToken(): String?

    fun setCurrentToken(token: String?)

    fun setCurrentUserName(userName: String?)

    fun getCurrentUserName(): String

    fun setCurrentRole(role: Role)

    fun getCurrentRole(): Role

}