package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskPriority
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GrayTextLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val task by viewModel.selectedTask.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Details",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier.testTag("detail_back_button")
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
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Task not found.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            val validTask = task!!
            val dateStr = dateFormatter.format(Date(validTask.dueDate))
            val creationStr = dateFormatter.format(Date(validTask.createdAt))
            val creationTimeStr = timeFormatter.format(Date(validTask.createdAt))

            val prioColor = when (validTask.priority) {
                TaskPriority.LOW -> SuccessGreen
                TaskPriority.MEDIUM -> WarningOrange
                TaskPriority.HIGH -> ErrorRed
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Info Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Text(
                            text = validTask.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Priority Badge Node
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(prioColor)
                            )
                            Text(
                                text = "${validTask.priority.name} Priority",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = prioColor
                            )
                        }

                        Divider(color = GrayTextLight.copy(alpha = 0.15f))

                        // Description Content
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (validTask.description.isNotBlank()) validTask.description else "No description specified.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (validTask.description.isNotBlank()) MaterialTheme.colorScheme.onSurface else GrayTextLight,
                            lineHeight = 22.sp
                        )

                        Divider(color = GrayTextLight.copy(alpha = 0.15f))

                        // Schedule Timing Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Due Date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GrayTextLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GrayTextLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (validTask.isCompleted) SuccessGreen.copy(alpha = 0.15f)
                                            else WarningOrange.copy(alpha = 0.15f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (validTask.isCompleted) "Completed" else "Pending",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (validTask.isCompleted) SuccessGreen else WarningOrange
                                    )
                                }
                            }
                        }

                        Divider(color = GrayTextLight.copy(alpha = 0.15f))

                        // Log Creation Timestamps
                        Column {
                            Text(
                                text = "Created On",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayTextLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$creationStr at $creationTimeStr",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayTextLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom CTA Fast Action button stack
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mark Completed Button
                    Button(
                        onClick = { viewModel.toggleTaskCompletion(validTask) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("detail_toggle_complete"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (validTask.isCompleted) GrayTextLight.copy(alpha = 0.12f)
                            else SuccessGreen,
                            contentColor = if (validTask.isCompleted) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete check"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (validTask.isCompleted) "Mark As Pending" else "Mark As Completed",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Edit Button
                        OutlinedButton(
                            onClick = {
                                viewModel.navigateTo(Screen.EDIT_TASK)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("detail_edit_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Task", fontWeight = FontWeight.Bold)
                        }

                        // Delete Button
                        Button(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("detail_delete_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ErrorRed,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Task", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation prompt dialog
    if (showDeleteConfirm && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete task?", fontWeight = FontWeight.Bold) },
            text = { Text("This operation is permanent. Do you really want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteTaskById(task!!.id)
                        viewModel.goBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
