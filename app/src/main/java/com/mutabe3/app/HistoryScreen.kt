package com.mutabe3.app

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryScreen(private val ctx: Context) {

    val root = LinearLayout(ctx)
    private val list = LinearLayout(ctx)
    private val chips = mutableListOf<TextView>()
    private var filter = 0
    private val names = listOf("جميع المواقع", "غير مرسلة", "تم إرسالها")

    init {
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(Ui.dp(ctx, 16), Ui.dp(ctx, 40), Ui.dp(ctx, 16), 0)

        val title = TextView(ctx).apply {
            text = "سجل المواقع"
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        root.addView(title, LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = Ui.dp(ctx, 14)
        })

        val row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        names.forEachIndexed { i, n ->
            val chip = TextView(ctx).apply {
                text = n
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(Ui.dp(ctx, 14), Ui.dp(ctx, 8), Ui.dp(ctx, 14), Ui.dp(ctx, 8))
                setOnClickListener {
                    filter = i
                    styleChips()
                    refresh()
                }
            }
            chips.add(chip)
            row.addView(chip, LinearLayout.LayoutParams(-2, -2).apply {
                marginEnd = Ui.dp(ctx, 8)
            })
        }
        root.addView(row, LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = Ui.dp(ctx, 12)
        })

        list.orientation = LinearLayout.VERTICAL
        val scroll = ScrollView(ctx)
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        styleChips()
        refresh()
    }

    private fun styleChips() {
        chips.forEachIndexed { i, c ->
            val on = i == filter
            c.setTextColor(Color.WHITE)
            c.background = Ui.rounded(
                if (on) Ui.BLUE else Ui.CARD,
                Ui.dp(ctx, 18).toFloat()
            )
        }
    }

    fun refresh() {
        list.removeAllViews()
        val all = LocationStore.all(ctx).reversed()
        val shown = when (filter) {
            1 -> all.filter { !it.synced }
            2 -> all.filter { it.synced }
            else -> all
        }

        if (shown.isEmpty()) {
            list.addView(TextView(ctx).apply {
                text = "لا توجد مواقع محفوظة بعد"
                textSize = 15f
                setTextColor(Ui.MUTED)
                gravity = Gravity.CENTER
                setPadding(0, Ui.dp(ctx, 40), 0, 0)
            })
            return
        }

        val fmt = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
        shown.forEach { r ->
            val name = if (r.title.isNotEmpty()) r.title
            else String.format(Locale.US, "%.5f , %.5f", r.lat, r.lng)

            val t1 = TextView(ctx).apply {
                text = name
                textSize = 16f
                setTextColor(Color.WHITE)
            }
            val t2 = TextView(ctx).apply {
                text = fmt.format(Date(r.time))
                textSize = 13f
                setTextColor(Ui.MUTED)
            }
            val t3 = TextView(ctx).apply {
                text = if (r.synced) "● تم الإرسال" else "● غير مرسل"
                textSize = 13f
                setTextColor(if (r.synced) Ui.GREEN else Ui.RED)
            }
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                background = Ui.rounded(Ui.CARD, Ui.dp(ctx, 16).toFloat())
                setPadding(Ui.dp(ctx, 16), Ui.dp(ctx, 12), Ui.dp(ctx, 16), Ui.dp(ctx, 12))
                addView(t1)
                addView(t2)
                addView(t3)
            }
            list.addView(card, LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = Ui.dp(ctx, 10)
            })
        }
    }
}
