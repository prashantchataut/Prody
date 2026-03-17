package com.prody.prashant.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Extension function to find the underlying Activity from a Context.
 * Safely traverses ContextWrappers (like those used in Hilt or Jetpack Compose).
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
