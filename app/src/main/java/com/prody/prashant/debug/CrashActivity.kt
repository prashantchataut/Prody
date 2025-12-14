package com.prody.prashant.debug

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BugReport
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
 *
 * A modern, minimalist crash reporting screen that displays error details
 * with options to copy the report or restart the app.
 *
 * CRITICAL DESIGN DECISIONS:
 * - Runs in separate process (:crash) for complete isolation from main app
 * - NO Hilt/DI dependencies - completely standalone
 * - Uses its own theme to avoid dependency on main app theme
 * - Graceful fallback handling for all operations
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

        // Extract crash information from intent
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
            // If Compose fails, the activity will at least show something
            android.util.Log.e("CrashActivity", "Failed to set Compose content", e)
            // Try to at least show a toast with the error
            try {
                Toast.makeText(this, "Crash: $exceptionType - $exceptionMessage", Toast.LENGTH_LONG).show()
            } catch (ignore: Exception) {
                // Last resort - just log
            }
        }
    }

    private fun extractCrashInfo() {
        try {
            exceptionType = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_TYPE) ?: "Unknown"
            exceptionMessage = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_MESSAGE) ?: "No message"
            stackTrace = intent.getStringExtra(CrashHandler.EXTRA_STACK_TRACE) ?: "No stack trace available"
            deviceInfo = intent.getStringExtra(CrashHandler.EXTRA_DEVICE_INFO) ?: ""
            timestamp = intent.getStringExtra(CrashHandler.EXTRA_TIMESTAMP) ?: ""
            fullReport = intent.getStringExtra(CrashHandler.EXTRA_FULL_REPORT) ?: buildFallbackReport()
            threadName = intent.getStringExtra(CrashHandler.EXTRA_THREAD_NAME) ?: "Unknown"
            rootCauseType = intent.getStringExtra(CrashHandler.EXTRA_ROOT_CAUSE_TYPE) ?: ""
            rootCauseMsg = intent.getStringExtra(CrashHandler.EXTRA_ROOT_CAUSE_MSG) ?: ""
        } catch (e: Exception) {
            android.util.Log.e("CrashActivity", "Error extracting crash info", e)
        }
    }

    private fun buildFallbackReport(): String {
        return buildString {
            appendLine("PRODY CRASH REPORT")
            appendLine("==================")
            appendLine("Exception: $exceptionType")
            appendLine("Message: $exceptionMessage")
            appendLine()
            appendLine("Stack Trace:")
            appendLine(stackTrace)
            appendLine()
            appendLine(deviceInfo)
        }
    }

    private fun copyToClipboard() {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard == null) {
                Toast.makeText(this, "Clipboard not available", Toast.LENGTH_SHORT).show()
                return
            }
            val clip = ClipData.newPlainText("Prody Crash Report", fullReport)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Crash report copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            android.util.Log.e("CrashActivity", "Failed to copy to clipboard", e)
            Toast.makeText(this, "Failed to copy: ${e.message}", Toast.LENGTH_SHORT).show()
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

            // Kill the crash process
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            android.util.Log.e("CrashActivity", "Failed to restart app", e)
            Toast.makeText(this, "Failed to restart: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

// =============================================================================
// CRASH THEME - Completely self-contained, no external dependencies
// =============================================================================

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

    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}

// =============================================================================
// CRASH SCREEN COMPOSABLE
// =============================================================================

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

    // Pulse animation for the error icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color(0xFF0D1210),
                        Color(0xFF0F1512)
                    )
                )
            )
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

            // Animated error icon with glow
            Box(contentAlignment = Alignment.Center) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE65C5C),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Icon container
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3D1C1C),
                                    Color(0xFF2D1515)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFFE65C5C).copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BugReport,
                        contentDescription = null,
                        tint = Color(0xFFFF8080),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "App Crashed",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Timestamp & Thread
            Text(
                text = "$timestamp • Thread: $threadName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Exception Type Card
            ErrorInfoCard(
                title = "Exception",
                value = exceptionType,
                color = Color(0xFFE65C5C)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Exception Message Card
            ErrorMessageCard(
                message = exceptionMessage
            )

            // Root Cause Section (if different from main exception)
            if (rootCauseType.isNotEmpty() && rootCauseType != exceptionType) {
                Spacer(modifier = Modifier.height(14.dp))

                ExpandableSection(
                    title = "Root Cause: $rootCauseType",
                    icon = Icons.Filled.Warning,
                    isExpanded = showRootCause,
                    onToggle = { showRootCause = !showRootCause }
                ) {
                    CodeBlock(text = rootCauseMsg)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stack Trace Expandable Section
            ExpandableSection(
                title = "Stack Trace",
                icon = Icons.Filled.Code,
                isExpanded = showStackTrace,
                onToggle = { showStackTrace = !showStackTrace }
            ) {
                CodeBlock(text = stackTrace)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Device Info Expandable Section
            ExpandableSection(
                title = "Device Info",
                icon = Icons.Filled.PhoneAndroid,
                isExpanded = showDeviceInfo,
                onToggle = { showDeviceInfo = !showDeviceInfo }
            ) {
                CodeBlock(text = deviceInfo)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Copy Button
                ActionButton(
                    text = "Copy",
                    icon = Icons.Filled.ContentCopy,
                    onClick = onCopyClick,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                // Restart Button
                ActionButton(
                    text = "Restart",
                    icon = Icons.Filled.Refresh,
                    onClick = onRestartClick,
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// =============================================================================
// UI COMPONENTS
// =============================================================================

@Composable
private fun ErrorInfoCard(
    title: String,
    value: String,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun ErrorMessageCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Message",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isExpanded) 0.dp else 12.dp,
                bottomEnd = if (isExpanded) 0.dp else 12.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scaleY = if (isExpanded) -1f else 1f, scaleX = 1f)
                )
            }
        }

        if (isExpanded) {
            content()
        }
    }
}

@Composable
private fun CodeBlock(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0A0D0B),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFFB8D4C8),
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isPrimary) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isPrimary) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPrimary) 4.dp else 0.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
