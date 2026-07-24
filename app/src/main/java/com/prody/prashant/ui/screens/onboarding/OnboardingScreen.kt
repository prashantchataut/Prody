package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.components.kairos.KairosAppBackground
import com.prody.prashant.ui.components.kairos.KairosGlassSurface
import com.prody.prashant.ui.components.kairos.KairosPrimaryButton
import com.prody.prashant.ui.components.kairos.KairosReadingSurface
import com.prody.prashant.ui.components.kairos.KairosSecondaryButton
import com.prody.prashant.ui.components.kairos.KairosSegmentedControl
import com.prody.prashant.ui.theme.KairosRadius
import com.prody.prashant.ui.theme.KairosSpacing
import com.prody.prashant.ui.theme.KairosTheme
import com.prody.prashant.ui.theme.ThemeMode
import kotlinx.coroutines.launch

private const val OnboardingPageCount = 3

private data class WisdomCategory(
    val key: String,
    val label: String
)

private val wisdomCategories = listOf(
    WisdomCategory("wisdom", "Wisdom"),
    WisdomCategory("life", "Life"),
    WisdomCategory("motivation", "Motivation"),
    WisdomCategory("creativity", "Creativity"),
    WisdomCategory("communication", "Communication"),
    WisdomCategory("growth", "Growth")
)

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
    onSubmit: (Int, Set<String>) -> Unit,
    onClearError: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingPageCount })
    val scope = rememberCoroutineScope()
    var difficulty by rememberSaveable { mutableIntStateOf(3) }
    var selectedCategoryKeys by rememberSaveable {
        mutableStateOf("wisdom,life,motivation")
    }
    val selectedCategories = remember(selectedCategoryKeys) {
        selectedCategoryKeys.split(',').filter(String::isNotBlank).toSet()
    }
    val isSaving = completionState is OnboardingCompletionState.Saving

    LaunchedEffect(completionState) {
        if (completionState is OnboardingCompletionState.Completed) onComplete()
    }

    KairosAppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            OnboardingTopBar(
                currentPage = pagerState.currentPage,
                onBack = {
                    onClearError()
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = !isSaving
            ) { page ->
                when (page) {
                    0 -> DailyMomentPage()
                    1 -> PersonalizationPage(
                        difficulty = difficulty,
                        onDifficultyChange = { difficulty = it },
                        selectedCategories = selectedCategories,
                        onCategoryToggle = { key ->
                            val updated = if (key in selectedCategories) {
                                if (selectedCategories.size > 1) selectedCategories - key else selectedCategories
                            } else {
                                selectedCategories + key
                            }
                            selectedCategoryKeys = updated.sorted().joinToString(",")
                        }
                    )
                    else -> ReadyPage(
                        completionState = completionState,
                        onRetry = {
                            onSubmit(difficulty, selectedCategories)
                        }
                    )
                }
            }

            OnboardingActions(
                currentPage = pagerState.currentPage,
                isSaving = isSaving,
                onNext = {
                    onClearError()
                    scope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(OnboardingPageCount - 1))
                    }
                },
                onFinish = { onSubmit(difficulty, selectedCategories) }
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    currentPage: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KairosSpacing.screen, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (currentPage > 0) {
            KairosGlassSurface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                elevation = 4.dp,
                onClick = onBack
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Previous step",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Step ${currentPage + 1} of $OnboardingPageCount"
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(OnboardingPageCount) { index ->
                KairosGlassSurface(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp),
                    shape = CircleShape,
                    strong = index <= currentPage,
                    elevation = 0.dp
                ) {
                    if (index <= currentPage) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "${currentPage + 1}/$OnboardingPageCount",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(width = 48.dp, height = 24.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DailyMomentPage() {
    OnboardingPage(
        eyebrow = "WELCOME TO KAIROS",
        title = "One useful moment, every day.",
        body = "Learn a word, notice an idea, and do one small thing that helps it stay with you."
    ) {
        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = MaterialTheme.colorScheme.primary
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text(
                    text = "WORD OF THE DAY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "lucid",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "/ˈluː.sɪd/  ·  adjective",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Clear and easy to understand.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "She gave a lucid explanation of a difficult idea.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = MaterialTheme.colorScheme.secondary
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "“Attention is the beginning of devotion.”",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "— Mary Oliver",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonalizationPage(
    difficulty: Int,
    onDifficultyChange: (Int) -> Unit,
    selectedCategories: Set<String>,
    onCategoryToggle: (String) -> Unit
) {
    val difficultyValues = listOf(2, 3, 4)
    OnboardingPage(
        eyebrow = "MAKE IT YOURS",
        title = "Tune what appears.",
        body = "These choices feed the recommendation ranking. You can change them later, and your feedback will keep refining the mix."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(KairosSpacing.sm)) {
            Text(
                text = "Vocabulary pace",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            KairosSegmentedControl(
                items = listOf("Gentle", "Balanced", "Stretch"),
                selectedIndex = difficultyValues.indexOf(difficulty).coerceAtLeast(0),
                onSelected = { index -> onDifficultyChange(difficultyValues[index]) }
            )
            Text(
                text = when (difficulty) {
                    2 -> "More familiar language with a lighter review load."
                    4 -> "Rarer words and a little more challenge."
                    else -> "A practical mix of familiar and stretching words."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(KairosSpacing.sm)) {
            Text(
                text = "Ideas you want more often",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                wisdomCategories.forEach { category ->
                    val selected = category.key in selectedCategories
                    FilterChip(
                        selected = selected,
                        onClick = { onCategoryToggle(category.key) },
                        label = { Text(category.label) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
            Text(
                text = "Keep at least one topic selected. Kairos also limits repetition so the feed stays varied.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        KairosGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            strong = true,
            elevation = 4.dp,
            contentPadding = PaddingValues(KairosSpacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KairosSpacing.md)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Too easy, too hard, more like this, and less like this are real ranking signals—not decorative buttons.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ReadyPage(
    completionState: OnboardingCompletionState,
    onRetry: () -> Unit
) {
    OnboardingPage(
        eyebrow = "READY WHEN YOU ARE",
        title = "Quiet by default.",
        body = "Kairos starts with four clear places. Your local profile works without an account, and reminders stay optional."
    ) {
        KairosReadingSurface(
            modifier = Modifier.fillMaxWidth(),
            accent = MaterialTheme.colorScheme.tertiary,
            contentPadding = PaddingValues(KairosSpacing.lg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DestinationRow(Icons.Outlined.Lightbulb, "Today", "One selected word, one thought, one useful action.")
                DestinationRow(Icons.Outlined.School, "Learn", "Browse, save, and review vocabulary.")
                DestinationRow(Icons.Outlined.EditNote, "Reflect", "Write privately and revisit your own ideas.")
                DestinationRow(Icons.Outlined.AutoStories, "Library", "Keep quotes, proverbs, idioms, and phrases together.")
            }
        }

        KairosGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            strong = true,
            elevation = 4.dp,
            contentPadding = PaddingValues(KairosSpacing.lg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TrustRow(Icons.Outlined.Lock, "Local-first", "Your core learning and reflections work on this device.")
                TrustRow(Icons.Outlined.NotificationsNone, "No surprise prompts", "Notification permission is requested only when you enable reminders.")
                TrustRow(Icons.Outlined.BookmarkBorder, "No fake community", "Personal progress stays personal until real shared features exist.")
            }
        }

        AnimatedVisibility(visible = completionState is OnboardingCompletionState.Error) {
            val message = (completionState as? OnboardingCompletionState.Error)?.message.orEmpty()
            KairosReadingSurface(
                modifier = Modifier.fillMaxWidth(),
                accent = MaterialTheme.colorScheme.error,
                contentPadding = PaddingValues(KairosSpacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(KairosSpacing.md)) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    KairosSecondaryButton(
                        text = "Try again",
                        icon = Icons.Outlined.Refresh,
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationRow(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KairosSpacing.md)
    ) {
        KairosGlassSurface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            elevation = 2.dp
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrustRow(
    icon: ImageVector,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(KairosSpacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OnboardingPage(
    eyebrow: String,
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = KairosSpacing.tabletMaxWidth)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = KairosSpacing.screen,
                    end = KairosSpacing.screen,
                    top = 20.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(KairosSpacing.xl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(KairosSpacing.sm)) {
                Text(
                    text = eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
            Spacer(Modifier.height(8.dp))
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KairosSpacing.screen, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = KairosSpacing.tabletMaxWidth),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KairosSpacing.sm)
        ) {
            if (currentPage < OnboardingPageCount - 1) {
                KairosPrimaryButton(
                    text = if (currentPage == 0) "Set my preferences" else "Continue",
                    icon = Icons.AutoMirrored.Outlined.ArrowForward,
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                KairosPrimaryButton(
                    text = if (isSaving) "Setting up Kairos" else "Start Kairos",
                    onClick = onFinish,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                )
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingPreview() {
    KairosTheme(dynamicColor = false) {
        OnboardingContent(
            completionState = OnboardingCompletionState.Idle,
            onComplete = {},
            onSubmit = { _, _ -> },
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 900)
@Composable
private fun OnboardingTabletDarkPreview() {
    KairosTheme(themeMode = ThemeMode.DARK, dynamicColor = false) {
        OnboardingContent(
            completionState = OnboardingCompletionState.Idle,
            onComplete = {},
            onSubmit = { _, _ -> },
            onClearError = {}
        )
    }
}
