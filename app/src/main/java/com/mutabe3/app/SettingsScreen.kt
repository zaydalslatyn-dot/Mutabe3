package com.mutabe3.app

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class SettingsScreen(
    private val ctx: Context,
    private val onTrackingToggle: (Boolean) -> Unit
) {
    val root = ScrollView(ctx)
    private val statusTitle = TextView(ctx)
    private val statusSub = TextView(ctx)
    private val savedText = TextView(ctx)

    companion object {
        private const val PREF = "mutabe3_settings"

        fun isTrackingEnabled(ctx: Context): Boolean =
            ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getBoolean("tracking", true)
    }

    init {
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(Ui.dp(ctx, 16), Ui.dp(ctx, 40), Ui.dp(ctx, 16), Ui.dp(ctx, 16))
        }
        root.addView(col)

        col.addView(TextView(ctx).apply {
            text = "الإعدادات"
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = Ui.dp(ctx, 16) })

        // بطاقة حالة الاتصال
        statusTitle.apply {
            textSize = 18f
            gravity = Gravity.CENTER
        }
        statusSub.apply {
            textSize = 13f
            setTextColor(Ui.MUTED)
            gravity = Gravity.CENTER
        }
        savedText.apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, Ui.dp(ctx, 10), 0, 0)
        }
        val statusCard = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            background = Ui.rounded(Ui.CARD, Ui.dp(ctx, 18).toFloat())
            setPadding(Ui.dp(ctx, 18), Ui.dp(ctx, 18), Ui.dp(ctx, 18), Ui.dp(ctx, 18))
            addView(statusTitle)
            addView(statusSub)
            addView(savedText)
        }
        col.addView(statusCard, LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = Ui.dp(ctx, 16)
        })

        // مفتاح التتبع
        val sw = Switch(ctx).apply {
            isChecked = isTrackingEnabled(ctx)
            setOnCheckedChangeListener { _, on ->
                ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                    .edit().putBoolean("tracking", on).apply()
                onTrackingToggle(on)
            }
        }
        val labels = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(ctx).apply {
                text = "تتبع الموقع وحفظه"
                textSize = 16f
                setTextColor(Color.WHITE)
            })
            addView(TextView(ctx).apply {
                text = "يحفظ موقعك على الجوال حتى بدون إنترنت"
                textSize = 12f
                setTextColor(Ui.MUTED)
            })
        }
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = Ui.rounded(Ui.CARD, Ui.dp(ctx, 18).toFloat())
            setPadding(Ui.dp(ctx, 18), Ui.dp(ctx, 14), Ui.dp(ctx, 18), Ui.dp(ctx, 14))
            addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            addView(sw)
        }
        col.addView(row, LinearLayout.LayoutParams(-1, -2))

        refresh()
    }

    private fun isOnline(): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun refresh() {
        if (isOnline()) {
            statusTitle.text = "متصل بالإنترنت"
            statusTitle.setTextColor(Ui.GREEN)
            statusSub.text = "الاتصال يعمل بشكل طبيعي"
        } else {
            statusTitle.text = "لا يوجد إنترنت"
            statusTitle.setTextColor(Ui.RED)
            statusSub.text = "سيتم حفظ المواقع على الجوال تلقائياً"
        }
        val n = LocationStore.unsyncedCount(ctx)
        savedText.text = "المواقع المحفوظة غير المرسلة: $n"
    }
}
