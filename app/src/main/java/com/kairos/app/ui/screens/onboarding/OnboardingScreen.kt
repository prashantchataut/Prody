package com.kairos.app.ui.screens.onboarding

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.app.ui.animation.KairosDurations
import com.kairos.app.ui.animation.KairosEasing
import com.kairos.app.ui.animation.rememberKairosReducedMotion
import com.kairos.app.ui.components.kairos.KairosMark
import com.kairos.app.ui.theme.KairosClay
import com.kairos.app.ui.theme.KairosPeriwinkle
import com.kairos.app.ui.theme.KairosSeaGlass
import com.kairos.app.ui.theme.KairosTheme
import com.kairos.app.ui.theme.ThemeMode
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val OnboardingPageCount = 3

private data class WisdomCategory(val key: String, val label: String)

private val wisdomCategories = listOf(
    WisdomCategory("wisdom", "Wisdom"),
    WisdomCategory("life", "Life"),
    WisdomCategory("motivation", "Momentum"),
    WisdomCategory("creativity", "Creativity"),
    WisdomCategory("communication", "Communication"),
    WisdomCategory("growth", "Growth")
)

/** Word interests align with the categories used across the expanded vocabulary catalog. */
private val wordInterestCategories = listOf(
    WisdomCategory("self-improvement", "Self-improvement"),
    WisdomCategory("communication", "Communication"),
    WisdomCategory("emotion", "Emotion"),
    WisdomCategory("mindfulness", "Mindfulness"),
    WisdomCategory("learning", "Learning"),
    WisdomCategory("reflection", "Reflection"),
    WisdomCategory("business", "Business"),
    WisdomCategory("academic", "Academic")
)

private val OnboardingInk = Color(0xFF111216)
private val OnboardingPaper = Color(0xFFF6F4EF)
private val OnboardingMuted = Color(0xFFAAAAB2)
private val OnboardingPanel = Color(0xC923242A)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val completionState by viewModel.completionState.collectAsStateWithLifecycle()
    OnboardingContent(
        completionState = completionState,
        onComplete = onComplete,
        onSubmit = viewModel::completeOnboarding,
        onClearError = viewModel::clearError
    )
}

@Composable
private fun OnboardingContent(
    completionState: OnboardingCompletionState,
    onComplete: () -> Unit,
    onSubmit: (Int, Set<String>, Set<String>, Int) -> Unit,
    onClearError: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingPageCount })
    val scope = rememberCoroutineScope()
    var difficulty by rememberSaveable { mutableIntStateOf(3) }
    var sessionSize by rememberSaveable { mutableIntStateOf(5) }
    var selectedCategoryKeys by rememberSaveable { mutableStateOf("wisdom,life,motivation") }
    var selectedWordCategoryKeys by rememberSaveable {
        mutableStateOf("self-improvement,communication,mindfulness,reflection")
    }
    val selectedCategories = remember(selectedCategoryKeys) {
        selectedCategoryKeys.split(',').filter(String::isNotBlank).toSet()
    }
    val selectedWordCategories = remember(selectedWordCategoryKeys) {
        selectedWordCategoryKeys.split(',').filter(String::isNotBlank).toSet()
    }
    val saving = completionState is OnboardingCompletionState.Saving

    KeepOnboardingSystemBarsDark()

    LaunchedEffect(completionState) {
        if (completionState is OnboardingCompletionState.Completed) onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(OnboardingInk, Color(0xFF17161C), Color(0xFF101318)),
                    start = Offset.Zero,
                    end = Offset(1200f, 2200f)
                )
            )
    ) {
        OnboardingAtmosphere(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            OnboardingTopBar(
                currentPage = pagerState.currentPage,
                enabled = !saving,
                onSkip = {
                    onSubmit(difficulty, selectedCategories, selectedWordCategories, sessionSize)
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = !saving,
                beyondViewportPageCount = 1
            ) { page ->
                val offset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                ).absoluteValue.coerceIn(0f, 1f)
                val pageModifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - (offset * 0.30f)
                        scaleX = 1f - (offset * 0.035f)
                        scaleY = 1f - (offset * 0.035f)
                        translationX = offset * 18.dp.toPx()
                    }

                when (page) {
                    0 -> DailyMomentPage(pageModifier)
                    1 -> PersonalizationPage(
                        modifier = pageModifier,
                        difficulty = difficulty,
                        onDifficultyChange = { difficulty = it },
                        sessionSize = sessionSize,
                        onSessionSizeChange = { sessionSize = it },
                        selectedCategories = selectedCategories,
                        onCategoryToggle = { key ->
                            val updated = if (key in selectedCategories) {
                                if (selectedCategories.size > 1) selectedCategories - key else selectedCategories
                            } else {
                                selectedCategories + key
                            }
                            selectedCategoryKeys = updated.sorted().joinToString(",")
                        },
                        selectedWordCategories = selectedWordCategories,
                        onWordCategoryToggle = { key ->
                            val updated = if (key in selectedWordCategories) {
                                if (selectedWordCategories.size > 1) selectedWordCategories - key else selectedWordCategories
                            } else {
                                selectedWordCategories + key
                            }
                            selectedWordCategoryKeys = updated.sorted().joinToString(",")
                        }
                    )
                    else -> ReadyPage(
                        modifier = pageModifier,
                        completionState = completionState,
                        onRetry = {
                            onSubmit(difficulty, selectedCategories, selectedWordCategories, sessionSize)
                        }
                    )
                }
            }

            OnboardingActions(
                currentPage = pagerState.currentPage,
                isSaving = saving,
                onNext = {
                    onClearError()
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = (pagerState.currentPage + 1).coerceAtMost(OnboardingPageCount - 1),
                            animationSpec = tween(KairosDurations.Page, easing = KairosEasing.EaseOutExpo)
                        )
                    }
                },
                onFinish = {
                    onSubmit(difficulty, selectedCategories, selectedWordCategories, sessionSize)
                }
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    currentPage: Int,
    enabled: Boolean,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KairosMark(
                modifier = Modifier.size(31.dp),
                tint = OnboardingPaper,
                accent = KairosPeriwinkle
            )
            Text(
                text = "Kairos",
                color = OnboardingPaper,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = if (currentPage == OnboardingPageCount - 1) "" else "Skip  ›",
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(enabled = enabled && currentPage < OnboardingPageCount - 1, onClick = onSkip)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            color = OnboardingMuted,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun DailyMomentPage(modifier: Modifier = Modifier) {
    val reducedMotion = rememberKairosReducedMotion()
    val transition = rememberInfiniteTransition(label = "onboarding-float")
    val floatY by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_800, easing = KairosEasing.EaseOutQuart),
            repeatMode = RepeatMode.Reverse
        ),
        label = "daily-card-float"
    )

    OnboardingPage(
        modifier = modifier,
        title = "Learn what stays with you.",
        body = "One useful word, one worthwhile thought, and a small action that turns reading into memory."
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingShadowCard(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(190.dp)
                    .graphicsLayer {
                        rotationZ = -7f
                        translationX = -42f
                        translationY = 34f
                        alpha = 0.34f
                    }
            )
            FloatingShadowCard(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .height(204.dp)
                    .graphicsLayer {
                        rotationZ = 6f
                        translationX = 40f
                        translationY = 16f
                        alpha = 0.48f
                    }
            )
            OnboardingGlass(
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .graphicsLayer { translationY = if (reducedMotion) 0f else floatY },
                strong = true
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TODAY'S WORD", style = MaterialTheme.typography.labelSmall, color = KairosPeriwinkle, fontWeight = FontWeight.Bold)
                        Text("2 min", style = MaterialTheme.typography.labelSmall, color = OnboardingMuted)
                    }
                    Text("lucid", style = MaterialTheme.typography.displaySmall, color = OnboardingPaper, fontWeight = FontWeight.Medium)
                    Text("/Ëˆluː.sÉªd/  ·  adjective", style = MaterialTheme.typography.labelMedium, color = OnboardingMuted)
                    Text("Clear and easy to understand.", style = MaterialTheme.typography.titleMedium, color = OnboardingPaper)
                    Text(
                        "She gave a lucid explanation of a difficult idea.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnboardingPaper.copy(alpha = 0.72f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonalizationPage(
    modifier: Modifier = Modifier,
    difficulty: Int,
    onDifficultyChange: (Int) -> Unit,
    sessionSize: Int,
    onSessionSizeChange: (Int) -> Unit,
    selectedCategories: Set<String>,
    onCategoryToggle: (String) -> Unit,
    selectedWordCategories: Set<String>,
    onWordCategoryToggle: (String) -> Unit
) {
    OnboardingPage(
        modifier = modifier,
        title = "A feed that learns gently.",
        body = "Tell Kairos what kind of words and ideas you want more of. You can change all of this later in Settings."
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OnboardingGlass(modifier = Modifier.fillMaxWidth(), strong = true) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Vocabulary pace", style = MaterialTheme.typography.titleMedium, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
                        DarkSegmentedControl(
                            labels = listOf("Gentle", "Balanced", "Stretch"),
                            selectedIndex = when (difficulty) { 2 -> 0; 4 -> 2; else -> 1 },
                            onSelected = { index -> onDifficultyChange(listOf(2, 3, 4)[index]) }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Cards per session", style = MaterialTheme.typography.titleMedium, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
                        DarkSegmentedControl(
                            labels = listOf("3", "5", "10"),
                            selectedIndex = when (sessionSize) { 3 -> 0; 10 -> 2; else -> 1 },
                            onSelected = { index -> onSessionSizeChange(listOf(3, 5, 10)[index]) }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Words you want to learn", style = MaterialTheme.typography.titleMedium, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Words in these areas surface first in your practice sessions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnboardingPaper.copy(alpha = 0.66f)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            wordInterestCategories.forEach { category ->
                                InterestChip(
                                    text = category.label,
                                    selected = category.key in selectedWordCategories,
                                    onClick = { onWordCategoryToggle(category.key) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Quotes and ideas", style = MaterialTheme.typography.titleMedium, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Quotes from your chosen themes appear more often in Today.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnboardingPaper.copy(alpha = 0.66f)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            wisdomCategories.forEach { category ->
                                InterestChip(
                                    text = category.label,
                                    selected = category.key in selectedCategories,
                                    onClick = { onCategoryToggle(category.key) }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RecommendationPreview("Review due", "35%", KairosPeriwinkle, Modifier.weight(1f))
                RecommendationPreview("Your interests", "20%", KairosClay, Modifier.weight(1f))
                RecommendationPreview("Freshness", "15%", KairosSeaGlass, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ReadyPage(
    modifier: Modifier = Modifier,
    completionState: OnboardingCompletionState,
    onRetry: () -> Unit
) {
    OnboardingPage(
        modifier = modifier,
        title = "A small ritual, not another dashboard.",
        body = "Today, Learn, Reflect, and Library are the whole core. Everything else stays secondary until it earns a place."
    ) {
        OnboardingGlass(modifier = Modifier.fillMaxWidth(), strong = true) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DestinationRow(Icons.Outlined.AutoStories, "Today", "One word and one thought")
                DestinationRow(Icons.Outlined.School, "Learn", "Review at the right time")
                DestinationRow(Icons.Outlined.EditNote, "Reflect", "Turn ideas into your own words")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TrustRow(Icons.Outlined.Lock, "Useful without an account", "Your core library and reflections work locally.")
            TrustRow(Icons.Outlined.NotificationsNone, "Reminders are opt-in", "Kairos asks only after you choose to enable them.")
        }

        AnimatedVisibility(
            visible = completionState is OnboardingCompletionState.Error,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OnboardingGlass(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = (completionState as? OnboardingCompletionState.Error)?.message.orEmpty(),
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFB4AB),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(onClick = onRetry, shape = CircleShape, color = Color.White.copy(alpha = 0.10f)) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Retry setup", tint = OnboardingPaper, modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            content = content
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Medium,
                color = OnboardingPaper,
                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.02f
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = OnboardingMuted,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.12f
            )
        }
    }
}

@Composable
private fun OnboardingActions(
    currentPage: Int,
    isSaving: Boolean,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            repeat(OnboardingPageCount) { index ->
                Box(
                    Modifier
                        .size(width = if (index == currentPage) 22.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(if (index == currentPage) OnboardingPaper else OnboardingPaper.copy(alpha = 0.22f))
                        .semantics { contentDescription = "Step ${index + 1}${if (index == currentPage) ", current" else ""}" }
                )
            }
        }
        Surface(
            onClick = if (currentPage == OnboardingPageCount - 1) onFinish else onNext,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            color = OnboardingPaper,
            contentColor = OnboardingInk
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = isSaving,
                    transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(120)) },
                    label = "onboarding-action"
                ) { saving ->
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = OnboardingInk)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (currentPage == OnboardingPageCount - 1) "Begin with Kairos" else "Continue",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingGlass(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (strong) Color(0xD92A2A31) else OnboardingPanel)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.06f))
                ),
                shape
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )
        content()
    }
}

@Composable
private fun FloatingShadowCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2B2931))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
    )
}

@Composable
private fun DarkSegmentedControl(
    labels: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(Color.Black.copy(alpha = 0.22f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Surface(
                onClick = { onSelected(index) },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (index == selectedIndex) OnboardingPaper else Color.Transparent,
                contentColor = if (index == selectedIndex) OnboardingInk else OnboardingMuted
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun InterestChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) KairosPeriwinkle.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.06f),
        contentColor = if (selected) Color(0xFFE8EAFF) else OnboardingMuted,
        border = BorderStroke(1.dp, if (selected) KairosPeriwinkle.copy(alpha = 0.68f) else Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (selected) Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(15.dp))
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun RecommendationPreview(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    OnboardingGlass(modifier = modifier.height(92.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = accent, fontWeight = FontWeight.SemiBold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = OnboardingMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DestinationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.08f)) {
            Icon(icon, contentDescription = null, tint = OnboardingPaper, modifier = Modifier.padding(10.dp).size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnboardingMuted)
        }
    }
}

@Composable
private fun TrustRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = KairosSeaGlass, modifier = Modifier.size(21.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnboardingPaper, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodySmall, color = OnboardingMuted)
        }
    }
}

@Composable
private fun OnboardingAtmosphere(modifier: Modifier = Modifier) {
    Box(modifier) {
        Box(
            Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 130f; translationY = -120f }
                .background(
                    Brush.radialGradient(listOf(KairosPeriwinkle.copy(alpha = 0.16f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            Modifier
                .size(300.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -130f; translationY = 110f }
                .background(
                    Brush.radialGradient(listOf(KairosClay.copy(alpha = 0.12f), Color.Transparent)),
                    CircleShape
                )
        )
    }
}

@Composable
private fun KeepOnboardingSystemBarsDark() {
    val view = LocalView.current
    if (view.isInEditMode) return
    val activity = view.context as? Activity ?: return
    DisposableEffect(view) {
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val oldStatus = controller.isAppearanceLightStatusBars
        val oldNavigation = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = oldStatus
            controller.isAppearanceLightNavigationBars = oldNavigation
        }
    }
}

@Preview(name = "Kairos onboarding", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingPreview() {
    KairosTheme(themeMode = ThemeMode.DARK) {
        OnboardingContent(
            completionState = OnboardingCompletionState.Idle,
            onComplete = {},
            onSubmit = { _, _, _, _ -> },
            onClearError = {}
        )
    }
}
