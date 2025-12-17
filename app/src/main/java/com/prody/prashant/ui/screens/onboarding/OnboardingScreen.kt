package com.prody.prashant.ui.screens.onboarding

/*
 * ============================================================================
 * LEGACY ONBOARDING IMPLEMENTATION - COMMENTED OUT
 * ============================================================================
 * The previous 6-page onboarding flow has been replaced with a new
 * 3-screen premium "Journey" flow. See OnboardingJourneyScreen.kt
 * ============================================================================
 *
 * Previous implementation contained:
 * - 6-page HorizontalPager flow
 * - WelcomeScreen, JournalingFeatureScreen, GamificationLeaderboardScreen
 * - XpArcScreen, DailyWisdomFeaturesScreen, PersonalizedInsightsScreen
 * - Various helper composables for UI elements
 *
 * All legacy code has been removed and replaced with the new implementation below.
 * ============================================================================
 */

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Onboarding Screen Entry Point
 *
 * This now delegates to the new OnboardingJourneyScreen which implements
 * the premium 3-screen "Journey" onboarding flow:
 *
 * 1. "The Awakening" - Welcome with spinning lotus icon
 * 2. "The Path" - Gamification showcase with custom arc
 * 3. "The Contract" - Commitment with name input and seal animation
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    OnboardingJourneyScreen(
        onComplete = {
            viewModel.completeOnboarding()
            onComplete()
        }
    )
}
