package com.prody.prashant.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Robustly find the nearest Activity from a given Context.
 * Traverses ContextWrappers until an Activity is found or null is returned.
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
