package com.euromix.esupervisor.app.utils

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.newPermissionManager(
    onAccepted: () -> Unit,
    onDenied: () -> Unit = {}
): NewPermissionManager {

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val allGranted = !permissions.any { map -> !map.value }

            if (allGranted) {
                onAccepted()
            } else {
                if (onDenied != null) {
                    onDenied()
                }
            }
        }

    return NewPermissionManager(
        permissionChecker = { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        },
        requestPermissionLauncher
    )
}

class NewPermissionManager(
    private val permissionChecker: (String) -> Boolean,
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>>
) {

    fun requestPermissions(requestedPermissions: Array<String>, onAccepted: () -> Unit) {

        val notGrantedPermissions = requestedPermissions.filterNot { permissionChecker(it) }
        if (notGrantedPermissions.isEmpty()) {
            onAccepted()
        } else {
            requestPermissionLauncher.launch(notGrantedPermissions.toTypedArray())
        }
    }

}