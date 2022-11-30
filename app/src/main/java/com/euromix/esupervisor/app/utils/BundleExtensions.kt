package com.euromix.esupervisor.app.utils

import android.os.Build
import android.os.Bundle

inline fun <reified T> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") get(key) as? T
}