package com.example.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilter {
    ALL,
    COMPLETED,
    PENDING,
    HIGH_PRIORITY,
    DUE_TODAY
}

enum class TaskSort {
    DATE_DESC,
    DATE_ASC,
    PRIORITY_HIGH_FIRST,
    ALPHABETICAL
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class Screen {
    ONBOARDING,
    DASHBOARD,
    ADD_TASK,
    EDIT_TASK,
    TASK_DETAIL,
    SETTINGS
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val prefs: SharedPreferences = application.getSharedPreferences("taskflow_prefs", Context.MODE_PRIVATE)

    // Screen navigation state managed securely
    private val _currentScreen = MutableStateFlow<Screen>(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen history backstack to allow proper hardware/software back navigation
    private val screenHistory = mutableListOf<Screen>()

    // Selection variables for focus or editing
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    // Search and filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _sortBy = MutableStateFlow(TaskSort.DATE_DESC)
    val sortBy: StateFlow<TaskSort> = _sortBy.asStateFlow()

    // Preferences-based properties
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // Quotes and productivity parameters
    val productivityQuotes = listOf(
        "Focus on being productive instead of busy.",
        "Your mind is for having ideas, not holding them.",
        "The best way to predict your future is to create it.",
        "Action is the foundational key to all success.",
        "Simplicity is the soul of efficiency.",
        "Yesterday is gone. Tomorrow has not yet come.",
        "Do the hard jobs first. The easy jobs will take care of themselves."
    )
    
    val currentQuote: String

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TaskRepository(database.taskDao())

        // Load Preferences
        val themeOrdinal = prefs.getInt("theme_mode", AppThemeMode.SYSTEM.ordinal)
        _themeMode.value = AppThemeMode.values().getOrElse(themeOrdinal) { AppThemeMode.SYSTEM }

        val isCompleted = prefs.getBoolean("onboarding_completed", false)
        _onboardingCompleted.value = isCompleted
        if (!isCompleted) {
            _currentScreen.value = Screen.ONBOARDING
        }

        // Pick a pseudo-random quote based on day of week
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        currentQuote = productivityQuotes[day % productivityQuotes.size]
    }

    // All tasks from repository
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered and sorted tasks flow
    val tasks: StateFlow<List<Task>> = combine(
        allTasks,
        _searchQuery,
        _filter,
        _sortBy
    ) { rawTasks, query, f, sort ->
        var list = rawTasks

        // 1. Search Query filtering
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        // 2. Tab Filter
        list = when (f) {
            TaskFilter.ALL -> list
            TaskFilter.COMPLETED -> list.filter { it.isCompleted }
            TaskFilter.PENDING -> list.filter { !it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> list.filter { it.priority == TaskPriority.HIGH }
            TaskFilter.DUE_TODAY -> {
                val today = Calendar.getInstance()
                list.filter {
                    val taskCal = Calendar.getInstance().apply { timeInMillis = it.dueDate }
                    taskCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    taskCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                }
            }
        }

        // 3. Sorting
        when (sort) {
            TaskSort.DATE_DESC -> list.sortedByDescending { it.dueDate }
            TaskSort.DATE_ASC -> list.sortedBy { it.dueDate }
            TaskSort.PRIORITY_HIGH_FIRST -> list.sortedByDescending { it.priority.ordinal }
            TaskSort.ALPHABETICAL -> list.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Analytics state (directly processed from allTasks)
    val totalTasksCount = allTasks.map { it.size }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val completedTasksCount = allTasks.map { it.count { task -> task.isCompleted } }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val pendingTasksCount = allTasks.map { it.count { task -> !task.isCompleted } }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val completionRate = allTasks.map {
        if (it.isEmpty()) 0 else (it.count { task -> task.isCompleted } * 100 / it.size)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Navigation and screen management
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun goBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
            return true
        }
        return false
    }

    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }

    // Task Actions
    fun addTask(title: String, description: String, dueDate: Long, priority: TaskPriority) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim(),
                dueDate = dueDate,
                priority = priority,
                isCompleted = false
            )
            repository.insert(task)
        }
    }

    fun updateTask(id: Int, title: String, description: String, dueDate: Long, priority: TaskPriority, isCompleted: Boolean) {
        viewModelScope.launch {
            val task = Task(
                id = id,
                title = title.trim(),
                description = description.trim(),
                dueDate = dueDate,
                priority = priority,
                isCompleted = isCompleted
            )
            repository.update(task)
            // Synchronize selectedState representation
            if (_selectedTask.value?.id == id) {
                _selectedTask.value = task
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.update(updated)
            if (_selectedTask.value?.id == task.id) {
                _selectedTask.value = updated
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            if (_selectedTask.value?.id == task.id) {
                _selectedTask.value = null
            }
        }
    }

    fun deleteTaskById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_selectedTask.value?.id == id) {
                _selectedTask.value = null
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun setSort(sort: TaskSort) {
        _sortBy.value = sort
    }

    fun changeThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putInt("theme_mode", mode.ordinal).apply()
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        _currentScreen.value = Screen.DASHBOARD
    }

    fun resetOnboarding() {
        _onboardingCompleted.value = false
        prefs.edit().putBoolean("onboarding_completed", false).apply()
        _currentScreen.value = Screen.ONBOARDING
    }
}
