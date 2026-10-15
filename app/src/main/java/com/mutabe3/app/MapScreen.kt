package com.mutabe3.app

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapScreen(private val ctx: Context) {

    val root = FrameLayout(ctx)
    private val map: MapView
    private val marker: Marker
    private val coordText: TextView
    private val timeText: TextView
    private var centered = false
    private var last: GeoPoint? = null

    init {
        Configuration.getInstance().userAgentValue = ctx.packageName
        Configuration.getInstance().osmdroidBasePath = ctx.filesDir
        Configuration.getInstance().osmdroidTileCache = ctx.cacheDir

        map = MapView(ctx).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(16.0)
            controller.setCenter(GeoPoint(24.7136, 46.6753))
        }
        root.addView(map, FrameLayout.LayoutParams(-1, -1))

        marker = Marker(map).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "موقعي"
            isEnabled = false
        }
        map.overlays.add(marker)

        // العنوان في الأعلى
        val title = TextView(ctx).apply {
            text = "مُتابع"
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = Ui.rounded(Ui.CARD, Ui.dp(ctx, 16).toFloat())
            setPadding(Ui.dp(ctx, 24), Ui.dp(ctx, 10), Ui.dp(ctx, 24), Ui.dp(ctx, 10))
        }
        root.addView(title, FrameLayout.LayoutParams(-2, -2).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            topMargin = Ui.dp(ctx, 36)
        })

        // أزرار الخريطة
        val buttons = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL }
        buttons.addView(circleBtn("◎") { centerOnMe() })
        buttons.addView(circleBtn("+") { map.controller.zoomIn() })
        buttons.addView(circleBtn("−") { map.controller.zoomOut() })
        root.addView(buttons, FrameLayout.LayoutParams(-2, -2).apply {
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            rightMargin = Ui.dp(ctx, 12)
        })

        // بطاقة الموقع الحالي
        coordText = TextView(ctx).apply {
            text = "جاري تحديد موقعك..."
            textSize = 16f
            setTextColor(Color.WHITE)
        }
        timeText = TextView(ctx).apply {
            text = ""
            textSize = 13f
            setTextColor(Ui.MUTED)
        }
        val head = TextView(ctx).apply {
            text = "الموقع الحالي"
            textSize = 13f
            setTextColor(Ui.BLUE)
        }
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = Ui.rounded(Ui.CARD, Ui.dp(ctx, 18).toFloat())
            setPadding(Ui.dp(ctx, 18), Ui.dp(ctx, 14), Ui.dp(ctx, 18), Ui.dp(ctx, 14))
            addView(head)
            addView(coordText)
            addView(timeText)
        }
        root.addView(card, FrameLayout.LayoutParams(-1, -2).apply {
            gravity = Gravity.BOTTOM
            leftMargin = Ui.dp(ctx, 14)
            rightMargin = Ui.dp(ctx, 14)
            bottomMargin = Ui.dp(ctx, 14)
        })
    }

    private fun circleBtn(label: String, onClick: () -> Unit): View {
        val size = Ui.dp(ctx, 46)
        return TextView(ctx).apply {
            text = label
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = Ui.rounded(Ui.CARD, size / 2f)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                bottomMargin = Ui.dp(ctx, 10)
            }
            setOnClickListener { onClick() }
        }
    }

    fun update(rec: LocationRecord) {
        val p = GeoPoint(rec.lat, rec.lng)
        last = p
        marker.position = p
        marker.isEnabled = true
        if (!centered) {
            map.controller.setCenter(p)
            centered = true
        }
        coordText.text = String.format(Locale.US, "%.5f , %.5f", rec.lat, rec.lng)
        timeText.text = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
            .format(Date(rec.time))
        map.invalidate()
    }

    fun centerOnMe() {
        last?.let { map.controller.animateTo(it) }
    }

    fun onResume() = map.onResume()
    fun onPause() = map.onPause()
}
