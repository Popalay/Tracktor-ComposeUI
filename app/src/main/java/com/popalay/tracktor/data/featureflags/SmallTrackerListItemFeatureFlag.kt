package com.popalay.tracktor.data.featureflags

import android.content.SharedPreferences

interface SmallTrackerListItemFeatureFlag {
    fun isSmallTrackerListItemEnabled(): Boolean
    fun setSmallTrackerListItemEnabled(value: Boolean)
}

class RealSmallTrackerListItemFeatureFlag(
    private val sharedPreferences: SharedPreferences
) : SmallTrackerListItemFeatureFlag {
    companion object {
        private const val KEY_SMALL_TRACKER_LIST_ITEM = "KEY_SMALL_TRACKER_LIST_ITEM"
    }

    override fun isSmallTrackerListItemEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_SMALL_TRACKER_LIST_ITEM, false)

    override fun setSmallTrackerListItemEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SMALL_TRACKER_LIST_ITEM, value).apply()
    }
}