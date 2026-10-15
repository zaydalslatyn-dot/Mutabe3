package com.mutabe3.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mapScreen: MapScreen
    private lateinit var historyScreen: HistoryScreen
    private lateinit var settingsScreen: SettingsScreen
    private lateinit var tracker: LocationTracker
    private lateinit var content: FrameLayout
    private val tabs = mutableListOf<TextView>()
    private var current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tracker = LocationTracker(this) { rec ->
            mapScreen.update(rec)
            if (current == 1) historyScreen.refresh()
            if (current == 2) settingsScreen.refresh()
        }

        mapScreen = MapScreen(this)
        historyScreen = HistoryScreen(this)
        settingsScreen = SettingsScreen(this) { on ->
            if (on) startTrackingIfAllowed() else tracker.stop()
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Ui.BG)
        }

        content = FrameLayout(this)
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setBackgroundColor(Ui.CARD)
        }
        val names = listOf("📍 الخريطة", "🕒 السجل", "⚙️ الإعدادات")
        names.forEachIndexed { i, n ->
            val t = TextView(this).apply {
                text = n
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, Ui.dp(this@MainActivity, 14), 0, Ui.dp(this@MainActivity, 14))
                setOnClickListener { showTab(i) }
            }
            tabs.add(t)
            bar.addView(t, LinearLayout.LayoutParams(0, -2, 1f))
        }
        root.addView(bar, LinearLayout.LayoutParams(-1, -2))

        setContentView(root)
        showTab(0)
        requestLocationPermission()
    }

    private fun showTab(i: Int) {
        current = i
        content.removeAllViews()
        when (i) {
            0 -> content.addView(mapScreen.root)
            1 -> {
                historyScreen.refresh()
                content.addView(historyScreen.root)
            }
            else -> {
                settingsScreen.refresh()
                content.addView(settingsScreen.root)
            }
        }
        tabs.forEachIndexed { idx, t ->
            t.setTextColor(if (idx == i) Ui.BLUE else Ui.MUTED)
        }
    }

    private fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (hasPermission()) {
            startTrackingIfAllowed()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }
    }

    private fun startTrackingIfAllowed() {
        if (hasPermission() && SettingsScreen.isTrackingEnabled(this)) {
            tracker.start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) startTrackingIfAllowed()
    }

    override fun onResume() {
        super.onResume()
        mapScreen.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapScreen.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.stop()
    }
}
