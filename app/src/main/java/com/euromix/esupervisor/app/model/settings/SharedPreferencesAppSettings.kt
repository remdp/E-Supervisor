package com.euromix.esupervisor.app.model.settings

import android.content.Context
import com.euromix.esupervisor.app.enums.Role
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesAppSettings @Inject constructor(
    @ApplicationContext appContext: Context
) : AppSettings {

    private val sharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val editor = sharedPreferences.edit()

    override fun getCurrentToken(): String? {
        return sharedPreferences.getString(PREF_CURRENT_ACCOUNT_TOKEN, null)
    }

    override fun setCurrentToken(token: String?) {

        if (token == null)
            editor.remove(PREF_CURRENT_ACCOUNT_TOKEN)
        else
            editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
        editor.apply()

    }

    override fun setCurrentUserName(userName: String?) {
        editor.putString(PREF_CURRENT_USER_NAME, userName)
    }

    override fun getCurrentUserName(): String {
        return sharedPreferences.getString(PREF_CURRENT_USER_NAME, "") ?: "undefined"
    }

    override fun setCurrentRole(role: Role) {
        editor.putString(PREF_CURRENT_ROLE, role.toString())
        editor.apply()
    }

    override fun getCurrentRole(): Role = Role.toRole(
        sharedPreferences.getString(
            PREF_CURRENT_ROLE,
            Role.SUPERVISOR.toString()
        )
    )

    companion object {
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
        private const val PREF_CURRENT_USER_NAME = "currentUserName"
        private const val PREF_CURRENT_ROLE = "role"
    }
}