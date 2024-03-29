package com.euromix.esupervisor.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.utils.customIndicator.CustomProgressIndicator

fun View.showKeyboard() {
    requestFocus()
    if (!context.isHardKeyboardAvailable()) {
        context?.getSystemService(Context.INPUT_METHOD_SERVICE)?.safeAs<InputMethodManager>()
            ?.showSoftInput(this, 0)
    }
}

fun View.hideKeyboard() {
    context?.getSystemService(Context.INPUT_METHOD_SERVICE)?.safeAs<InputMethodManager>()
        ?.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setTextColorStatus(status: Status) {
    setTextColor(context.colorStateList(status.getColor()))
}

fun TextView.setIconStatus(status: Status) {
    setCompoundDrawablesWithIntrinsicBounds(status.getIconId(), 0, 0, 0)
}

fun TextView.setTextColorTaskState(taskState: TaskState) {
    setTextColor(context.colorStateList(taskState.getColor()))
}

fun TextView.setIconTaskState(taskState: TaskState) {
    setCompoundDrawablesWithIntrinsicBounds(taskState.getIconId(), 0, 0, 0)
}

fun ImageButton.setIconCollapse(collapse: Boolean) {
    setImageResource(if (collapse) R.drawable.ic_arrow_drop_down_black else R.drawable.ic_arrow_drop_up_black)
}

fun EditText.setEtOnEditorActionListener(searchFunction: (String) -> Unit) {

    setOnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            searchFunction((v as TextView).text.toString())

            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                v.windowToken,
                0
            )
        }
        return@setOnEditorActionListener true
    }
}

fun EditText.setOnClickListenerServerSelection(onCLick: (ServerPair?) -> Unit) {

    setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val textLocation = IntArray(2)
                getLocationOnScreen(textLocation)
                if (event.rawX <= textLocation[0] + getTotalPaddingLeft()) {
                    // Left drawable was tapped

                }
                if (event.rawX >= textLocation[0] + getWidth() - getTotalPaddingRight()) {
                    // Right drawable was tapped
                    onCLick.invoke(null)
                }
            }
        }
        return@setOnTouchListener false
    }
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.setOnClickListenerLocalSelection(
    itemsList: List<ServerPair>,
    updaterSelection: (ServerPair?) -> Unit,
    onClick: (
        itemsList: List<ServerPair>,
        updaterSelection: (ServerPair?) -> Unit,
        anchor: View,
        click: Int,
        emptyChecker: () -> Boolean
    ) -> Unit,
    emptyChecker: () -> Boolean
) {

    setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val textLocation = IntArray(2)
                getLocationOnScreen(textLocation)
                if (event.rawX <= textLocation[0] + totalPaddingLeft) {
                    // Left drawable was tapped

                } else if (event.rawX >= textLocation[0] + width - totalPaddingRight) {
                    // Right drawable was tapped
                    onClick(itemsList, updaterSelection, v, 1, emptyChecker)
                    //return@setOnTouchListener true
                } else {
                    onClick(itemsList, updaterSelection, v, 0, emptyChecker)
                }
            }
        }
        return@setOnTouchListener true
    }
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.setDrawableOnClickListener(onClick: () -> Unit) {

    setOnTouchListener { v, event ->

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val textLocation = IntArray(2)
                getLocationOnScreen(textLocation)
                if (event.rawX <= textLocation[0] + totalPaddingLeft) {
                    // Left drawable was tapped

                } else if (event.rawX >= textLocation[0] + width - totalPaddingRight) {
                    // Right drawable was tapped
                    onClick()
                   // return@setOnTouchListener false
                } else {
                    onClick()
                }
            }
        }
        return@setOnTouchListener true
    }

}

fun View.isPartiallyShow(): Boolean {
    val rect = Rect()
    return !(getGlobalVisibleRect(rect) && height == rect.height() && width == rect.width())
}

fun CustomProgressIndicator.draw(
    totalPlan: Double,
    totalFact: Double,
    indicatorWidth: Int = 10
) {

    val colorPi = getColor(
        context,
        if (totalFact < totalPlan) R.color.blue else R.color.green
    )

    val percentage = totalFact / totalPlan
    var startAngleC = (270 + (percentage - percentage.toInt()) * 360).toInt()
    if (startAngleC > 360) startAngleC -= 360

    drawCircularProgress(
        false,
        true,
        colorPi,
        indicatorWidth,
        startAngleC,
        100,
        100,
        CustomProgressIndicator.ProgressTextAdapter {
            return@ProgressTextAdapter (percentage * 100).toInt().toString() + "%"
        })
}

fun CustomProgressIndicator.clear(indicatorWidth: Int = 10) {

    isAnimationEnabled = false
    setProgressBackgroundStrokeWidthDp(indicatorWidth)
    setDotWidthDp(0)
    progressBackgroundColor = ContextCompat.getColor(context, R.color.gray_100)
    setGradient(
        CustomProgressIndicator.NO_GRADIENT,
        ContextCompat.getColor(context, R.color.white)
    )

    setProgress(0.0, 0.0)
    textColor = getColor(context, R.color.gray_200)

    setProgressTextAdapter { return@setProgressTextAdapter "0%" }

}

fun Context.colorStateList(@ColorRes color: Int): ColorStateList {
    return ColorStateList.valueOf(ContextCompat.getColor(this, color))
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}


