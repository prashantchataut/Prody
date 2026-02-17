package com.prody.prashant.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PACKAGE_NAME = "com.prody.prashant.debug"
private const val WAIT_TIMEOUT_MS = 5_000L

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @get:Rule
    val macrobenchmarkRule = MacrobenchmarkRule()

    @Test
    fun generateBaselineProfileForTopDestinations() {
        baselineProfileRule.collect(
            packageName = PACKAGE_NAME,
            includeInStartupProfile = true,
            maxIterations = 8,
            stableIterations = 3,
            strictStability = false,
            outputFilePrefix = "top_destinations"
        ) {
            exerciseTopDestinations()
        }
    }

    @Test
    fun frameAndRecompositionMetricsForTopDestinations() {
        macrobenchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                FrameTimingMetric(),
                TraceSectionMetric("compose:recompose")
            ),
            iterations = 6,
            startupMode = StartupMode.WARM,
            baselineProfileMode = BaselineProfileMode.Require
        ) {
            pressHome()
            startActivityAndWait()
            exerciseTopDestinations()
        }
    }

    private fun MacrobenchmarkScope.exerciseTopDestinations() {
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), WAIT_TIMEOUT_MS)
        navigateTo("Journal")
        navigateTo("Haven")
        navigateTo("Stats")
        navigateTo("Profile")
        navigateTo("Home")
    }

    private fun MacrobenchmarkScope.navigateTo(label: String) {
        device.wait(Until.findObject(By.text(label)), WAIT_TIMEOUT_MS)?.click()
        device.waitForIdle()
    }
}
