package com.euromix.esupervisor.app.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.euromix.esupervisor.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ClickableViewAccessibility")
fun setPeriodSelection(
    et: EditText,
    period: Pair<Date, Date>?,
    fm: FragmentManager,
    periodUpdater: (period: Pair<Date, Date>?) -> Unit
) {
    val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
    val picker = dateRangePicker.build()

    designedPeriodView(et, period)

    et.setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val textLocation = IntArray(2)
                v.getLocationOnScreen(textLocation)

                if (event.rawX >= textLocation[0] + et.width - et.totalPaddingRight) {
                    // Right drawable was tapped
                    designedPeriodView(et,null)
                    periodUpdater(null)
                    return@setOnTouchListener true
                }

                picker.addOnPositiveButtonClickListener {

                    with(Pair(Date(it.first), Date(it.second))){
                        designedPeriodView(et,this)
                        periodUpdater(this)
                    }

                }
                picker.show(fm, picker.toString())
            }
        }
        return@setOnTouchListener true
    }
}

fun designedPeriodView(et: EditText, period: Pair<Date, Date>?) {

    if (period == null) {
        et.setText("")
        et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0)
    } else {
        et.setText(formatDateRange(Pair(period.first, period.second)) ?: "")
        et.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_calendar,
            0,
            R.drawable.ic_cross_gray_300,
            //if (showClearView) R.drawable.ic_cross_gray_300 else 0,
            0
        )
    }
}

private fun formatDateRange(
    range: Pair<Date, Date>,
    pattern: String = "dd.MM.yyyy",
    locale: Locale = Locale.getDefault()
): String? {
    val localDateFormat = SimpleDateFormat(pattern, locale)
    return try {
        "${localDateFormat.format(range.first)} - ${localDateFormat.format(range.second)}"
    } catch (ex: Throwable) {
        null
    }
}