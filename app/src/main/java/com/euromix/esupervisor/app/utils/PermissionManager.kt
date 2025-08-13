package com.euromix.esupervisor.app.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.permissionManager(): PermissionManager {
    var _onAccepted: (() -> Unit)? = null
    var _onDenied: (() -> Unit)? = null

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }

            if (allGranted) {
                _onAccepted?.invoke()
            } else {
                _onDenied?.invoke()
            }
        }

    return PermissionManager(
        permissionChecker = { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        },
        requestPermissionLauncher = { permissions, onAccepted, onDenied ->
            _onAccepted = onAccepted
            _onDenied = onDenied
            requestPermissionLauncher.launch(permissions)
        }
    )
}

class PermissionManager(
    private val permissionChecker: (String) -> Boolean,
    private val requestPermissionLauncher: (Array<String>, () -> Unit, () -> Unit) -> Unit
) {
    fun requestPermissions(
        requestedPermissions: Array<String>,
        onAccepted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val notGrantedPermissions = requestedPermissions.filterNot { permissionChecker(it) }

        if (notGrantedPermissions.isEmpty()) {
            onAccepted()
        } else {
            requestPermissionLauncher(notGrantedPermissions.toTypedArray(), onAccepted, onDenied)
        }
    }
}