package com.prody.prashant.ui.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prody.prashant.ui.components.ProdyPrimaryButton
import com.prody.prashant.ui.theme.ProdyTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CriticalAccessibilityFlowsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setupAccessibilityChecks() {
        AccessibilityChecks.enable().setRunChecksFromRootView(true)
    }

    @Test
    fun primaryButton_exposesRoleAndStateDescription() {
        composeRule.setContent {
            ProdyTheme {
                ProdyPrimaryButton(
                    text = "Save entry",
                    contentDescription = "Save journal entry",
                    loading = true,
                    onClick = {},
                    modifier = Modifier.semantics { testTag = "save_button" }
                )
            }
        }

        composeRule.onNodeWithTag("save_button")
            .assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Loading"))
    }

    @Test
    fun bottomActions_haveDeterministicTraversalOrderForTalkBackAndSwitchAccess() {
        composeRule.setContent {
            ProdyTheme {
                Row {
                    listOf("Home", "Journal", "Profile").forEachIndexed { index, label ->
                        ProdyPrimaryButton(
                            text = label,
                            onClick = {},
                            modifier = Modifier.semantics {
                                testTag = "tab_$label"
                                role = Role.Tab
                                traversalIndex = index.toFloat()
                                contentDescription = label
                                stateDescription = if (index == 0) "Selected" else "Not selected"
                            }
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithTag("tab_Home")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 0f))
        composeRule.onNodeWithTag("tab_Journal")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 1f))
        composeRule.onNodeWithTag("tab_Profile")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 2f))
    }

    @Test
    fun longButtonLabel_remainsAccessibleAtLargeFontScale() {
        composeRule.setContent {
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(baseDensity.density, fontScale = 1.8f)) {
                ProdyTheme {
                    Column {
                        ProdyPrimaryButton(
                            text = "This is a very long action label used to verify truncation handling at larger font scales",
                            contentDescription = "Long action label",
                            onClick = {},
                            modifier = Modifier.semantics { testTag = "long_label_button" }
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithTag("long_label_button").assertExists().assertHasClickAction()
    }
}
