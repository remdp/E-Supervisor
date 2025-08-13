package com.euromix.esupervisor.app.utils

import android.content.Context
import android.net.Uri
import android.util.Base64

fun base64StringFromUri(context: Context, uri: Uri): String =
    Base64.encodeToString(
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() },
        Base64.DEFAULT
    )