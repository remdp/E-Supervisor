package com.euromix.esupervisor.app.utils

import android.content.pm.PackageManager
import android.util.SparseArray
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.util.containsKey
import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

/**
 * @return [PermissionManager] for Fragment.
 */
fun Fragment.permissionManager(): PermissionManager {
    val requestCode = AtomicInteger()

    lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    return PermissionManager(
        permissionChecker = { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        },
        permissionRequester = { code, permissions ->
            requestPermissions(permissions, code)
        },
        requestCodeGenerator = { requestCode.incrementAndGet() }
    )
}

class PermissionManager(
    private val permissionChecker: (String) -> Boolean,
    private val permissionRequester: (Int, Array<String>) -> Unit,
    private val requestCodeGenerator: () -> Int
) {

    private val requestHandlers = SparseArray<PermissionResultHandler>()

    fun request(permission: String, onAccepted: () -> Unit, onDenied: (List<String>) -> Unit) {
        return request(listOf(permission), onAccepted, onDenied)
    }

    fun request(permissions: List<String>, onAccepted: () -> Unit, onDenied: (List<String>) -> Unit) {
        val notGrantedPermissions = permissions.filterNot { permissionChecker(it) }
        if (notGrantedPermissions.isEmpty()) {
            onAccepted()
        } else {
            val requestCode = requestCodeGenerator()
            val handler = PermissionResultHandler(onAccepted, onDenied)
            requestHandlers.put(requestCode,handler)
            permissionRequester(requestCode, notGrantedPermissions.toTypedArray())
        }
    }

    fun onRequestResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestHandlers.containsKey(requestCode)) {
            requestHandlers.get(requestCode).onPermissionResult(permissions, grantResults)
            requestHandlers.remove(requestCode)
        }
    }

    class PermissionResultHandler(private val onAccepted: () -> Unit, private val onDenied: (List<String>) -> Unit) {

        fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
            val deniedGrants = grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
            if (deniedGrants.isEmpty()) onAccepted() else onDenied(deniedGrants.map { permissions[it] })
        }
    }
}