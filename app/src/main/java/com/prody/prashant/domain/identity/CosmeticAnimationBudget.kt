package com.prody.prashant.domain.identity

/**
 * [CosmeticAnimationBudget] - Animation budget management for cosmetics
 *
 * Controls the total number and intensity of animations displayed simultaneously.
 * This prevents "animation soup" where too many moving elements create visual noise.
 *
 * Design Philosophy:
 * - Maximum 1-2 subtle animations per screen
 * - Legendary items get animation priority
 * - Animations should be so subtle they're barely noticed
 * - Performance budget matters: fewer animations = smoother scrolling
 *
 * Animation Types (by subtlety, most to least):
 * 1. Shimmer: Very slow light sweep across surface
 * 2. Pulse: Extremely slow opacity fluctuation
 * 3. Flow: Slow movement of pattern elements
 * 4. Glow: Subtle border luminance change (use sparingly)
 *
 * Example usage:
 * ```
 * val budget = CosmeticAnimationBudget.forScreen(ScreenContext.PROFILE)
 * val animatedCosmetics = budget.allocateAnimations(
 *     banner = userBanner,
 *     frame = userFrame,
 *     badges = pinnedBadges
 * )
 *
 * // Only animate what the budget allows
 * if (animatedCosmetics.shouldAnimateBanner) {
 *     AnimatedBanner(banner = userBanner)
 * } else {
 *     StaticBanner(banner = userBanner)
 * }
 * ```
 */
object CosmeticAnimationBudget {

    /**
     * Maximum animations allowed per screen type.
     */
    const val MAX_ANIMATIONS_PROFILE = 2
    const val MAX_ANIMATIONS_LEADERBOARD = 1
    const val MAX_ANIMATIONS_SHARE_CARD = 1
    const val MAX_ANIMATIONS_LIST_ITEM = 0

    /**
     * Animation types in order of visual subtlety.
     * Lower ordinal = more subtle.
     */
    enum class AnimationType(
        val id: String,
        val displayName: String,
        val performanceCost: Float, // 0.0 to 1.0
        val subtletyRating: Float   // 0.0 = very subtle, 1.0 = noticeable
    ) {
        /** Very slow light sweep - barely perceptible */
        SHIMMER(
            id = "shimmer",
            displayName = "Shimmer",
            performanceCost = 0.2f,
            subtletyRating = 0.1f
        ),

        /** Extremely slow opacity fluctuation */
        PULSE(
            id = "pulse",
            displayName = "Pulse",
            performanceCost = 0.15f,
            subtletyRating = 0.2f
        ),

        /** Slow movement of pattern elements */
        FLOW(
            id = "flow",
            displayName = "Flow",
            performanceCost = 0.3f,
            subtletyRating = 0.3f
        ),

        /** Subtle border luminance - most noticeable, use rarely */
        GLOW(
            id = "glow",
            displayName = "Glow",
            performanceCost = 0.4f,
            subtletyRating = 0.5f
        );

        companion object {
            fun fromId(id: String): AnimationType? = entries.find { it.id == id }

            /** Get animations sorted by subtlety (most subtle first) */
            val sortedBySubtlety: List<AnimationType>
                get() = entries.sortedBy { it.subtletyRating }
        }
    }

    /**
     * Screen contexts with different animation budgets.
     */
    enum class ScreenContext(
        val maxAnimations: Int,
        val maxPerformanceCost: Float,
        val allowedAnimationTypes: Set<AnimationType>
    ) {
        /** User's own profile - most generous budget */
        PROFILE(
            maxAnimations = MAX_ANIMATIONS_PROFILE,
            maxPerformanceCost = 0.6f,
            allowedAnimationTypes = AnimationType.entries.toSet()
        ),

        /** Viewing another user's profile */
        OTHER_PROFILE(
            maxAnimations = 1,
            maxPerformanceCost = 0.3f,
            allowedAnimationTypes = setOf(AnimationType.SHIMMER, AnimationType.PULSE)
        ),

        /** Leaderboard list - needs to be snappy */
        LEADERBOARD(
            maxAnimations = MAX_ANIMATIONS_LEADERBOARD,
            maxPerformanceCost = 0.2f,
            allowedAnimationTypes = setOf(AnimationType.SHIMMER)
        ),

        /** Share card being generated */
        SHARE_CARD(
            maxAnimations = MAX_ANIMATIONS_SHARE_CARD,
            maxPerformanceCost = 0.4f,
            allowedAnimationTypes = setOf(AnimationType.SHIMMER, AnimationType.PULSE)
        ),

        /** List items (journal, achievements, etc.) - no animations */
        LIST_ITEM(
            maxAnimations = MAX_ANIMATIONS_LIST_ITEM,
            maxPerformanceCost = 0f,
            allowedAnimationTypes = emptySet()
        ),

        /** Gallery/picker view - minimal animations */
        GALLERY(
            maxAnimations = 1,
            maxPerformanceCost = 0.3f,
            allowedAnimationTypes = setOf(AnimationType.SHIMMER)
        )
    }

    /**
     * Priority levels for animation allocation.
     * Higher priority cosmetics get animations first.
     */
    enum class AnimationPriority(val weight: Int) {
        /** Special badges (DEV, Founder) - highest priority */
        SPECIAL(100),
        /** Legendary rarity */
        LEGENDARY(80),
        /** Epic rarity */
        EPIC(60),
        /** Banner - usually the most prominent */
        BANNER(50),
        /** Frame around avatar */
        FRAME(40),
        /** Rare rarity */
        RARE(30),
        /** Common - lowest priority, rarely animated */
        COMMON(10)
    }

    /**
     * Represents a cosmetic item requesting animation.
     */
    data class AnimationRequest(
        val cosmeticId: String,
        val cosmeticType: CosmeticType,
        val rarity: CosmeticRarity,
        val preferredAnimationType: AnimationType,
        val isSpecial: Boolean = false
    ) {
        val priority: Int get() = when {
            isSpecial -> AnimationPriority.SPECIAL.weight
            rarity == CosmeticRarity.LEGENDARY -> AnimationPriority.LEGENDARY.weight
            rarity == CosmeticRarity.EPIC -> AnimationPriority.EPIC.weight
            cosmeticType == CosmeticType.BANNER -> AnimationPriority.BANNER.weight
            cosmeticType == CosmeticType.FRAME -> AnimationPriority.FRAME.weight
            rarity == CosmeticRarity.RARE -> AnimationPriority.RARE.weight
            else -> AnimationPriority.COMMON.weight
        }
    }

    /**
     * Types of cosmetics that can be animated.
     */
    enum class CosmeticType(val id: String) {
        BANNER("banner"),
        FRAME("frame"),
        BADGE("badge"),
        ACCENT("accent")
    }

    /**
     * Result of animation budget allocation.
     */
    data class AnimationAllocation(
        val screenContext: ScreenContext,
        val allocatedAnimations: Map<String, AnimationType>,
        val totalPerformanceCost: Float,
        val budgetRemaining: Int
    ) {
        /** Check if a specific cosmetic should be animated */
        fun shouldAnimate(cosmeticId: String): Boolean =
            allocatedAnimations.containsKey(cosmeticId)

        /** Get the animation type for a cosmetic, or null if not animated */
        fun getAnimationType(cosmeticId: String): AnimationType? =
            allocatedAnimations[cosmeticId]

        /** Check if budget is exhausted */
        val isBudgetExhausted: Boolean get() = budgetRemaining <= 0

        /** Check if any animations are allocated */
        val hasAnimations: Boolean get() = allocatedAnimations.isNotEmpty()
    }

    /**
     * Allocates animation budget across cosmetic items.
     *
     * @param screenContext The screen where cosmetics will be displayed
     * @param requests List of cosmetics requesting animation
     * @return Allocation result with animations assigned to highest priority items
     */
    fun allocateAnimations(
        screenContext: ScreenContext,
        requests: List<AnimationRequest>
    ): AnimationAllocation {
        if (screenContext.maxAnimations == 0 || requests.isEmpty()) {
            return AnimationAllocation(
                screenContext = screenContext,
                allocatedAnimations = emptyMap(),
                totalPerformanceCost = 0f,
                budgetRemaining = screenContext.maxAnimations
            )
        }

        // Sort by priority (highest first)
        val sortedRequests = requests.sortedByDescending { it.priority }

        val allocated = mutableMapOf<String, AnimationType>()
        var remainingBudget = screenContext.maxAnimations
        var totalCost = 0f

        for (request in sortedRequests) {
            if (remainingBudget <= 0) break
            if (totalCost >= screenContext.maxPerformanceCost) break

            // Find the best animation type for this request
            val animationType = findBestAnimationType(
                request = request,
                context = screenContext,
                remainingPerformanceBudget = screenContext.maxPerformanceCost - totalCost
            )

            if (animationType != null) {
                allocated[request.cosmeticId] = animationType
                totalCost += animationType.performanceCost
                remainingBudget--
            }
        }

        return AnimationAllocation(
            screenContext = screenContext,
            allocatedAnimations = allocated,
            totalPerformanceCost = totalCost,
            budgetRemaining = remainingBudget
        )
    }

    /**
     * Finds the best animation type for a request given constraints.
     */
    private fun findBestAnimationType(
        request: AnimationRequest,
        context: ScreenContext,
        remainingPerformanceBudget: Float
    ): AnimationType? {
        // First try the preferred type if allowed
        if (context.allowedAnimationTypes.contains(request.preferredAnimationType) &&
            request.preferredAnimationType.performanceCost <= remainingPerformanceBudget
        ) {
            return request.preferredAnimationType
        }

        // Otherwise find the most subtle allowed type within budget
        return AnimationType.sortedBySubtlety
            .filter { context.allowedAnimationTypes.contains(it) }
            .filter { it.performanceCost <= remainingPerformanceBudget }
            .firstOrNull()
    }

    /**
     * Creates animation requests from user cosmetics.
     */
    fun createRequests(
        bannerId: String?,
        bannerRarity: CosmeticRarity?,
        bannerIsAnimated: Boolean?,
        frameId: String?,
        frameRarity: CosmeticRarity?,
        frameIsAnimated: Boolean?,
        badgeIds: List<String> = emptyList(),
        specialBadgeIds: Set<String> = emptySet()
    ): List<AnimationRequest> {
        val requests = mutableListOf<AnimationRequest>()

        // Banner request
        if (bannerId != null && bannerIsAnimated == true && bannerRarity != null) {
            requests.add(
                AnimationRequest(
                    cosmeticId = bannerId,
                    cosmeticType = CosmeticType.BANNER,
                    rarity = bannerRarity,
                    preferredAnimationType = when (bannerRarity) {
                        CosmeticRarity.LEGENDARY -> AnimationType.SHIMMER
                        CosmeticRarity.EPIC -> AnimationType.PULSE
                        else -> AnimationType.SHIMMER
                    }
                )
            )
        }

        // Frame request
        if (frameId != null && frameIsAnimated == true && frameRarity != null) {
            requests.add(
                AnimationRequest(
                    cosmeticId = frameId,
                    cosmeticType = CosmeticType.FRAME,
                    rarity = frameRarity,
                    preferredAnimationType = AnimationType.PULSE
                )
            )
        }

        // Badge requests (only special badges can animate)
        badgeIds.filter { specialBadgeIds.contains(it) }.forEach { badgeId ->
            requests.add(
                AnimationRequest(
                    cosmeticId = badgeId,
                    cosmeticType = CosmeticType.BADGE,
                    rarity = CosmeticRarity.LEGENDARY,
                    preferredAnimationType = AnimationType.SHIMMER,
                    isSpecial = true
                )
            )
        }

        return requests
    }

    /**
     * Quick check if any animations should be shown for a rarity.
     */
    fun shouldConsiderAnimation(rarity: CosmeticRarity): Boolean =
        rarity >= CosmeticRarity.EPIC

    /**
     * Gets the recommended animation duration based on rarity.
     * Longer durations = more subtle (less noticeable).
     */
    fun getRecommendedDuration(rarity: CosmeticRarity): Long = when (rarity) {
        CosmeticRarity.LEGENDARY -> 6000L  // 6 seconds - very slow
        CosmeticRarity.EPIC -> 4000L       // 4 seconds
        CosmeticRarity.RARE -> 3000L       // 3 seconds
        CosmeticRarity.COMMON -> 0L        // No animation
    }

    /**
     * Gets the recommended animation alpha (opacity of the effect).
     */
    fun getRecommendedAlpha(rarity: CosmeticRarity): Float = when (rarity) {
        CosmeticRarity.LEGENDARY -> 0.15f  // Very subtle
        CosmeticRarity.EPIC -> 0.12f
        CosmeticRarity.RARE -> 0.1f
        CosmeticRarity.COMMON -> 0f
    }
}
