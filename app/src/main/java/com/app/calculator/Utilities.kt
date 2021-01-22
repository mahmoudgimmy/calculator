package com.app.calculator

import android.content.Context
import android.util.DisplayMetrics


object Utility {
    // calculate number of columns in GridView depend on screen size and predefined column width
    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Float = 120f
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}