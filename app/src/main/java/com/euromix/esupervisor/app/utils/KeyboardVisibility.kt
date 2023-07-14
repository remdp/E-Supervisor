package com.euromix.esupervisor.app.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment

/**
 * Posts true on soft keyboard open, false on close.
 * This creates a strong reference to activity instance. Make sure to call ActivitySubscription.dispoose()
 * @param visibleThresholdDp if global activity layout changed more than by 100(default) dp
 *
 * @return activity subscription object that allows to unregister listener
 */
fun Fragment.addSoftKeyboardVisibilityListener(
    visibleThresholdDp: Int = 100,
    initialState: Boolean = false,
    listener: (Boolean) -> Unit): ActivitySubscription {

    return KeyboardVisibilitySubscription(requireActivity(), visibleThresholdDp, initialState, listener)
}

interface ActivitySubscription {
    fun dispose()
}

internal class KeyboardVisibilitySubscription(
    activity: Activity,
    visibleThresholdDp: Int,
    wasInitiallyOpened: Boolean,
    callback: (Boolean) -> Unit) : ActivitySubscription {

    private var activityConstHeight: Int = 0
    private var wasOpened: Boolean = wasInitiallyOpened

    private val displayRect = Rect()
    private val visibleThresholdPx = activity.resources.displayMetrics.density * visibleThresholdDp
    private val activityRoot: View = activity.findViewById(android.R.id.content)
    private val viewTreeObserver: ViewTreeObserver = activityRoot.viewTreeObserver
    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        // determine initial activity height
        if (activityConstHeight == 0) {
            activityConstHeight = activityRoot.rootView.height
        }

        //screen height minus keyboard
        val activityEffectiveHeight = displayRect.apply { activityRoot.getWindowVisibleDisplayFrame(this) }.height()

        val heightDiff = activityConstHeight - activityEffectiveHeight

        val isOpen = if (wasInitiallyOpened) {
            heightDiff <= visibleThresholdPx
        } else {
            heightDiff > visibleThresholdPx
        }

        if (isOpen != wasOpened) {
            callback(isOpen)
        }

        wasOpened = isOpen
    }

    init {
        callback(wasOpened)
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    override fun dispose() {
        viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }
}