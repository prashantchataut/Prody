package com.prody.prashant.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkRule
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColdStartMacrobenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStart_beforeAfterIntegrityWorkScheduling() {
        benchmarkRule.measureRepeated(
            packageName = "com.prody.prashant",
            metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
            iterations = 10,
            startupMode = StartupMode.COLD,
            compilationMode = CompilationMode.Partial(),
            setupBlock = {
                pressHome()
            }
        ) {
            startActivityAndWait()
        }
    }
}
