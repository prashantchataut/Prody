package com.prody.prashant.ui.main

import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is Loading initially`() = runTest {
        val preferencesManager = FakePreferencesManager(flowOf(false), flowOf("system"))
        viewModel = MainViewModel(preferencesManager)
        assertEquals(MainActivityUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success when onboarding is not completed`() = runTest {
        val preferencesManager = FakePreferencesManager(flowOf(false), flowOf("system"))
        viewModel = MainViewModel(preferencesManager)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(
            MainActivityUiState.Success(Screen.Onboarding.route, ThemeMode.SYSTEM),
            uiState
        )
    }

    @Test
    fun `uiState is Success when onboarding is completed`() = runTest {
        val preferencesManager = FakePreferencesManager(flowOf(true), flowOf("dark"))
        viewModel = MainViewModel(preferencesManager)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(
            MainActivityUiState.Success(Screen.Home.route, ThemeMode.DARK),
            uiState
        )
    }

    @Test
    fun `uiState defaults to onboarding when preferences throw IOException`() = runTest {
        val preferencesManager = FakePreferencesManager(
            flow { throw IOException() },
            flowOf("system")
        )
        viewModel = MainViewModel(preferencesManager)
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(
            MainActivityUiState.Success(Screen.Onboarding.route, ThemeMode.SYSTEM),
            uiState
        )
    }

    // A fake implementation of PreferencesManager for testing purposes.
    class FakePreferencesManager(
        private val onboardingCompletedFlow: kotlinx.coroutines.flow.Flow<Boolean>,
        private val themeModeFlow: kotlinx.coroutines.flow.Flow<String>
    ) : PreferencesManager(null) {
        override val onboardingCompleted = onboardingCompletedFlow
        override val themeMode = themeModeFlow
        override suspend fun setOnboardingCompleted(completed: Boolean) {}
        override suspend fun setThemeMode(themeMode: String) {}
        override suspend fun clear() {}
    }
}
