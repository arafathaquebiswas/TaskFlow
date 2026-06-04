package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.*
import com.example.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            
            val useDarkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> systemDark
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            TaskFlowTheme(darkTheme = useDarkTheme) {
                TaskFlowApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TaskFlowApp(viewModel: TaskViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle physical back clicks gracefully
    BackHandler(enabled = currentScreen != Screen.DASHBOARD && currentScreen != Screen.ONBOARDING) {
        viewModel.goBack()
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn() with fadeOut()
        },
        modifier = Modifier.fillMaxSize(),
        label = "AppScreenNavigationFade"
    ) { screen ->
        when (screen) {
            Screen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
            Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
            Screen.ADD_TASK -> AddEditTaskScreen(viewModel = viewModel, isEditMode = false)
            Screen.EDIT_TASK -> AddEditTaskScreen(viewModel = viewModel, isEditMode = true)
            Screen.TASK_DETAIL -> TaskDetailScreen(viewModel = viewModel)
            Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
        }
    }
}
