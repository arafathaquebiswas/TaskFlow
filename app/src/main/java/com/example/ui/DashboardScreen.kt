package com.example.ui

import androidx.compose.animation.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.data.TaskPriority
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.filter.collectAsState()
    val activeSort by viewModel.sortBy.collectAsState()

    // Analytics counters gathered reactively
    val totalCount by viewModel.totalTasksCount.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()
    val pendingCount by viewModel.pendingTasksCount.collectAsState()
    val rate by viewModel.completionRate.collectAsState()

    val focusManager = LocalFocusManager.current
    var showSortSheet by remember { mutableStateOf(false) }

    // Floating action action trigger
    val onAddTaskClicked = {
        viewModel.selectTask(null)
        viewModel.navigateTo(Screen.ADD_TASK)
    }

    // Dynamic Greeting Calculations
    val greeting = remember {
        val cal = Calendar.getInstance()
        when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    // Date Format string
    val currentDateStr = remember {
        val formatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        formatter.format(Date())
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("dashboard_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClicked,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("dashboard_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Task",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        // Main Grid / Column Layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. High Density Personalized Header Panel (Matches Design HTML)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$greeting, Arafat",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayTextLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = currentDateStr,
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Dynamic high-fidelity Avatar Profile Button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    .clickable { viewModel.navigateTo(Screen.SETTINGS) }
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .testTag("dashboard_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AR",
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Curatorial quotes under header
                        Text(
                            text = "\"${viewModel.currentQuote}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = GrayTextLight.copy(alpha = 0.8f)
                        )
                    }
                }

                // 2 & 3. High Density Productivity Summary Dashboard
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isSystemInDarkTheme()) GrayBorderDark.copy(alpha = 0.5f) else GrayBorder.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Top progress details row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "OVERALL PROGRESS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = GrayTextLight
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$rate%",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // Interactive/dynamic progress bar visualizer (vertical bars representing stats)
                                Row(
                                    modifier = Modifier.height(44.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // 5 beautiful rounded indicators mimicking the chart activity
                                    val activeColor = MaterialTheme.colorScheme.primary
                                    val inactiveColor = if (isSystemInDarkTheme()) GrayBorderDark else Color(0xFFF1F5F9)
                                    
                                    // Height fractions based on task completion metric
                                    val heights = listOf(0.40f, 0.65f, 1.00f, 0.75f, 0.35f)
                                    heights.forEachIndexed { i, fraction ->
                                        val isHighlighted = i == 2 || i == 3 // Highlighted accents
                                        Box(
                                            modifier = Modifier
                                                .width(8.dp)
                                                .fillMaxHeight(fraction)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isHighlighted) activeColor else inactiveColor)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3-Column dynamic indicators summary grid (Matches Design HTML)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Total Count Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSystemInDarkTheme()) CardsDark.copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = totalCount.toString().padStart(2, '0'),
                                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "TOTAL",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = GrayTextLight
                                        )
                                    }
                                }

                                // Completed Count Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSystemInDarkTheme()) CardsDark.copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = completedCount.toString().padStart(2, '0'),
                                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                            color = SuccessGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "DONE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = GrayTextLight
                                        )
                                    }
                                }

                                // Pending Count Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSystemInDarkTheme()) CardsDark.copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = pendingCount.toString().padStart(2, '0'),
                                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                            color = WarningOrange
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "TO-DO",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = GrayTextLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Search input bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search goals...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.setSearchQuery("") },
                                        modifier = Modifier.testTag("search_clear")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_bar"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = GrayTextLight.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        // Sort filter BottomSheet trigger button
                        IconButton(
                            onClick = { showSortSheet = true },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("dashboard_sort_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort Tasks",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 5. Filter Selective Chips row
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Filter Schedule",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GrayTextLight
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val activeFilters = listOf(
                                TaskFilter.ALL to "All",
                                TaskFilter.PENDING to "Pending",
                                TaskFilter.COMPLETED to "Done",
                                TaskFilter.DUE_TODAY to "Today",
                                TaskFilter.HIGH_PRIORITY to "High"
                            )

                            activeFilters.forEach { (filter, label) ->
                                val isSelected = activeFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.setFilter(filter)
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("filter_chip_${label.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // 6. Header/Status label
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Task Workspace",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${tasks.size} Items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayTextLight
                        )
                    }
                }

                // 7. Active Task scrolling items feed
                if (tasks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            EmptyStateIllustration(Modifier.size(160.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No matching tasks found" else "You're all caught up!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Try adjusting your search criteria" else "Click the '+' button to schedule a new task",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrayTextLight
                            )
                        }
                    }
                } else {
                    items(tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                            onEditClick = {
                                viewModel.selectTask(task)
                                viewModel.navigateTo(Screen.EDIT_TASK)
                            },
                            onDeleteClick = { viewModel.deleteTask(task) },
                            onCardClick = {
                                viewModel.selectTask(task)
                                viewModel.navigateTo(Screen.TASK_DETAIL)
                            }
                        )
                    }
                }

                // Spacing block under list
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 8. CREATOR WATERMARK SIGN-OFF (Adheres strictly to watermarking mandates)
            Text(
                text = "AB IT",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 12.dp)
                    .alpha(0.15f) // 15% opacity strictly met
                    .testTag("creator_watermark")
            )
        }
    }

    // Sort Choices Bottom Sheet Dialog
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sort Task Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val sorts = listOf(
                    TaskSort.DATE_DESC to "Due Date (Latest First)",
                    TaskSort.DATE_ASC to "Due Date (Earliest First)",
                    TaskSort.PRIORITY_HIGH_FIRST to "Priority (High Alert)",
                    TaskSort.ALPHABETICAL to "Alphabetical (A - Z)"
                )

                sorts.forEach { (sort, label) ->
                    val isSelected = activeSort == sort
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setSort(sort)
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active Selection",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Stats Blocks Component Rendering
@Composable
fun AnalyticsMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayTextLight
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun AnalyticsSmallBlock(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = GrayTextLight,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

// Single List Card Item for Task rendering (High Density Theme)
@Composable
fun TaskItemCard(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = remember { SimpleDateFormat("MMMM dd", Locale.getDefault()) }
    val formattedDate = df.format(Date(task.dueDate))

    val prioColor = when (task.priority) {
        TaskPriority.LOW -> SuccessGreen
        TaskPriority.MEDIUM -> WarningOrange
        TaskPriority.HIGH -> ErrorRed
    }

    val prioText = when (task.priority) {
        TaskPriority.LOW -> "Low"
        TaskPriority.MEDIUM -> "Med"
        TaskPriority.HIGH -> "High"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .border(
                width = 1.dp,
                color = if (isSystemInDarkTheme()) GrayBorderDark.copy(alpha = 0.4f) else GrayBorder.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Forces the left priority bar to span full vertical height of internal row constraints
        ) {
            // Left priority vertical colored bar (Matches design HTML left high-density accentuation)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(prioColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Custom Checked Checkbox Box (Matches Design HTML precisely)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (task.isCompleted) SuccessGreen 
                            else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = if (task.isCompleted) SuccessGreen 
                                    else if (isSystemInDarkTheme()) GrayBorderDark 
                                    else GrayBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onToggleCompletion)
                        .testTag("task_checkbox_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Task Details block
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (task.isCompleted) GrayTextLight.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Priority status label/capsule chip (Matches style design text-cols)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(prioColor.copy(alpha = 0.10f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = prioText.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = prioColor
                            )
                        }
                    }

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = GrayTextLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Schedule detail Row (Matches clock-vector indicators)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Due Date",
                            tint = GrayTextLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Due $formattedDate",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = GrayTextLight
                        )
                    }
                }

                // Inline interactive modifications (Fast inline operational controls)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("task_edit_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit task",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("task_delete_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete task",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
