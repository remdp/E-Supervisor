package com.euromix.esupervisor.app.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.euromix.esupervisor.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ClickableViewAccessibility")
fun setDateSelection(
    tv: TextView,
    fm: FragmentManager,
    showClearView: Boolean = true,
    underlineIfNull: Boolean = false,
    dateUpdater: ((date: Date?) -> Unit)? = null
) {
    val datePicker = MaterialDatePicker.Builder.datePicker()
    val picker = datePicker.build()

    designedDateView(tv, null, showClearView, underlineIfNull)

    tv.setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val textLocation = IntArray(2)
                v.getLocationOnScreen(textLocation)

                if (event.rawX >= textLocation[0] + tv.width - tv.totalPaddingRight) {
                    // Right drawable was tapped
                    designedDateView(tv, null, showClearView, underlineIfNull)
                    dateUpdater?.invoke(null)
                    return@setOnTouchListener true
                }

                picker.addOnPositiveButtonClickListener { dateLong ->
                    designedDateView(tv, Date(dateLong), showClearView, underlineIfNull)
                    dateUpdater?.invoke(Date(dateLong))

                }
                picker.show(fm, picker.toString())
            }
        }
        return@setOnTouchListener true
    }
}

fun designedDateView(
    tv: TextView,
    date: Date?,
    showClearView: Boolean = true,
    underlineIfNull: Boolean = false
) {

    if (date == null) {
        tv.text = ""
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0)
        if (underlineIfNull) tv.setBackgroundResource(R.drawable.bg_underline_red)

    } else {
        tv.text = formatDateRange(date) ?: ""
        tv.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_calendar,
            0,
            if (showClearView) R.drawable.ic_cross_gray_300 else 0,
            0
        )

        tv.setBackgroundResource(R.drawable.bg_8dp_white_border)
    }
}

private fun formatDateRange(
    date: Date,
    pattern: String = "dd.MM.yyyy",
    locale: Locale = Locale.getDefault()
): String? {
    return try {
        SimpleDateFormat(pattern, locale).format(date)
    } catch (ex: Throwable) {
        null
    }
}