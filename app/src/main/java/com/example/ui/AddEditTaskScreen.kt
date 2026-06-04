package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.data.TaskPriority
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GrayTextLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val editingTask by viewModel.selectedTask.collectAsState()

    // Form inputs state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.LOW) }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }

    var titleError by remember { mutableStateOf<String?>(null) }

    // Constants
    val maxTitleChars = 50
    val maxDescChars = 250

    // Load initial values if in edit mode
    LaunchedEffect(isEditMode, editingTask) {
        if (isEditMode && editingTask != null) {
            val task = editingTask!!
            title = task.title
            description = task.description
            priority = task.priority
            dueDate = task.dueDate
        } else if (!isEditMode) {
            title = ""
            description = ""
            priority = TaskPriority.LOW
            dueDate = System.currentTimeMillis()
        }
    }

    // Date formatting helper
    val dateFormatter = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    // Show native date picker function
    fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = dueDate }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val resultCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }
                dueDate = resultCal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("add_edit_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Task" else "Create Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier.testTag("add_task_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task Title Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Title",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${title.length}/$maxTitleChars",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (title.length > maxTitleChars) ErrorRed else GrayTextLight
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= maxTitleChars) {
                            title = it
                            titleError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    placeholder = { Text("What needs to be done?") },
                    isError = titleError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = GrayTextLight.copy(alpha = 0.4f)
                    )
                )

                if (titleError != null) {
                    Text(
                        text = titleError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Task Description Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Description Preview (Optional)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${description.length}/$maxDescChars",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (description.length > maxDescChars) ErrorRed else GrayTextLight
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        if (it.length <= maxDescChars) {
                            description = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("task_desc_input"),
                    placeholder = { Text("Add any details or notes here...") },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = GrayTextLight.copy(alpha = 0.4f)
                    )
                )
            }

            // Priority Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { prio ->
                        val isSelected = priority == prio
                        val baseColor = when (prio) {
                            TaskPriority.LOW -> SuccessGreen
                            TaskPriority.MEDIUM -> WarningOrange
                            TaskPriority.HIGH -> ErrorRed
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) baseColor.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) baseColor else GrayTextLight.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    priority = prio
                                    focusManager.clearFocus()
                                }
                                .testTag("priority_selection_${prio.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(baseColor)
                                )
                                Text(
                                    text = when (prio) {
                                        TaskPriority.LOW -> "Low"
                                        TaskPriority.MEDIUM -> "Medium"
                                        TaskPriority.HIGH -> "High"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) baseColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Due Date Picker & Presets
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Schedule/Due Date",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Presets row to accelerate task planning in < 5 seconds!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        "Today" to 0,
                        "Tomorrow" to 1,
                        "In 3 Days" to 3,
                        "Next Week" to 7
                    )

                    presets.forEach { (label, daysOffset) ->
                        val cal = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, daysOffset)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                        }
                        val presetTime = cal.timeInMillis

                        // Check if current selection matches preset day
                        val isPresetSelected = run {
                            val selDay = Calendar.getInstance().apply { timeInMillis = dueDate }
                            selDay.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
                                    selDay.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isPresetSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isPresetSelected) MaterialTheme.colorScheme.primary else GrayTextLight.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    dueDate = presetTime
                                    focusManager.clearFocus()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isPresetSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Custom Date Trigger Input Option
                OutlinedCard(
                    onClick = {
                        focusManager.clearFocus()
                        showDatePicker()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_date_trigger"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Due On",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GrayTextLight
                                )
                                Text(
                                    text = dateFormatter.format(Date(dueDate)),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Trigger Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = "Task title cannot be blank"
                        return@Button
                    }

                    if (isEditMode && editingTask != null) {
                        viewModel.updateTask(
                            id = editingTask!!.id,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority,
                            isCompleted = editingTask!!.isCompleted
                        )
                    } else {
                        viewModel.addTask(
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority
                        )
                    }
                    viewModel.goBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("task_save_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Create Task",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
