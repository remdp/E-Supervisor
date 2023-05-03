package com.euromix.esupervisor.app.utils

import android.content.Context
import android.content.res.Configuration

fun Context.isHardKeyboardAvailable(): Boolean {
    return resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
}