package com.euromix.esupervisor

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import java.util.Locale


@HiltAndroidApp
class App : Application() {

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
            Date.from(
                YearMonth.now().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant()
            )

        fun getColor(context: Context, id: Int) = ContextCompat.getColor(context, id)

        fun getDrawable(context: Context, id: Int) = ContextCompat.getDrawable(context, id)

        fun getString(context: Context, id: Int) = context.getString(id)

    }

}