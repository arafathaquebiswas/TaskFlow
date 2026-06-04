package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GrayTextLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val scrollState = rememberScrollState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("settings_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Configuration Section
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "App Theme Mode",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.values().forEach { mode ->
                            val isSelected = themeMode == mode
                            val label = when (mode) {
                                AppThemeMode.SYSTEM -> "System"
                                AppThemeMode.LIGHT -> "Light"
                                AppThemeMode.DARK -> "Dark"
                            }
                            val icon = when (mode) {
                                AppThemeMode.SYSTEM -> Icons.Default.SettingsSuggest
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.changeThemeMode(mode) },
                                label = { Text(label) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_chip_${mode.name.lowercase()}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // About, Privacy, Terms, Reset Section
            Text(
                text = "Information & Support",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "About TaskFlow",
                        subtitle = "Learn more about the application",
                        onClick = { showAboutDialog = true }
                    )
                    Divider(color = GrayTextLight.copy(alpha = 0.15f))
                    SettingsRowItem(
                        icon = Icons.Default.Feedback,
                        title = "Send Feedback",
                        subtitle = "Help us improve your experience",
                        onClick = { showFeedbackDialog = true }
                    )
                    Divider(color = GrayTextLight.copy(alpha = 0.15f))
                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        title = "Privacy Policy",
                        subtitle = "Our commitment to your privacy",
                        onClick = { showPrivacyDialog = true }
                    )
                    Divider(color = GrayTextLight.copy(alpha = 0.15f))
                    SettingsRowItem(
                        icon = Icons.Default.Description,
                        title = "Terms & Conditions",
                        subtitle = "Legal agreements and parameters",
                        onClick = { showTermsDialog = true }
                    )
                    Divider(color = GrayTextLight.copy(alpha = 0.15f))
                    SettingsRowItem(
                        icon = Icons.Default.Refresh,
                        title = "Show Onboarding Again",
                        subtitle = "Play the introduction walkthrough again",
                        onClick = { viewModel.resetOnboarding() }
                    )
                }
            }

            // App Version representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TaskFlow Version 1.0.0 (Premium)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayTextLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Designed & Developed by AB IT",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // --- Dialog Popups ---

    // About App Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About TaskFlow", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "TaskFlow is a premium, performance-oriented task manager created to structure " +
                    "goals and increase daily production efficiency. Developed and maintained under the brand AB IT, " +
                    "featuring local client Room DB storage, 15% opacity creator watermarks, dynamic Material Design 3 templates, and high-fidelity graphics."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        var feedbackText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("Share Feedback", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("We'd love to hear your thoughts or support queries:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("How can we make TaskFlow better?") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFeedbackDialog = false },
                    enabled = feedbackText.isNotBlank()
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "TaskFlow operates 100% locally and values data security. All tasks, priorities, " +
                        "and progress statistics are securely stored on your local device repository using Room client " +
                        "DB. TaskFlow does not upload user data, telemetry, or analytics logs to third-party cloud engines."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "GitHub & Source Hosting Privacy Compliance:\n" +
                        "TaskFlow is open-source and conforms entirely with the GitHub Privacy Statement when cloned, hosted, " +
                        "or synced through GitHub services. It does not collect any user account credentials, repositories, " +
                        "or telemetry. Any interaction with the GitHub platform is strictly governed by GitHub's own privacy " +
                        "statement (available at https://docs.github.com/site-policy/privacy-policies/github-privacy-statement)."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Accept")
                }
            }
        )
    }

    // Terms and Conditions Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "By using TaskFlow App, you agree that your tasks and schedules are stored and managed " +
                        "strictly on your local device. The application is provided 'as is', and developers " +
                        "are not liable for data loss or device corruption arising from operations."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayTextLight
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = GrayTextLight.copy(alpha = 0.6f)
        )
    }
}
