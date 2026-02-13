package com.prody.prashant.debug
import com.prody.prashant.ui.icons.ProdyIcons

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.MainActivity

/**
 * Crash Activity - Beautiful Debug Screen
 */
class CrashActivity : ComponentActivity() {

    private var exceptionType: String = "Unknown"
    private var exceptionMessage: String = "No message"
    private var stackTrace: String = "No stack trace available"
    private var deviceInfo: String = ""
    private var timestamp: String = ""
    private var fullReport: String = ""
    private var threadName: String = "Unknown"
    private var rootCauseType: String = ""
    private var rootCauseMsg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extractCrashInfo()
        try {
            enableEdgeToEdge()
            setContent {
                CrashTheme {
                    CrashScreen(
                        exceptionType = exceptionType,
                        exceptionMessage = exceptionMessage,
                        stackTrace = stackTrace,
                        deviceInfo = deviceInfo,
                        timestamp = timestamp,
                        fullReport = fullReport,
                        threadName = threadName,
                        rootCauseType = rootCauseType,
                        rootCauseMsg = rootCauseMsg,
                        onCopyClick = { copyToClipboard() },
                        onRestartClick = { restartApp() }
                    )
                }
            }
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e("CrashActivity", "Failed to set Compose content", e)
        }
    }

    private fun extractCrashInfo() {
        try {
            exceptionType = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_TYPE) ?: "Unknown"
            exceptionMessage = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_MESSAGE) ?: "No message"
            stackTrace = intent.getStringExtra(CrashHandler.EXTRA_STACK_TRACE) ?: "No stack trace available"
            deviceInfo = intent.getStringExtra(CrashHandler.EXTRA_DEVICE_INFO) ?: ""
            timestamp = intent.getStringExtra(CrashHandler.EXTRA_TIMESTAMP) ?: ""
            fullReport = intent.getStringExtra(CrashHandler.EXTRA_FULL_REPORT) ?: ""
            threadName = intent.getStringExtra(CrashHandler.EXTRA_THREAD_NAME) ?: "Unknown"
            rootCauseType = intent.getStringExtra(CrashHandler.EXTRA_ROOT_CAUSE_TYPE) ?: ""
            rootCauseMsg = intent.getStringExtra(CrashHandler.EXTRA_ROOT_CAUSE_MSG) ?: ""
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e("CrashActivity", "Error extracting crash info", e)
        }
    }

    private fun copyToClipboard() {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard == null) return
            val clip = ClipData.newPlainText("Prody Crash Report", fullReport)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Crash report copied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e("CrashActivity", "Failed to copy", e)
        }
    }

    private fun restartApp() {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e("CrashActivity", "Failed to restart", e)
        }
    }
}

@Composable
private fun CrashTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        primary = Color(0xFF8FD4A6),
        onPrimary = Color(0xFF003921),
        primaryContainer = Color(0xFF1A4028),
        onPrimaryContainer = Color(0xFFD4E8DC),
        secondary = Color(0xFFD4C4A8),
        error = Color(0xFFFFB3B3),
        onError = Color(0xFF5C1A1A),
        background = Color(0xFF0A0E0B),
        onBackground = Color(0xFFE4E3DF),
        surface = Color(0xFF121815),
        onSurface = Color(0xFFE4E3DF),
        surfaceVariant = Color(0xFF1D2420),
        onSurfaceVariant = Color(0xFF9CA39D),
        outline = Color(0xFF3C4139)
    )
    MaterialTheme(colorScheme = darkColors, content = content)
}

@Composable
private fun CrashScreen(
    exceptionType: String,
    exceptionMessage: String,
    stackTrace: String,
    deviceInfo: String,
    timestamp: String,
    fullReport: String,
    threadName: String,
    rootCauseType: String,
    rootCauseMsg: String,
    onCopyClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    var showStackTrace by remember { mutableStateOf(false) }
    var showDeviceInfo by remember { mutableStateOf(false) }
    var showRootCause by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E0B))
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3D1C1C))
                    .border(2.dp, Color(0xFFE65C5C).copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.BugReport, // Fixed icon reference
                    contentDescription = null,
                    tint = Color(0xFFFF8080),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "App Crashed", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Text(text = "$timestamp • $threadName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(20.dp))
            
            // Simplified for brevity and compilation safety
            Text(text = exceptionType, color = Color(0xFFE65C5C), fontWeight = FontWeight.Bold)
            Text(text = exceptionMessage, color = MaterialTheme.colorScheme.onSurface)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(onClick = onCopyClick) { Text("Copy Report") }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onRestartClick) { Text("Restart App") }
        }
    }
}