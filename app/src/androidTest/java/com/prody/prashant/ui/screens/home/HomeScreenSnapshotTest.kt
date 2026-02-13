package com.prody.prashant.ui.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.captureToImage
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class HomeScreenSnapshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingState_snapshot() {
        composeRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState(isLoading = true),
                    onNavigateToJournal = {},
                    onNavigateToQuotes = {},
                    onNavigateToFutureMessage = {},
                    onNavigateToHaven = {},
                    onNavigateToProfile = {},
                    onOpenNotificationSettings = {},
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithTag("home_loading").assertIsDisplayed()
        assertNodeImageIsNotBlank("home_loading")
    }

    @Test
    fun errorState_snapshot() {
        composeRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        hasLoadError = true,
                        error = "Failed to load home data"
                    ),
                    onNavigateToJournal = {},
                    onNavigateToQuotes = {},
                    onNavigateToFutureMessage = {},
                    onNavigateToHaven = {},
                    onNavigateToProfile = {},
                    onOpenNotificationSettings = {},
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithTag("home_error").assertIsDisplayed()
        assertNodeImageIsNotBlank("home_error")
    }

    @Test
    fun emptyState_snapshot() {
        composeRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        isLoading = false,
                        aggregateState = HomeAggregateState(hasData = false)
                    ),
                    onNavigateToJournal = {},
                    onNavigateToQuotes = {},
                    onNavigateToFutureMessage = {},
                    onNavigateToHaven = {},
                    onNavigateToProfile = {},
                    onOpenNotificationSettings = {},
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithTag("home_empty").assertIsDisplayed()
        assertNodeImageIsNotBlank("home_empty")
    }

    private fun assertNodeImageIsNotBlank(tag: String) {
        val image = composeRule.onNodeWithTag(tag).captureToImage()
        val pixelMap = image.toPixelMap()
        var hasNonTransparentPixel = false
        loop@ for (x in 0 until pixelMap.width) {
            for (y in 0 until pixelMap.height) {
                if (pixelMap[x, y].alpha > 0f) {
                    hasNonTransparentPixel = true
                    break@loop
                }
            }
        }
        assertTrue(hasNonTransparentPixel, "Expected captured snapshot for '$tag' to contain UI pixels")
    }
}
