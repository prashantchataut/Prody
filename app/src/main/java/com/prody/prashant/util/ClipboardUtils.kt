package com.prody.prashant.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * ClipboardUtils - Utility for clipboard operations with user feedback.
 */
object ClipboardUtils {

    /**
     * Copy text to system clipboard and show a Toast message.
     *
     * @param context Android context
     * @param text Text to copy
     * @param label Label for the clipped data (optional)
     * @param toastMessage Message to show in Toast (defaults to "Copied to clipboard")
     */
    fun copyToClipboard(
        context: Context,
        text: String,
        label: String = "Prody Content",
        toastMessage: String = "Copied to clipboard"
    ) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to copy", Toast.LENGTH_SHORT).show()
        }
    }
}
