package com.euromix.esupervisor.app.model.settings

import android.content.Context

class SharedPreferencesAppSettings(
    appContext: Context
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

    companion object {
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
        private const val PREF_CURRENT_USER_NAME = "currentUserName"
    }
}