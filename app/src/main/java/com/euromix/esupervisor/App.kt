package com.euromix.esupervisor

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*


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

        fun dateToJsonString(date: Date): String =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date)

        fun dateToString(date: Date): String =
            SimpleDateFormat("dd.MM.yyyy").format(date)

        fun stringToDate(stringDate: String) =
            Date.from(LocalDateTime.parse(stringDate).toInstant(ZoneOffset.UTC))


        fun getColor(context: Context, id: Int) = ContextCompat.getColor(context, id)

        fun getDrawable(context: Context, id: Int) = ContextCompat.getDrawable(context, id)

        fun getString(context: Context, id: Int) = context.getString(id)

    }

}