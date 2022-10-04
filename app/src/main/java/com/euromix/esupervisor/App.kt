package com.euromix.esupervisor

import android.app.Application
import com.yariksoffice.lingver.Lingver
import java.util.*

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Lingver.init(this, Locale.getDefault())
    }

}