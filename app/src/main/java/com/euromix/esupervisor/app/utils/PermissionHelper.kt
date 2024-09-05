package com.euromix.esupervisor.app.utils

import android.Manifest
import android.os.Build

object PermissionHelper {

    fun getImagePermission(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    fun getWriteStoragePermissionList(): List<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            emptyList()
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        else -> emptyList()
    }

}