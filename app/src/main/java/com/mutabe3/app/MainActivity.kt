package com.mutabe3.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0A1226"))
        }

        val title = TextView(this).apply {
            text = "مُتابع"
            textSize = 44f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val sub = TextView(this).apply {
            text = "مَوقعهم .. بين يديك"
            textSize = 18f
            setTextColor(Color.parseColor("#8FB8FF"))
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(sub)
        setContentView(root)
    }
}
