package com.euromix.esupervisor

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        //Lingver.init(this, Locale.getDefault())
        Lingver.init(this, Locale.UK.language)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}