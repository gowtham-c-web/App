package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.components.ConfettiEffect
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.MoodJournalScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.AuraViewModel
import com.example.ui.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

enum class AuraTab(val label: String, val icon: ImageVector, val tag: String) {
    Dashboard("Today", Icons.Default.CheckCircle, "tab_dashboard"),
    Focus("Focus", Icons.Default.Timer, "tab_focus"),
    Analytics("Insights", Icons.Default.BarChart, "tab_analytics"),
    Mood("Mood", Icons.Default.Psychology, "tab_mood"),
    Settings("Settings", Icons.Default.Settings, "tab_settings")
}

@Composable
fun MainNavHost(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AuraTab.Dashboard) }
    var triggerCelebration by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val habits by viewModel.habits.collectAsState()
    val moodLogs by viewModel.moodLogs.collectAsState()
    val totalFocusMins by viewModel.totalFocusMinutes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.TriggerCelebration -> {
                    triggerCelebration = true
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AuraTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.label)
                        },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    AuraTab.Dashboard -> DashboardScreen(viewModel = viewModel, habits = habits)
                    AuraTab.Focus -> FocusScreen(viewModel = viewModel)
                    AuraTab.Analytics -> AnalyticsScreen(
                        viewModel = viewModel,
                        habits = habits,
                        totalFocusMinutes = totalFocusMins
                    )
                    AuraTab.Mood -> MoodJournalScreen(viewModel = viewModel, moodLogs = moodLogs)
                    AuraTab.Settings -> SettingsScreen(viewModel = viewModel)
                }
            }

            ConfettiEffect(
                trigger = triggerCelebration,
                onFinished = { triggerCelebration = false }
            )
        }
    }
}
