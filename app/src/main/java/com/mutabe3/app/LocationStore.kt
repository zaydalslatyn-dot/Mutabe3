package com.mutabe3.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class LocationRecord(
    val lat: Double,
    val lng: Double,
    val time: Long,
    val synced: Boolean,
    val title: String
)

object LocationStore {
    private const val PREF = "mutabe3_store"
    private const val KEY = "records"

    fun all(ctx: Context): List<LocationRecord> {
        val s = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(s)
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            LocationRecord(
                o.getDouble("lat"),
                o.getDouble("lng"),
                o.getLong("time"),
                o.getBoolean("synced"),
                o.optString("title", "")
            )
        }
    }

    fun add(ctx: Context, r: LocationRecord) {
        save(ctx, all(ctx) + r)
    }

    fun markAllSynced(ctx: Context) {
        save(ctx, all(ctx).map { it.copy(synced = true) })
    }

    fun unsyncedCount(ctx: Context): Int = all(ctx).count { !it.synced }

    private fun save(ctx: Context, list: List<LocationRecord>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().apply {
                put("lat", it.lat)
                put("lng", it.lng)
                put("time", it.time)
                put("synced", it.synced)
                put("title", it.title)
            })
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }
}
