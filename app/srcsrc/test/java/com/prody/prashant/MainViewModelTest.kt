package com.prody.prashant

import app.cash.turbine.test
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.theme.ThemeMode
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Loading initially and then Success with correct data`() = runTest {
        // Given
        val onboardingCompleted = flowOf(true)
        val themeMode = flowOf("dark")
        every { preferencesManager.onboardingCompleted } returns onboardingCompleted
        every { preferencesManager.themeMode } returns themeMode

        // When
        viewModel = MainViewModel(preferencesManager)

        // Then
        viewModel.uiState.test {
            // Initial value should be Loading
            assertEquals(MainActivityUiState.Loading, awaitItem())

            // Then it should emit Success with the correct data
            val successState = awaitItem()
            assertEquals(true, (successState as MainActivityUiState.Success).isOnboardingCompleted)
            assertEquals(ThemeMode.DARK, successState.themeMode)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState maps theme mode strings correctly`() = runTest {
        // Test "light"
        every { preferencesManager.onboardingCompleted } returns flowOf(true)
        every { preferencesManager.themeMode } returns flowOf("light")
        viewModel = MainViewModel(preferencesManager)
        viewModel.uiState.test {
            assertEquals(MainActivityUiState.Loading, awaitItem())
            assertEquals(ThemeMode.LIGHT, (awaitItem() as MainActivityUiState.Success).themeMode)
            cancelAndIgnoreRemainingEvents()
        }

        // Test "system"
        every { preferencesManager.themeMode } returns flowOf("system")
        viewModel = MainViewModel(preferencesManager)
        viewModel.uiState.test {
            assertEquals(MainActivityUiState.Loading, awaitItem())
            assertEquals(ThemeMode.SYSTEM, (awaitItem() as MainActivityUiState.Success).themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
