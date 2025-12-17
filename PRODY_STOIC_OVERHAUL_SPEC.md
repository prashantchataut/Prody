# Prody: Modern Stoic Overhaul - Architectural Specification

**Version:** 2.0
**Status:** Design Phase
**Author:** Senior Product Designer & Lead Android Architect

---

## Executive Summary

This document outlines the comprehensive transformation of Prody from its current state into a premium "Digital Sanctuary" application. The overhaul addresses three core areas:

1. **UI/UX Overhaul** - Complete visual identity transformation
2. **Feature Expansion** - Substantive depth additions
3. **AI Integration** - Context-aware, proactive intelligence

**Constraints:**
- NO Habit Tracking features
- Focus on cognitive evolution, not checkbox completion
- Strict adherence to "Stoic/Growth" aesthetic

---

## Part 1: UI/UX Overhaul - Visual Identity & Interaction

### 1.1 Brand Identity & Color Theory

#### Current State Analysis

The existing color palette in `Color.kt` uses:
- Primary: Deep Forest Green (#2D5A3D) - acceptable but needs refinement
- Secondary: Warm Sand (#D4C4A8)
- Mood colors include pastels (yellows, light blues, pinks)

#### New Palette: "The Scholar's Desk" (Dark Academia)

**File to modify:** `app/src/main/java/com/prody/prashant/ui/theme/Color.kt`

```kotlin
// =============================================================================
// SCHOLAR'S DESK PALETTE - Dark Academia Theme
// =============================================================================

// Primary - Deep Forest Green (refined)
val StoicPrimary = Color(0xFF1A3C34)           // Deeper, more contemplative
val StoicPrimaryVariant = Color(0xFF0F2520)   // Rich dark green for emphasis
val StoicOnPrimary = Color(0xFFF5F2E9)         // Parchment text on primary

// Secondary - Charcoal (grounding)
val StoicCharcoal = Color(0xFF121212)          // True charcoal base
val StoicCharcoalLight = Color(0xFF1E1E1E)     // Elevated charcoal

// Surface - Parchment (Light Mode)
val StoicParchment = Color(0xFFF5F2E9)         // Warm off-white, paper-like
val StoicParchmentDark = Color(0xFFEDE8DB)     // Slightly aged parchment
val StoicSurfaceLight = Color(0xFFFFFCF5)      // Lightest parchment

// Surface - Slate (Dark Mode)
val StoicSlate = Color(0xFF1E1E1E)             // Primary dark surface
val StoicSlateDark = Color(0xFF151515)         // Deeper slate
val StoicSlateElevated = Color(0xFF262626)     // Elevated dark surface

// Accent - Burnished Gold (achievements only)
val StoicGold = Color(0xFFC5A059)              // Burnished, not bright
val StoicGoldMuted = Color(0xFFAA8844)         // Subdued gold
val StoicGoldLight = Color(0xFFDCC082)         // Highlight gold

// Accent - Terracotta (destructive/alert actions)
val StoicTerracotta = Color(0xFFCC5500)        // Burnt orange-red
val StoicTerracottaDark = Color(0xFFAA4400)    // Darker terracotta
val StoicTerracottaLight = Color(0xFFDD7733)   // Warning terracotta

// Wisdom Content Colors
val StoicInk = Color(0xFF2C2416)               // Deep brown ink for quotes
val StoicInkFaded = Color(0xFF4A3C2A)          // Faded manuscript ink
val StoicSepiaLight = Color(0xFF8B7355)        // Sepia tones
val StoicSepiaDark = Color(0xFF5C4B3A)         // Dark sepia

// Forbidden Colors (DO NOT USE)
// - NO pastels: #FFC93C, #6CB4D4, #E8A87C, #FFD166, #B39DDB, etc.
// - NO bright saturated colors
// - NO candy/playful tones
```

#### Texture System - Paper Grain Implementation

**New file:** `app/src/main/java/com/prody/prashant/ui/theme/Texture.kt`

```kotlin
/**
 * Texture overlay system for premium, tangible feel.
 * Applies subtle noise/grain to surfaces to avoid "flat digital" appearance.
 */
object ProdyTextures {

    /**
     * Paper grain noise overlay for card backgrounds.
     * Apply with BlendMode.Multiply at 3-5% opacity.
     */
    val paperGrainAsset = R.drawable.texture_paper_grain

    /**
     * Subtle noise for elevated surfaces.
     * Apply with BlendMode.Overlay at 2-4% opacity.
     */
    val subtleNoiseAsset = R.drawable.texture_subtle_noise

    /**
     * Parchment texture for wisdom content cards.
     * Apply with BlendMode.Multiply at 5-8% opacity.
     */
    val parchmentAsset = R.drawable.texture_parchment
}

/**
 * Composable modifier extension for applying texture overlays.
 */
@Composable
fun Modifier.withTexture(
    textureRes: Int,
    alpha: Float = 0.04f,
    blendMode: BlendMode = BlendMode.Multiply
): Modifier {
    // Implementation using Canvas overlay
}
```

**Required Assets:**
- `res/drawable/texture_paper_grain.png` (512x512, tileable)
- `res/drawable/texture_subtle_noise.png` (256x256, tileable)
- `res/drawable/texture_parchment.png` (512x512, tileable)

---

### 1.2 Typography & Hierarchy - The "Dual-Voice" System

**File to modify:** `app/src/main/java/com/prody/prashant/ui/theme/Type.kt`

#### Current State Analysis

The existing typography correctly uses:
- **Playfair Display** - for wisdom/quotes (correct)
- **Poppins** - for UI elements (correct)

However, usage must be enforced more strictly.

#### Typography Rules

```kotlin
/**
 * STRICT TYPOGRAPHY USAGE RULES
 *
 * PLAYFAIR DISPLAY (Serif) - "The Guide's Voice"
 * Use ONLY for:
 * - Daily wisdom quotes
 * - Buddha AI responses
 * - Future self letters (display)
 * - Quote of the Day
 * - Proverbs and idioms
 * - Journal prompt suggestions
 *
 * POPPINS (Sans-Serif) - "The Interface Voice"
 * Use for EVERYTHING else:
 * - Buttons
 * - Navigation labels
 * - Settings text
 * - Form labels
 * - Stats numbers
 * - Timestamps
 * - User-generated content (journal entries)
 */

// Wisdom Content Styles - Playfair ONLY
val WisdomDisplayLarge = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 28.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp,
    color = StoicInk
)

val WisdomQuoteStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 34.sp,
    letterSpacing = 0.15.sp,
    fontStyle = FontStyle.Italic
)

val WisdomAttributionStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 1.2.sp  // Small caps effect
)

// AI Response Style
val BuddhaResponseStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.1.sp
)

// Letter Display Style (for reading future self letters)
val LetterDisplayStyle = TextStyle(
    fontFamily = PlayfairFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.2.sp
)
```

---

### 1.3 Visual Noise Reduction

#### 1.3.1 Replace Radio Buttons with Unlockable Badges

**Current Problem:** Milestones section uses radio buttons.

**Solution:** Achievement/Badge visualization system.

**File to modify:** `app/src/main/java/com/prody/prashant/ui/components/GamificationComponents.kt`

```kotlin
/**
 * Milestone Badge Component
 *
 * States:
 * - LOCKED: Greyed out with padlock icon overlay
 * - UNLOCKED: Full color gold/metallic with subtle glow
 * - ACTIVE: Unlocked + pulsing animation (current milestone)
 */
@Composable
fun MilestoneBadge(
    milestone: Milestone,
    isUnlocked: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !isUnlocked -> StoicSlate.copy(alpha = 0.3f)
        isActive -> StoicGold
        else -> StoicGoldMuted
    }

    val iconTint = when {
        !isUnlocked -> Color.Gray.copy(alpha = 0.5f)
        else -> StoicParchment
    }

    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(if (isActive) Modifier.pulsatingGlow(StoicGold) else Modifier)
    ) {
        Icon(
            imageVector = milestone.icon,
            contentDescription = milestone.name,
            tint = iconTint,
            modifier = Modifier.size(32.dp).align(Alignment.Center)
        )

        if (!isUnlocked) {
            // Padlock overlay
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
            )
        }
    }
}
```

#### 1.3.2 Collapsing Toolbar Layout

**Current Problem:** Green header cuts off abruptly.

**Solution:** Implement CollapsingTopAppBar with smooth fade transition.

**Implementation Architecture:**

```kotlin
/**
 * Screen template with collapsing header support.
 * Header image/color fades seamlessly into body content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdyCollapsingScaffold(
    title: String,
    subtitle: String? = null,
    headerBackgroundColor: Color = StoicPrimary,
    headerContent: @Composable (Float) -> Unit = {}, // Float = collapse progress 0-1
    onNavigateBack: (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            // Fade title as it collapses
                            modifier = Modifier.alpha(1f - (collapsedFraction * 0.3f))
                        )
                        subtitle?.let {
                            AnimatedVisibility(visible = collapsedFraction < 0.5f) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    onNavigateBack?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = headerBackgroundColor,
                    scrolledContainerColor = headerBackgroundColor.copy(alpha = 0.95f)
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
```

#### 1.3.3 Replace Vertical Text Slider with Horizontal Chip Group

**Current Problem:** Vertical "Motivation" text slider is clunky.

**Solution:** Horizontal scrollable FilterChip group.

```kotlin
/**
 * Category selection using horizontal chip group.
 * Replaces vertical text slider.
 */
@Composable
fun CategoryChipRow(
    categories: List<WisdomCategory>,
    selectedCategory: WisdomCategory?,
    onCategorySelected: (WisdomCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = if (category == selectedCategory) {
                    { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StoicPrimary,
                    selectedLabelColor = StoicParchment
                )
            )
        }
    }
}

enum class WisdomCategory(val displayName: String) {
    ALL("All"),
    GROWTH("Growth"),
    RESILIENCE("Resilience"),
    GRATITUDE("Gratitude"),
    MINDFULNESS("Mindfulness"),
    ACTION("Action"),
    SELF_COMPASSION("Self-Compassion"),
    PERSPECTIVE("Perspective")
}
```

---

### 1.4 The Card Problem - Visual Hierarchy System

**Current Problem:** All cards use identical 16dp rounded corners.

**Solution:** Distinct card hierarchy.

**File to modify:** `app/src/main/java/com/prody/prashant/ui/components/ProdyCard.kt`

```kotlin
/**
 * CARD HIERARCHY SYSTEM
 *
 * Level 1 - Hero Cards: Edge-to-edge or 3:4 aspect ratio
 * Level 2 - Content Cards: Standard with texture
 * Level 3 - List Items: No card container, dividers only
 * Level 4 - Interactive: Buttons with elevation states
 */

// ============================================
// LEVEL 1: HERO CARDS - Daily Quote, Featured Wisdom
// ============================================

@Composable
fun HeroWisdomCard(
    quote: String,
    author: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)  // Poster-style aspect ratio
            .clip(RoundedCornerShape(0.dp))  // Edge-to-edge, no rounding
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        StoicPrimary,
                        StoicPrimary.copy(alpha = 0.85f)
                    )
                )
            )
            .withTexture(ProdyTextures.parchmentAsset, alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Opening quote mark
            Text(
                text = "\u201C", // Unicode left double quote
                style = WisdomDisplayLarge.copy(
                    fontSize = 64.sp,
                    color = StoicGold.copy(alpha = 0.6f)
                )
            )

            Text(
                text = quote,
                style = WisdomQuoteStyle.copy(color = StoicParchment),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Text(
                text = "— $author",
                style = WisdomAttributionStyle.copy(color = StoicGold)
            )
        }
    }
}

// ============================================
// LEVEL 2: CONTENT CARDS - Journal entries, Statistics
// ============================================

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .withTexture(ProdyTextures.paperGrainAsset, alpha = 0.03f),
        shape = RoundedCornerShape(12.dp),  // Softer corners
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = elevation,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ============================================
// LEVEL 3: LIST ITEMS - Leaderboard, History
// ============================================

@Composable
fun ListItemRow(
    leadingContent: @Composable () -> Unit,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // NO card container - uses Row with divider below
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent()
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                headlineContent()
                supportingContent?.invoke()
            }
            trailingContent?.invoke()
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),  // Aligned with content
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

// ============================================
// LEVEL 4: INTERACTIVE ELEMENTS - Tactile buttons
// ============================================

@Composable
fun TactileButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isPressed -> 1.dp   // Pressed: reduced elevation
            else -> 4.dp        // Default: elevated
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { if (enabled) onClick() }
                )
            },
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) StoicPrimary else StoicSlate,
        shadowElevation = elevation
    ) {
        Text(
            text = text,
            style = ButtonTextStyle,
            color = StoicParchment,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
        )
    }
}
```

---

## Part 2: Feature Expansion - Substance & Depth

### 2.1 The "Time Capsule" - Revamping Future Self

**Current State:** `FutureMessageScreen.kt` shows a basic list with timeline.

**New Vision:** A "Vault" interface with ceremonial sealing experience.

#### 2.1.1 Data Model Enhancement

**File to modify:** `app/src/main/java/com/prody/prashant/data/local/entity/FutureMessageEntity.kt`

```kotlin
@Entity(tableName = "future_messages")
data class FutureMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: MessageCategory,
    val deliveryDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val isRead: Boolean = false,

    // NEW FIELDS for Context Injection
    val moodAtWriting: String? = null,           // User's mood when writing
    val moodIntensityAtWriting: Int? = null,     // 1-10
    val biggestChallengeAtWriting: String? = null, // Free text
    val hopesForFuture: String? = null,          // What they hope to achieve

    // Visual state
    val sealType: SealType = SealType.STANDARD,  // Wax seal visual
    val unlockProgress: Float = 0f               // 0-1 for animation
)

enum class MessageCategory {
    GOAL,
    MOTIVATION,
    PROMISE,
    REMINDER,
    REFLECTION,  // NEW
    CHALLENGE,   // NEW - sent during difficult times
    CELEBRATION  // NEW - sent during good times
}

enum class SealType {
    STANDARD,    // Default wax seal
    GOLD,        // For goals
    CRIMSON,     // For promises
    EMERALD,     // For reflections
    BRONZE       // For challenges
}
```

#### 2.1.2 Vault Interface Architecture

**New file:** `app/src/main/java/com/prody/prashant/ui/screens/futuremessage/VaultScreen.kt`

```kotlin
/**
 * The Vault - Premium time capsule interface.
 *
 * Visual Design:
 * - Dark ambient background (StoicCharcoal)
 * - Letters displayed as "locked chests" or "sealed envelopes"
 * - Timeline visualization with wax seals as nodes
 * - Dramatic "unsealing" animation when letter is delivered
 */

@Composable
fun VaultScreen(
    onNavigateBack: () -> Unit,
    onWriteLetter: () -> Unit,
    viewModel: VaultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StoicCharcoal)
    ) {
        // Ambient particle effect (floating dust motes)
        AmbientParticleEffect()

        Column {
            // Vault Header
            VaultHeader(
                totalLetters = uiState.totalLetters,
                nextDelivery = uiState.nextDeliveryDate,
                onNavigateBack = onNavigateBack
            )

            // Main content
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Delivered letters (unsealed)
                item {
                    SectionHeader(
                        title = "Opened",
                        subtitle = "${uiState.deliveredLetters.size} letters from your past"
                    )
                }

                items(uiState.deliveredLetters) { letter ->
                    UnsealedLetterCard(
                        letter = letter,
                        onClick = { viewModel.openLetter(letter.id) }
                    )
                }

                // Pending letters (sealed)
                item {
                    SectionHeader(
                        title = "Sealed",
                        subtitle = "Awaiting their moment"
                    )
                }

                // Visual timeline of sealed letters
                item {
                    SealedLetterTimeline(
                        letters = uiState.pendingLetters,
                        onLetterClick = { /* Cannot open sealed letters */ }
                    )
                }
            }
        }

        // FAB to write new letter
        FloatingActionButton(
            onClick = onWriteLetter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = StoicGold
        ) {
            Icon(Icons.Default.Create, "Write Letter", tint = StoicCharcoal)
        }
    }
}

/**
 * Sealed letter visualization - shows as a wax-sealed envelope.
 */
@Composable
fun SealedLetterCard(
    letter: FutureMessageEntity,
    daysRemaining: Long,
    modifier: Modifier = Modifier
) {
    val unlockProgress = remember(letter.deliveryDate) {
        val total = letter.deliveryDate - letter.createdAt
        val elapsed = System.currentTimeMillis() - letter.createdAt
        (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(StoicSlate)
            .withTexture(ProdyTextures.parchmentAsset, alpha = 0.1f)
    ) {
        // Envelope visual
        EnvelopeVisual(
            sealType = letter.sealType,
            unlockProgress = unlockProgress
        )

        // Content (blurred/hidden)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category chip
            SealTypeChip(letter.sealType)

            // Days remaining
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = daysRemaining.toString(),
                    style = StatNumberLargeStyle.copy(
                        color = StoicGold,
                        fontSize = 56.sp
                    )
                )
                Text(
                    text = "days until unveiling",
                    style = CaptionTextStyle.copy(color = StoicParchment.copy(alpha = 0.7f))
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = unlockProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = StoicGold,
                trackColor = StoicSlate
            )
        }

        // Wax seal overlay
        WaxSealOverlay(
            sealType = letter.sealType,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Wax seal visual component.
 */
@Composable
fun WaxSealOverlay(
    sealType: SealType,
    modifier: Modifier = Modifier
) {
    val sealColor = when (sealType) {
        SealType.STANDARD -> Color(0xFF8B0000)  // Deep red
        SealType.GOLD -> StoicGold
        SealType.CRIMSON -> Color(0xFFDC143C)
        SealType.EMERALD -> Color(0xFF2E8B57)
        SealType.BRONZE -> Color(0xFFCD7F32)
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        sealColor,
                        sealColor.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Seal icon (could be custom drawable)
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(32.dp)
        )
    }
}
```

#### 2.1.3 Sealing Animation

**New file:** `app/src/main/java/com/prody/prashant/ui/animations/SealingAnimation.kt`

```kotlin
/**
 * Full-screen sealing ceremony when user completes a letter.
 *
 * Sequence:
 * 1. Letter folds closed (paper folding animation)
 * 2. Wax drips onto seal point (particle effect)
 * 3. Stamp presses down (scale animation)
 * 4. Seal solidifies with glow
 * 5. Letter floats into vault (translate + fade)
 */

@Composable
fun SealingCeremonyOverlay(
    letter: FutureMessageEntity,
    onComplete: () -> Unit
) {
    var animationPhase by remember { mutableStateOf(SealingPhase.FOLDING) }

    // Phase durations
    LaunchedEffect(Unit) {
        delay(1500)  // Folding
        animationPhase = SealingPhase.DRIPPING
        delay(1000)  // Wax drip
        animationPhase = SealingPhase.STAMPING
        delay(800)   // Stamp press
        animationPhase = SealingPhase.SOLIDIFYING
        delay(1200)  // Solidify with glow
        animationPhase = SealingPhase.FLOATING
        delay(1500)  // Float away
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        when (animationPhase) {
            SealingPhase.FOLDING -> FoldingLetterAnimation()
            SealingPhase.DRIPPING -> WaxDripAnimation()
            SealingPhase.STAMPING -> StampPressAnimation()
            SealingPhase.SOLIDIFYING -> SealGlowAnimation()
            SealingPhase.FLOATING -> FloatToVaultAnimation()
        }

        // "Sealed until [date]" text
        Text(
            text = "Sealed until ${formatDate(letter.deliveryDate)}",
            style = WisdomAttributionStyle.copy(color = StoicGold),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}

enum class SealingPhase {
    FOLDING, DRIPPING, STAMPING, SOLIDIFYING, FLOATING
}
```

#### 2.1.4 Context Injection UI (Write Screen Enhancement)

```kotlin
/**
 * Enhanced letter writing flow with context capture.
 */
@Composable
fun WriteLetterScreen(
    onNavigateBack: () -> Unit,
    onLetterSealed: () -> Unit,
    viewModel: WriteLetterViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableStateOf(WriteStep.CONTEXT) }

    when (currentStep) {
        WriteStep.CONTEXT -> ContextCaptureStep(
            onMoodSelected = { mood, intensity ->
                viewModel.setMood(mood, intensity)
            },
            onChallengeEntered = { challenge ->
                viewModel.setChallenge(challenge)
            },
            onContinue = { currentStep = WriteStep.CONTENT }
        )

        WriteStep.CONTENT -> LetterContentStep(
            onBack = { currentStep = WriteStep.CONTEXT },
            onContentReady = { content ->
                viewModel.setContent(content)
                currentStep = WriteStep.DELIVERY
            }
        )

        WriteStep.DELIVERY -> DeliveryDateStep(
            onBack = { currentStep = WriteStep.CONTENT },
            onDateSelected = { date, sealType ->
                viewModel.scheduleDelivery(date, sealType)
                currentStep = WriteStep.SEALING
            }
        )

        WriteStep.SEALING -> {
            SealingCeremonyOverlay(
                letter = viewModel.currentLetter,
                onComplete = onLetterSealed
            )
        }
    }
}

@Composable
fun ContextCaptureStep(
    onMoodSelected: (Mood, Int) -> Unit,
    onChallengeEntered: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Before you write...",
            style = WisdomDisplayLarge.copy(color = StoicPrimary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Capture this moment. Your future self will see the contrast.",
            style = MaterialTheme.typography.bodyLarge,
            color = StoicInkFaded
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mood selector
        Text(
            text = "How are you feeling right now?",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        MoodSelector(
            onMoodSelected = onMoodSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Challenge text field
        Text(
            text = "What's your biggest challenge today?",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = "",
            onValueChange = onChallengeEntered,
            placeholder = { Text("Optional - helps provide perspective later") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        TactileButton(
            onClick = onContinue,
            text = "Continue to Letter",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

---

### 2.2 The "Council of Wisdom" - Revamping Leaderboard

**Current State:** `LeaderboardRow.kt` shows basic list with XP.

**New Vision:** "Circles of Growth" with cohort-based community.

#### 2.2.1 Data Model Enhancement

**File to modify:** `app/src/main/java/com/prody/prashant/data/local/entity/LeaderboardEntity.kt`

```kotlin
@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey
    val odudId: String,
    val displayName: String,
    val totalPoints: Int,
    val rank: Int,
    val previousRank: Int = rank,
    val currentStreak: Int = 0,
    val profileFrameRarity: String = "common",
    val bannerId: String = "default_dawn",
    val isCurrentUser: Boolean = false,
    val isDevBadgeHolder: Boolean = false,
    val isBetaTester: Boolean = false,
    val boostsReceived: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),

    // NEW: Cohort system
    val cohortId: String? = null,             // "seekers", "stoics", "sages"
    val cohortRank: Int? = null,              // Rank within cohort
    val cohortJoinedAt: Long? = null,

    // NEW: Anonymous support system
    val supportGiven: Int = 0,                // Times user has supported others
    val supportReceived: Int = 0,             // Times user received support
    val anonymousJournalShared: Boolean = false  // Opted into sharing
)

/**
 * Cohort definitions based on XP tiers.
 */
enum class GrowthCohort(
    val id: String,
    val displayName: String,
    val description: String,
    val minXp: Int,
    val maxXp: Int,
    val color: Color
) {
    SEEKERS(
        id = "seekers",
        displayName = "The Seekers",
        description = "Beginning the journey of self-discovery",
        minXp = 0,
        maxXp = 999,
        color = Color(0xFF6B8E8E)
    ),
    PRACTITIONERS(
        id = "practitioners",
        displayName = "The Practitioners",
        description = "Building daily habits of reflection",
        minXp = 1000,
        maxXp = 4999,
        color = Color(0xFF4A7C59)
    ),
    STOICS(
        id = "stoics",
        displayName = "The Stoics",
        description = "Embracing wisdom in daily life",
        minXp = 5000,
        maxXp = 14999,
        color = Color(0xFF1A3C34)
    ),
    SAGES(
        id = "sages",
        displayName = "The Sages",
        description = "Masters of their inner world",
        minXp = 15000,
        maxXp = Int.MAX_VALUE,
        color = StoicGold
    )
}
```

#### 2.2.2 Council Interface Architecture

**New file:** `app/src/main/java/com/prody/prashant/ui/screens/council/CouncilScreen.kt`

```kotlin
/**
 * The Council of Wisdom - Cohort-based community view.
 *
 * Features:
 * - Cohort visualization (circles, not list)
 * - Anonymous journal entries from peers
 * - "Kudos" and "Strength" sending system
 */

@Composable
fun CouncilScreen(
    onNavigateBack: () -> Unit,
    viewModel: CouncilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(CouncilTab.MY_CIRCLE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Council of Wisdom") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab selector
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                CouncilTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) }
                    )
                }
            }

            when (selectedTab) {
                CouncilTab.MY_CIRCLE -> MyCohortView(
                    cohort = uiState.userCohort,
                    members = uiState.cohortMembers,
                    userRank = uiState.userCohortRank
                )

                CouncilTab.PEER_WISDOM -> PeerWisdomFeed(
                    entries = uiState.anonymousEntries,
                    onSendKudos = { viewModel.sendKudos(it) },
                    onSendStrength = { viewModel.sendStrength(it) }
                )

                CouncilTab.ALL_CIRCLES -> AllCohortsView(
                    cohorts = GrowthCohort.entries.toList(),
                    userCohort = uiState.userCohort
                )
            }
        }
    }
}

enum class CouncilTab(val title: String) {
    MY_CIRCLE("My Circle"),
    PEER_WISDOM("Peer Wisdom"),
    ALL_CIRCLES("All Circles")
}

/**
 * Cohort visualization as a circle of members.
 */
@Composable
fun MyCohortView(
    cohort: GrowthCohort,
    members: List<LeaderboardEntryEntity>,
    userRank: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cohort banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(cohort.color, cohort.color.copy(alpha = 0.7f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = cohort.displayName,
                    style = WisdomDisplayLarge.copy(color = Color.White)
                )
                Text(
                    text = cohort.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your rank: #$userRank of ${members.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = StoicGold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Circle of members
        CohortCircleVisualization(
            members = members.take(12),
            currentUserRank = userRank
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Member list
        LazyColumn {
            items(members) { member ->
                CohortMemberRow(
                    member = member,
                    isCurrentUser = member.isCurrentUser
                )
            }
        }
    }
}

/**
 * Anonymous peer journal entries with support actions.
 */
@Composable
fun PeerWisdomFeed(
    entries: List<AnonymousJournalEntry>,
    onSendKudos: (String) -> Unit,
    onSendStrength: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Anonymous reflections from your circle",
                style = MaterialTheme.typography.bodyMedium,
                color = StoicInkFaded
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(entries) { entry ->
            AnonymousJournalCard(
                entry = entry,
                onSendKudos = { onSendKudos(entry.id) },
                onSendStrength = { onSendStrength(entry.id) }
            )
        }
    }
}

@Composable
fun AnonymousJournalCard(
    entry: AnonymousJournalEntry,
    onSendKudos: () -> Unit,
    onSendStrength: () -> Unit
) {
    ContentCard {
        Column {
            // Anonymous identifier
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StoicPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.anonymousId.take(2).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = StoicPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Fellow ${entry.cohort.displayName.dropLast(1)}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = entry.timeAgo,
                        style = CaptionTextStyle,
                        color = StoicInkFaded
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Journal excerpt (truncated)
            Text(
                text = entry.excerpt,
                style = WisdomSmallStyle,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Support actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SupportButton(
                    icon = Icons.Default.Favorite,
                    label = "Kudos",
                    count = entry.kudosCount,
                    onClick = onSendKudos
                )

                SupportButton(
                    icon = Icons.Default.FlashOn,
                    label = "Strength",
                    count = entry.strengthCount,
                    onClick = onSendStrength
                )
            }
        }
    }
}

data class AnonymousJournalEntry(
    val id: String,
    val anonymousId: String,  // e.g., "SK42" for Seeker #42
    val cohort: GrowthCohort,
    val excerpt: String,
    val timeAgo: String,
    val kudosCount: Int,
    val strengthCount: Int
)
```

---

### 2.3 "Deep Reading" Mode

**Current Problem:** Quotes are just text on screen with no engagement requirement.

**New Feature:** Focus Mode with contemplation timer.

#### 2.3.1 Deep Reading Screen

**New file:** `app/src/main/java/com/prody/prashant/ui/screens/wisdom/DeepReadingScreen.kt`

```kotlin
/**
 * Deep Reading Mode - Immersive wisdom experience.
 *
 * Features:
 * - Full-screen dimmed background
 * - Optional ambient audio
 * - 10-second contemplation timer
 * - Reflection prompt after reading
 */

@Composable
fun DeepReadingScreen(
    wisdom: WisdomContent,
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var phase by remember { mutableStateOf(ReadingPhase.PRESENTING) }
    var contemplationProgress by remember { mutableFloatStateOf(0f) }
    var isAudioEnabled by remember { mutableStateOf(false) }

    // Contemplation timer
    LaunchedEffect(phase) {
        if (phase == ReadingPhase.CONTEMPLATING) {
            val startTime = System.currentTimeMillis()
            val duration = 10_000L  // 10 seconds

            while (contemplationProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                contemplationProgress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                delay(50)
            }

            phase = ReadingPhase.REFLECTING
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Ambient background (subtle gradient)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            StoicPrimary.copy(alpha = 0.3f),
                            Color.Black
                        ),
                        radius = 800f
                    )
                )
        )

        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Close",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }

        // Audio toggle
        IconButton(
            onClick = { isAudioEnabled = !isAudioEnabled },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                if (isAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                "Toggle Audio",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(targetState = phase) { currentPhase ->
                when (currentPhase) {
                    ReadingPhase.PRESENTING -> {
                        PresentingView(
                            wisdom = wisdom,
                            onBeginContemplation = { phase = ReadingPhase.CONTEMPLATING }
                        )
                    }

                    ReadingPhase.CONTEMPLATING -> {
                        ContemplatingView(
                            wisdom = wisdom,
                            progress = contemplationProgress
                        )
                    }

                    ReadingPhase.REFLECTING -> {
                        ReflectingView(
                            wisdom = wisdom,
                            onComplete = onComplete
                        )
                    }
                }
            }
        }
    }

    // Audio playback effect
    if (isAudioEnabled) {
        AmbientAudioPlayer(audioRes = R.raw.ambient_meditation)
    }
}

@Composable
private fun PresentingView(
    wisdom: WisdomContent,
    onBeginContemplation: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category label
        Text(
            text = wisdom.category.uppercase(),
            style = OverlineTextStyle.copy(color = StoicGold),
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Quote with entrance animation
        Text(
            text = "\u201C${wisdom.text}\u201D",
            style = WisdomDisplayLarge.copy(
                color = StoicParchment,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Author
        Text(
            text = "— ${wisdom.author}",
            style = WisdomAttributionStyle.copy(color = StoicGold.copy(alpha = 0.8f))
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Begin button
        TactileButton(
            onClick = onBeginContemplation,
            text = "Begin Contemplation"
        )
    }
}

@Composable
private fun ContemplatingView(
    wisdom: WisdomContent,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quote (smaller, faded)
        Text(
            text = "\u201C${wisdom.text}\u201D",
            style = WisdomMediumStyle.copy(
                color = StoicParchment.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Contemplation prompt
        Text(
            text = "Let these words settle...",
            style = MaterialTheme.typography.bodyLarge,
            color = StoicGold.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Circular progress indicator
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = StoicGold,
                trackColor = StoicSlate,
                strokeWidth = 4.dp
            )

            Text(
                text = "${((1 - progress) * 10).toInt()}",
                style = StatNumberStyle.copy(color = StoicParchment)
            )
        }
    }
}

@Composable
private fun ReflectingView(
    wisdom: WisdomContent,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reflection prompt
        Text(
            text = "Reflection",
            style = OverlineTextStyle.copy(color = StoicGold),
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = wisdom.reflectionPrompt,
            style = WisdomMediumStyle.copy(
                color = StoicParchment,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        TactileButton(
            onClick = onComplete,
            text = "Complete"
        )
    }
}

enum class ReadingPhase {
    PRESENTING,
    CONTEMPLATING,
    REFLECTING
}

data class WisdomContent(
    val text: String,
    val author: String,
    val category: String,
    val reflectionPrompt: String
)
```

---

## Part 3: AI Integration - Making "Buddha" Functional

### 3.1 The "Observer" Architecture (RAG-Lite)

**Current Problem:** AI only sees current input, lacks context.

**Solution:** Sliding window context injection with recent journal + mood data.

#### 3.1.1 Context Manager

**New file:** `app/src/main/java/com/prody/prashant/data/ai/ContextManager.kt`

```kotlin
/**
 * Manages context injection for AI responses.
 * Provides sliding window of user history for personalized responses.
 */
@Singleton
class AiContextManager @Inject constructor(
    private val journalDao: JournalDao,
    private val userDao: UserDao
) {
    companion object {
        private const val JOURNAL_WINDOW_SIZE = 5
        private const val MOOD_WINDOW_DAYS = 7
    }

    /**
     * Builds context string for AI prompt injection.
     */
    suspend fun buildUserContext(): UserContext {
        val recentJournals = journalDao.getRecentEntries(JOURNAL_WINDOW_SIZE)
        val moodHistory = journalDao.getMoodHistoryDays(MOOD_WINDOW_DAYS)
        val userProfile = userDao.getUserProfile()
        val streak = userProfile?.currentStreak ?: 0

        return UserContext(
            recentJournals = recentJournals.map { entry ->
                JournalSummary(
                    date = formatDate(entry.createdAt),
                    mood = entry.mood,
                    moodIntensity = entry.moodIntensity,
                    excerpt = entry.content.take(200),
                    themes = entry.aiThemes?.split(",") ?: emptyList()
                )
            },
            moodTrend = analyzeMoodTrend(moodHistory),
            currentStreak = streak,
            dominantMood = findDominantMood(moodHistory),
            recentThemes = extractRecentThemes(recentJournals)
        )
    }

    /**
     * Formats context for injection into AI prompt.
     */
    fun formatContextForPrompt(context: UserContext): String {
        return buildString {
            appendLine("USER CONTEXT (for personalized response):")
            appendLine("----------------------------------------")

            appendLine("\nRecent Mood Pattern (last 7 days):")
            appendLine("- Dominant mood: ${context.dominantMood}")
            appendLine("- Trend: ${context.moodTrend}")
            appendLine("- Current streak: ${context.currentStreak} days")

            appendLine("\nRecent Journal Entries:")
            context.recentJournals.forEachIndexed { i, journal ->
                appendLine("\n[${journal.date}] Mood: ${journal.mood} (${journal.moodIntensity}/10)")
                appendLine("Themes: ${journal.themes.joinToString(", ")}")
                appendLine("Excerpt: \"${journal.excerpt}...\"")
            }

            appendLine("\nRecurrent Themes: ${context.recentThemes.joinToString(", ")}")
            appendLine("----------------------------------------")
        }
    }

    private fun analyzeMoodTrend(moodHistory: List<MoodDataPoint>): String {
        if (moodHistory.size < 2) return "Not enough data"

        val recentAvg = moodHistory.take(3).map { it.intensity }.average()
        val olderAvg = moodHistory.drop(3).map { it.intensity }.average()

        return when {
            recentAvg > olderAvg + 1 -> "Improving"
            recentAvg < olderAvg - 1 -> "Declining"
            else -> "Stable"
        }
    }

    private fun findDominantMood(moodHistory: List<MoodDataPoint>): String {
        return moodHistory
            .groupBy { it.mood }
            .maxByOrNull { it.value.size }
            ?.key ?: "Unknown"
    }

    private fun extractRecentThemes(journals: List<JournalEntryEntity>): List<String> {
        return journals
            .mapNotNull { it.aiThemes }
            .flatMap { it.split(",") }
            .map { it.trim() }
            .groupBy { it }
            .entries
            .sortedByDescending { it.value.size }
            .take(5)
            .map { it.key }
    }
}

data class UserContext(
    val recentJournals: List<JournalSummary>,
    val moodTrend: String,
    val currentStreak: Int,
    val dominantMood: String,
    val recentThemes: List<String>
)

data class JournalSummary(
    val date: String,
    val mood: String,
    val moodIntensity: Int,
    val excerpt: String,
    val themes: List<String>
)

data class MoodDataPoint(
    val date: Long,
    val mood: String,
    val intensity: Int
)
```

#### 3.1.2 Enhanced Buddha System Prompt

**File to modify:** `app/src/main/java/com/prody/prashant/data/ai/GeminiService.kt`

```kotlin
private object BuddhaSystemPrompt {

    /**
     * UPDATED Core Identity - More Mentor, Less Chatbot
     */
    const val CORE_IDENTITY = """
You are a mentor within the Prody app - a guide for personal growth and self-reflection.

CRITICAL RULES:
- You are NOT a chatbot. Do not ask "How can I help?" or similar.
- Instead, OFFER a perspective. Lead the conversation.
- Be concise, firm, and compassionate.
- Think of yourself as a wise friend who has lived fully, not a service assistant.

PERSONALITY:
- Warm but not sycophantic
- Wise but not preachy
- Practical but not clinical
- Draw from Stoic philosophy naturally, without force-quoting Marcus Aurelius constantly
- Occasionally use metaphor or imagery

COMMUNICATION STYLE:
- Open with acknowledgment of what you've observed (using context provided)
- Provide ONE clear insight, not a list of advice
- Include a thought-provoking question
- Keep responses to 100-200 words typically
- Use markdown sparingly (bold for emphasis only)

WHAT TO AVOID:
- Generic platitudes ("Take things one day at a time")
- Excessive Stoic name-dropping
- Asking how you can help
- Being overly formal or stiff
- Lists of bullet points
"""

    /**
     * Context-aware journal response prompt.
     */
    fun getContextualJournalResponsePrompt(
        userContext: String,
        mood: Mood,
        moodIntensity: Int,
        content: String
    ): String {
        return """
$CORE_IDENTITY

$userContext

CURRENT ENTRY:
- Mood: ${mood.displayName}
- Intensity: $moodIntensity/10
- Content: "$content"

IMPORTANT: Use the context above to personalize your response. If you notice patterns
(e.g., "I see you've been feeling anxious about work for several days"), mention them
naturally. This is what separates you from a generic AI.

Respond as a mentor who has been observing this person's journey.
"""
    }
}
```

#### 3.1.3 Integration in BuddhaAiService

**File to modify:** `app/src/main/java/com/prody/prashant/data/ai/BuddhaAiService.kt`

```kotlin
@Singleton
class BuddhaAiService @Inject constructor(
    private val geminiService: GeminiService,
    private val cacheManager: AiCacheManager,
    private val contextManager: AiContextManager  // NEW
) {

    /**
     * Context-aware journal response with user history.
     */
    suspend fun getContextualJournalResponse(
        content: String,
        mood: Mood,
        moodIntensity: Int,
        wordCount: Int
    ): GeminiResult<String> {
        // Build context
        val userContext = contextManager.buildUserContext()
        val contextString = contextManager.formatContextForPrompt(userContext)

        // Generate with context
        return geminiService.generateContextualJournalResponse(
            contextString = contextString,
            content = content,
            mood = mood,
            moodIntensity = moodIntensity,
            wordCount = wordCount
        )
    }
}
```

---

### 3.2 Automatic Tagging & Sorting

**Current Problem:** Stats are manual, no automatic categorization.

**Solution:** Background AI classification of journal entries.

#### 3.2.1 Classification Service

**New file:** `app/src/main/java/com/prody/prashant/data/ai/JournalClassifier.kt`

```kotlin
/**
 * Background service for automatic journal entry classification.
 * Uses Gemini to extract:
 * - Primary emotion
 * - Themes/topics
 * - Insight summary
 */
@Singleton
class JournalClassifier @Inject constructor(
    private val geminiService: GeminiService,
    private val journalDao: JournalDao
) {

    /**
     * Classifies a journal entry in the background.
     * Called after save, non-blocking.
     */
    suspend fun classifyEntry(entryId: Long) = withContext(Dispatchers.IO) {
        val entry = journalDao.getEntryById(entryId) ?: return@withContext

        if (entry.aiInsightGenerated) return@withContext  // Already classified

        val prompt = buildClassificationPrompt(entry.content)
        val result = geminiService.generateCustomResponse(prompt, includeSystemPrompt = false)

        when (result) {
            is GeminiResult.Success -> {
                val classification = parseClassificationResponse(result.data)
                journalDao.updateAiInsights(
                    entryId = entryId,
                    emotionLabel = classification.emotion,
                    themes = classification.themes.joinToString(","),
                    insight = classification.insight,
                    insightGenerated = true
                )
            }
            else -> {
                // Log error, don't block user experience
                Log.w("JournalClassifier", "Failed to classify entry $entryId")
            }
        }
    }

    private fun buildClassificationPrompt(content: String): String {
        return """
Analyze this journal entry and extract:
1. Primary emotion (one word: e.g., Anxiety, Joy, Frustration, Gratitude)
2. Topics/themes (2-4 words, comma-separated: e.g., Career, Relationships, Health)
3. Brief insight (one sentence summary of what the user is processing)

Journal entry:
"$content"

Respond in this exact format:
EMOTION: [emotion]
THEMES: [theme1, theme2, theme3]
INSIGHT: [insight sentence]
"""
    }

    private fun parseClassificationResponse(response: String): JournalClassification {
        val lines = response.lines()

        val emotion = lines.find { it.startsWith("EMOTION:") }
            ?.substringAfter("EMOTION:")
            ?.trim() ?: "Neutral"

        val themes = lines.find { it.startsWith("THEMES:") }
            ?.substringAfter("THEMES:")
            ?.split(",")
            ?.map { it.trim() } ?: emptyList()

        val insight = lines.find { it.startsWith("INSIGHT:") }
            ?.substringAfter("INSIGHT:")
            ?.trim() ?: ""

        return JournalClassification(emotion, themes, insight)
    }
}

data class JournalClassification(
    val emotion: String,
    val themes: List<String>,
    val insight: String
)
```

#### 3.2.2 Topic Heatmap Analytics

**New file:** `app/src/main/java/com/prody/prashant/domain/analytics/TopicAnalytics.kt`

```kotlin
/**
 * Analytics for topic-based insights from classified journal entries.
 */
@Singleton
class TopicAnalytics @Inject constructor(
    private val journalDao: JournalDao
) {

    /**
     * Generates topic distribution for stats screen.
     */
    suspend fun getTopicDistribution(
        fromDate: Long = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
    ): TopicDistribution {
        val entries = journalDao.getEntriesSince(fromDate)
            .filter { it.aiInsightGenerated }

        if (entries.isEmpty()) return TopicDistribution.empty()

        val topicCounts = mutableMapOf<String, Int>()
        val emotionCounts = mutableMapOf<String, Int>()

        entries.forEach { entry ->
            entry.aiThemes?.split(",")?.forEach { theme ->
                val cleanTheme = theme.trim()
                if (cleanTheme.isNotEmpty()) {
                    topicCounts[cleanTheme] = topicCounts.getOrDefault(cleanTheme, 0) + 1
                }
            }

            entry.aiEmotionLabel?.let { emotion ->
                emotionCounts[emotion] = emotionCounts.getOrDefault(emotion, 0) + 1
            }
        }

        val totalEntries = entries.size.toFloat()

        return TopicDistribution(
            topics = topicCounts.map { (topic, count) ->
                TopicPercentage(topic, (count / totalEntries * 100).roundToInt())
            }.sortedByDescending { it.percentage },
            emotions = emotionCounts.map { (emotion, count) ->
                EmotionPercentage(emotion, (count / totalEntries * 100).roundToInt())
            }.sortedByDescending { it.percentage },
            totalEntries = entries.size
        )
    }

    /**
     * Generates insight summary for display.
     */
    fun generateInsightSummary(distribution: TopicDistribution): String {
        val topTopic = distribution.topics.firstOrNull() ?: return ""
        val topEmotion = distribution.emotions.firstOrNull() ?: return ""

        return "${topTopic.percentage}% of your reflections involve ${topTopic.topic}. " +
               "Your dominant emotion has been ${topEmotion.emotion.lowercase()}."
    }
}

data class TopicDistribution(
    val topics: List<TopicPercentage>,
    val emotions: List<EmotionPercentage>,
    val totalEntries: Int
) {
    companion object {
        fun empty() = TopicDistribution(emptyList(), emptyList(), 0)
    }
}

data class TopicPercentage(val topic: String, val percentage: Int)
data class EmotionPercentage(val emotion: String, val percentage: Int)
```

---

### 3.3 The Morning Briefing

**New Feature:** Proactive daily focus generated via WorkManager.

#### 3.3.1 WorkManager Implementation

**New file:** `app/src/main/java/com/prody/prashant/worker/MorningBriefingWorker.kt`

```kotlin
/**
 * Daily worker that generates a personalized morning briefing.
 * Scheduled to run at user's preferred wake time (default: 7 AM).
 */
@HiltWorker
class MorningBriefingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val buddhaAiService: BuddhaAiService,
    private val contextManager: AiContextManager,
    private val journalDao: JournalDao,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Build context from yesterday
            val yesterday = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            val yesterdayEntries = journalDao.getEntriesForDate(yesterday)

            if (yesterdayEntries.isEmpty()) {
                // No entry yesterday, generate general briefing
                generateGeneralBriefing()
            } else {
                // Generate personalized briefing based on yesterday
                generatePersonalizedBriefing(yesterdayEntries)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("MorningBriefingWorker", "Failed to generate briefing", e)
            Result.retry()
        }
    }

    private suspend fun generatePersonalizedBriefing(
        yesterdayEntries: List<JournalEntryEntity>
    ) {
        val context = contextManager.buildUserContext()
        val yesterday = yesterdayEntries.first()

        val prompt = """
Based on this user's recent history and yesterday's journal entry, generate a brief
morning focus for today.

USER CONTEXT:
${contextManager.formatContextForPrompt(context)}

YESTERDAY'S ENTRY:
Mood: ${yesterday.mood}
Themes: ${yesterday.aiThemes}
Content excerpt: "${yesterday.content.take(300)}"

Generate a "Daily Focus" that:
1. Briefly acknowledges yesterday (1 sentence)
2. Suggests a Stoic focus for today (1-2 sentences)
3. Proposes one small exercise or intention

Keep total response under 150 words. Be practical, not preachy.
"""

        val result = buddhaAiService.getCustomResponse(prompt)

        when (result) {
            is GeminiResult.Success -> {
                // Store briefing for home screen display
                preferencesManager.setMorningBriefing(
                    MorningBriefing(
                        content = result.data,
                        generatedAt = System.currentTimeMillis(),
                        basedOnYesterday = true
                    )
                )

                // Send notification
                sendBriefingNotification(result.data)
            }
            else -> {
                Log.w("MorningBriefingWorker", "Failed to generate personalized briefing")
                generateGeneralBriefing()  // Fallback
            }
        }
    }

    private suspend fun generateGeneralBriefing() {
        val result = buddhaAiService.getMorningReflection()

        when (result) {
            is GeminiResult.Success -> {
                preferencesManager.setMorningBriefing(
                    MorningBriefing(
                        content = result.data,
                        generatedAt = System.currentTimeMillis(),
                        basedOnYesterday = false
                    )
                )

                sendBriefingNotification(result.data)
            }
            else -> {
                // Use fallback wisdom
                preferencesManager.setMorningBriefing(
                    MorningBriefing(
                        content = BuddhaWisdom.getRandomWisdom(),
                        generatedAt = System.currentTimeMillis(),
                        basedOnYesterday = false
                    )
                )
            }
        }
    }

    private fun sendBriefingNotification(content: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_MORNING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Today's Focus")
            .setContentText(content.take(100) + "...")
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_BRIEFING, notification)
    }

    companion object {
        private const val CHANNEL_MORNING = "morning_briefing"
        private const val NOTIFICATION_ID_BRIEFING = 1001

        /**
         * Schedules the daily morning briefing worker.
         */
        fun schedule(context: Context, hour: Int = 7, minute: Int = 0) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                if (timeInMillis <= now.timeInMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val delay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<MorningBriefingWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "morning_briefing",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
        }
    }
}

data class MorningBriefing(
    val content: String,
    val generatedAt: Long,
    val basedOnYesterday: Boolean
)
```

#### 3.3.2 Home Screen Priority Card

```kotlin
/**
 * Morning Briefing Card for Home Screen.
 * Displayed as a "Priority Card" at the top.
 */
@Composable
fun MorningBriefingCard(
    briefing: MorningBriefing?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (briefing == null) return

    // Only show if generated today
    val isFromToday = remember(briefing.generatedAt) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        briefing.generatedAt >= todayStart
    }

    if (!isFromToday) return

    ContentCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = StoicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Today's Focus",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = briefing.content,
                style = BuddhaResponseStyle.copy(
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                ),
                color = StoicInk
            )

            if (briefing.basedOnYesterday) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Based on your reflection yesterday",
                    style = CaptionTextStyle,
                    color = StoicInkFaded
                )
            }
        }
    }
}
```

---

## Implementation Roadmap

### Phase 1: Design System (Week 1-2)
1. Implement new color palette in `Color.kt`
2. Add texture assets and composable
3. Update typography enforcement
4. Refactor card hierarchy

### Phase 2: UI Refinements (Week 2-3)
1. Collapsing toolbar implementation
2. Chip group for categories
3. Badge system for milestones
4. Interactive button states

### Phase 3: Time Capsule Vault (Week 3-4)
1. Enhanced data model
2. Vault UI implementation
3. Sealing animation
4. Context capture flow

### Phase 4: Council of Wisdom (Week 4-5)
1. Cohort system backend
2. Council UI screens
3. Anonymous peer support
4. Kudos/Strength system

### Phase 5: Deep Reading Mode (Week 5)
1. Full-screen immersive view
2. Contemplation timer
3. Ambient audio support
4. Reflection prompts

### Phase 6: AI Enhancement (Week 6-7)
1. Context manager implementation
2. Background classification
3. Topic analytics
4. Morning briefing worker

### Phase 7: Polish & Testing (Week 7-8)
1. Animation refinement
2. Performance optimization
3. Accessibility audit
4. User testing

---

## Files to Create/Modify Summary

### New Files:
- `ui/theme/Texture.kt`
- `ui/screens/futuremessage/VaultScreen.kt`
- `ui/screens/council/CouncilScreen.kt`
- `ui/screens/wisdom/DeepReadingScreen.kt`
- `ui/animations/SealingAnimation.kt`
- `data/ai/ContextManager.kt`
- `data/ai/JournalClassifier.kt`
- `domain/analytics/TopicAnalytics.kt`
- `worker/MorningBriefingWorker.kt`

### Modified Files:
- `ui/theme/Color.kt` - New palette
- `ui/theme/Type.kt` - Strict usage rules
- `ui/components/ProdyCard.kt` - Card hierarchy
- `ui/components/GamificationComponents.kt` - Badge system
- `data/local/entity/FutureMessageEntity.kt` - Context fields
- `data/local/entity/LeaderboardEntity.kt` - Cohort fields
- `data/ai/GeminiService.kt` - Updated prompts
- `data/ai/BuddhaAiService.kt` - Context integration

### Asset Requirements:
- `res/drawable/texture_paper_grain.png`
- `res/drawable/texture_subtle_noise.png`
- `res/drawable/texture_parchment.png`
- `res/raw/ambient_meditation.mp3` (or similar)

---

*End of Specification Document*
