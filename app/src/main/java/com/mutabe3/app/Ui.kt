package com.mutabe3.app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

object Ui {
    val BG = Color.parseColor("#0A1226")
    val CARD = Color.parseColor("#111C36")
    val BLUE = Color.parseColor("#1E7BFF")
    val GREEN = Color.parseColor("#2ECC71")
    val RED = Color.parseColor("#FF5A5F")
    val MUTED = Color.parseColor("#8FA3C7")

    fun dp(ctx: Context, v: Int): Int =
        (v * ctx.resources.displayMetrics.density).toInt()

    fun rounded(color: Int, radiusPx: Float): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusPx
        }
}
