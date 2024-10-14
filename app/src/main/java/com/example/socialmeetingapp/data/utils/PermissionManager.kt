package com.example.socialmeetingapp.data.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionManager(
    activity: ComponentActivity,
) {
    private val context: Context = activity
    private val shouldShowPermissionRationale: (String) -> Boolean = activity::shouldShowRequestPermissionRationale
    private var onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private val requestMultiplePermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val isGranted = result.values.all { it == true }
            if (!isGranted) {
                // As above: Handle the case where permissions are denied.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private fun requestPermissions(permissionsToBeRequested: List<String>) {
        if (permissionsToBeRequested.size > 1) {
            requestMultiplePermissionsLauncher.launch(permissionsToBeRequested.toTypedArray())
        } else {
            permissionsToBeRequested.firstOrNull()?.let { requestPermissionLauncher.launch(it) }
        }
    }

    fun checkPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        this.onPermissionsGranted = onPermissionsGranted

        val permissionsToBeRequested = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        val shouldShowRequestPermissionRationale = permissionsToBeRequested.any {
            shouldShowPermissionRationale.invoke(it)
        }

        when {
            permissionsToBeRequested.isEmpty() -> onPermissionsGranted?.invoke(true)
            shouldShowRequestPermissionRationale -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                onPermissionsGranted?.invoke(false)
            }
            else -> requestPermissions(permissionsToBeRequested)
        }
    }

    companion object {
        const val FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_COARSE_LOCATION

    }

}