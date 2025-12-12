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
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.MainActivity

/**
 * Crash Activity - Beautiful Debug Screen
 * 
 * A modern, minimalist crash reporting screen that displays error details
 * with options to copy the report or restart the app.
 * 
 * Design: Clean, professional aesthetic matching Prody's design language
 */
class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val exceptionType = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_TYPE) ?: "Unknown"
        val exceptionMessage = intent.getStringExtra(CrashHandler.EXTRA_EXCEPTION_MESSAGE) ?: "No message"
        val stackTrace = intent.getStringExtra(CrashHandler.EXTRA_STACK_TRACE) ?: "No stack trace"
        val deviceInfo = intent.getStringExtra(CrashHandler.EXTRA_DEVICE_INFO) ?: ""
        val timestamp = intent.getStringExtra(CrashHandler.EXTRA_TIMESTAMP) ?: ""
        val fullReport = intent.getStringExtra(CrashHandler.EXTRA_CRASH_INFO) ?: ""

        setContent {
            CrashTheme {
                CrashScreen(
                    exceptionType = exceptionType,
                    exceptionMessage = exceptionMessage,
                    stackTrace = stackTrace,
                    deviceInfo = deviceInfo,
                    timestamp = timestamp,
                    fullReport = fullReport,
                    onCopyClick = { copyToClipboard(fullReport) },
                    onRestartClick = { restartApp() }
                )
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Prody Crash Report", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Crash report copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
        Runtime.getRuntime().exit(0)
    }
}

// =============================================================================
// CRASH THEME
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
    onCopyClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    var showStackTrace by remember { mutableStateOf(false) }
    var showDeviceInfo by remember { mutableStateOf(false) }

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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(28.dp))

            // Title
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Exception Type Card
            ErrorInfoCard(
                title = "Exception",
                value = exceptionType,
                color = Color(0xFFE65C5C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Exception Message Card
            ErrorMessageCard(
                message = exceptionMessage
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stack Trace Expandable Section
            ExpandableSection(
                title = "Stack Trace",
                icon = Icons.Filled.Code,
                isExpanded = showStackTrace,
                onToggle = { showStackTrace = !showStackTrace }
            ) {
                CodeBlock(text = stackTrace)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Device Info Expandable Section
            ExpandableSection(
                title = "Device Info",
                icon = Icons.Filled.PhoneAndroid,
                isExpanded = showDeviceInfo,
                onToggle = { showDeviceInfo = !showDeviceInfo }
            ) {
                CodeBlock(text = deviceInfo)
            }

            Spacer(modifier = Modifier.height(36.dp))

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

            Spacer(modifier = Modifier.height(40.dp))
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
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "rotation"
    )

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
                modifier = Modifier.padding(16.dp),
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
                .padding(16.dp)
                .fillMaxWidth(),
            fontSize = 11.sp,
            lineHeight = 16.sp
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
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
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
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
