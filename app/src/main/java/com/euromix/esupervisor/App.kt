package com.euromix.esupervisor

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        //Lingver.init(this, Locale.getDefault())
        Lingver.init(this, Locale.UK.language)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {
        fun beginCurrentMonth(): Date =
            Date.from(YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        fun endCurrentMonth(): Date =
            Date.from(YearMonth.now().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant())

        fun formattedDate(date: Date): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date)

        fun dateFromString(date: String) = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
    }

}