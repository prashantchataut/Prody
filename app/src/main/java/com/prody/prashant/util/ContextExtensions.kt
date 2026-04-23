package com.prody.prashant.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Extension function to find the hosting Activity from a Context.
 * Correctly handles ContextWrapper nesting (e.g. from Hilt or TintContextWrapper).
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return if (context is Activity) context else null
}
