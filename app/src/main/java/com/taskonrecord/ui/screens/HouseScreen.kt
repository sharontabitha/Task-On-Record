package com.taskonrecord.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskonrecord.data.model.*
import com.taskonrecord.ui.components.HapticHelper
import com.taskonrecord.ui.components.PixelCharacter
import com.taskonrecord.ui.components.TaskResidentCard
import com.taskonrecord.ui.theme.*
import com.taskonrecord.viewmodel.HouseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseScreen(
    onBack: () -> Unit,
    onAddTask: (Long) -> Unit,
    onEditTask: (Long, Long) -> Unit,
    viewModel: HouseViewModel = viewModel()
) {
    val context = LocalContext.current
    val category by viewModel.category.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    // Search query & priority filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) }

    val filteredTasks = remember(tasks, searchQuery, selectedPriorityFilter) {
        tasks.filter { task ->
            val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)
            val matchesPriority = selectedPriorityFilter == null || task.priority == selectedPriorityFilter
            matchesSearch && matchesPriority
        }
    }

    val houseType = category?.type ?: HouseType.HOME
    val (themeColor, bgGradient, headerEmoji) = when (houseType) {
        HouseType.COLLEGE -> Triple(
            CollegeBlue,
            listOf(Color(0xFF0D1B3E), Color(0xFF1A2E5A), DarkNight),
            "🏫"
        )
        HouseType.HOME -> Triple(
            HomeOrange,
            listOf(Color(0xFF2D1206), Color(0xFF4A1A0A), DarkNight),
            "🏠"
        )
        HouseType.GARDEN -> Triple(
            GardenGreen,
            listOf(Color(0xFF0A2010), Color(0xFF143520), DarkNight),
            "🌿"
        )
    }

    Scaffold(
        containerColor = DarkNight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$headerEmoji ${category?.name ?: "House"}",
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        HapticHelper.vibrate(context)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = StarWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.verticalGradient(colors = bgGradient.take(2))
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    HapticHelper.vibrate(context)
                    viewModel.categoryId.let { onAddTask(it) }
                },
                containerColor = themeColor,
                contentColor = PixelWhite
            ) {
                Icon(Icons.Default.Add, "Add Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // House banner
            item {
                HouseBanner(
                    category = category,
                    tasks = tasks,
                    themeColor = themeColor,
                    bgGradient = bgGradient,
                    headerEmoji = headerEmoji
                )
            }

            // Search bar & Priority filters
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search residents or tasks...", color = PixelGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = themeColor) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = StarWhite,
                            unfocusedTextColor = StarWhite
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Priority filter chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedPriorityFilter == null,
                                onClick = {
                                    HapticHelper.vibrate(context)
                                    selectedPriorityFilter = null
                                },
                                label = { Text("All") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeColor.copy(alpha = 0.3f),
                                    selectedLabelColor = themeColor
                                )
                            )
                        }
                        items(Priority.entries) { priority ->
                            FilterChip(
                                selected = selectedPriorityFilter == priority,
                                onClick = {
                                    HapticHelper.vibrate(context)
                                    selectedPriorityFilter = priority
                                },
                                label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeColor.copy(alpha = 0.3f),
                                    selectedLabelColor = themeColor
                                )
                            )
                        }
                    }
                }
            }

            // Section title
            item {
                Text(
                    text = "👥 RESIDENTS (${filteredTasks.size})",
                    style = MaterialTheme.typography.headlineMedium,
                    color = themeColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (filteredTasks.isEmpty()) {
                item {
                    EmptyHouseContent(
                        houseType = houseType,
                        themeColor = themeColor,
                        onAddTask = { onAddTask(viewModel.categoryId) }
                    )
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskResidentCard(
                        task = task,
                        onClick = {
                            HapticHelper.vibrate(context)
                            onEditTask(viewModel.categoryId, task.id)
                        },
                        onMarkDone = {
                            HapticHelper.vibrate(context, 60)
                            viewModel.markTaskDone(task)
                        },
                        onDelete = {
                            HapticHelper.vibrate(context)
                            viewModel.deleteTask(task)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HouseBanner(
    category: Category?,
    tasks: List<com.taskonrecord.data.model.Task>,
    themeColor: Color,
    bgGradient: List<Color>,
    headerEmoji: String
) {
    val doneCount = tasks.count { it.status == TaskStatus.DONE }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Brush.verticalGradient(bgGradient))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tasks.take(6).forEach { task ->
                PixelCharacter(
                    residentType = task.residentType,
                    status = task.status,
                    size = 40.dp,
                    modifier = Modifier.padding(4.dp)
                )
            }
            if (tasks.size > 6) {
                Text(
                    text = "+${tasks.size - 6}",
                    color = themeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (tasks.isEmpty()) {
                Text(headerEmoji, fontSize = 64.sp)
            }
        }

        if (totalCount > 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Progress: $doneCount / $totalCount done",
                    style = MaterialTheme.typography.labelSmall,
                    color = themeColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = themeColor,
                    trackColor = themeColor.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun EmptyHouseContent(
    houseType: HouseType,
    themeColor: Color,
    onAddTask: () -> Unit
) {
    val (emoji, hint) = when (houseType) {
        HouseType.COLLEGE -> "🐾" to "Add study tasks and animals will move in!"
        HouseType.HOME -> "👪" to "Add chore tasks and your family will appear!"
        HouseType.GARDEN -> "🌱" to "Plant some tasks and watch them grow!"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(emoji, fontSize = 64.sp)
        Text(
            text = "No matching residents!",
            style = MaterialTheme.typography.headlineMedium,
            color = StarWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyLarge,
            color = PixelGray,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAddTask,
            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
        ) {
            Text("➕ Add Resident")
        }
    }
}
