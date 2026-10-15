package com.mutabe3.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationTracker(
    private val ctx: Context,
    private val onLocation: (LocationRecord) -> Unit
) {
    private val client = LocationServices.getFusedLocationProviderClient(ctx)
    private var running = false

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val rec = LocationRecord(
                loc.latitude,
                loc.longitude,
                System.currentTimeMillis(),
                false,
                ""
            )
            LocationStore.add(ctx, rec)
            onLocation(rec)
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (running) return
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15000L)
            .setMinUpdateDistanceMeters(10f)
            .build()
        client.requestLocationUpdates(req, callback, Looper.getMainLooper())
        running = true
    }

    fun stop() {
        client.removeLocationUpdates(callback)
        running = false
    }

    fun isRunning(): Boolean = running
}
