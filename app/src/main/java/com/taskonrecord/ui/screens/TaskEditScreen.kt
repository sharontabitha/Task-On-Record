package com.taskonrecord.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskonrecord.data.model.*
import com.taskonrecord.ui.components.PixelCharacter
import com.taskonrecord.ui.components.formatDate
import com.taskonrecord.ui.theme.*
import com.taskonrecord.viewmodel.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val category by viewModel.category.collectAsStateWithLifecycle()
    val existingTask by viewModel.existingTask.collectAsStateWithLifecycle()

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedResident by remember { mutableStateOf<ResidentType?>(null) }
    var dueDateMillis by remember { mutableStateOf<Long?>(null) }
    var reminderMillis by remember { mutableStateOf<Long?>(null) }

    // Populate from existing task
    LaunchedEffect(existingTask) {
        existingTask?.let { task ->
            title = task.title
            description = task.description
            priority = task.priority
            selectedResident = task.residentType
            dueDateMillis = task.dueDateMillis
            reminderMillis = task.reminderMillis
        }
    }

    val availableResidents = remember(category?.type) {
        when (category?.type) {
            HouseType.COLLEGE -> listOf(
                ResidentType.PIG, ResidentType.DOG, ResidentType.CAT, ResidentType.MOUSE
            )
            HouseType.HOME -> listOf(
                ResidentType.BOY, ResidentType.GIRL, ResidentType.BABY, ResidentType.GRANNY
            )
            HouseType.GARDEN -> listOf(
                ResidentType.SUNFLOWER, ResidentType.ROSE, ResidentType.TULIP,
                ResidentType.WHEAT, ResidentType.CARROT, ResidentType.CORN
            )
            null -> emptyList()
        }
    }

    // Set default resident if only one category type
    LaunchedEffect(availableResidents) {
        if (selectedResident == null && availableResidents.isNotEmpty()) {
            selectedResident = availableResidents.first()
        }
    }

    val houseType = category?.type ?: HouseType.HOME
    val themeColor = when (houseType) {
        HouseType.COLLEGE -> CollegeBlue
        HouseType.HOME -> HomeOrange
        HouseType.GARDEN -> GardenGreen
    }

    Scaffold(
        containerColor = DarkNight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingTask != null) "Edit Resident" else "New Resident",
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = StarWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor.copy(alpha = 0.2f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Character preview
            selectedResident?.let { resident ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(12.dp))
                        .border(2.dp, themeColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelCharacter(
                            residentType = resident,
                            status = TaskStatus.PENDING,
                            size = 80.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resident.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            color = themeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Choose resident
            Section(title = "👤 Choose Resident", themeColor = themeColor) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(availableResidents) { resident ->
                        val isSelected = selectedResident == resident
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedResident = resident }
                                .background(
                                    if (isSelected) themeColor.copy(alpha = 0.2f) else CardBackground,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isSelected) themeColor else CardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            PixelCharacter(
                                residentType = resident,
                                status = TaskStatus.PENDING,
                                size = 48.dp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = resident.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) themeColor else PixelGray
                            )
                        }
                    }
                }
            }

            // Task name
            Section(title = "📝 Task Name", themeColor = themeColor) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("What needs to be done?", color = PixelGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite,
                        cursorColor = themeColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            // Description
            Section(title = "📋 Description (optional)", themeColor = themeColor) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Add details...", color = PixelGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite,
                        cursorColor = themeColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4
                )
            }

            // Priority
            Section(title = "⚡ Priority", themeColor = themeColor) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.values().forEach { p ->
                        val (color, emoji) = when (p) {
                            Priority.LOW -> PriorityLow to "🟢"
                            Priority.MEDIUM -> PriorityMedium to "🟡"
                            Priority.HIGH -> PriorityHigh to "🔴"
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = {
                                Text("$emoji ${p.name.lowercase().replaceFirstChar { it.uppercase() }}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.25f),
                                selectedLabelColor = color
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Due date
            Section(title = "📅 Due Date", themeColor = themeColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showDateTimePicker(context, dueDateMillis) { millis ->
                                dueDateMillis = millis
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColor.copy(alpha = 0.2f),
                            contentColor = themeColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(dueDateMillis?.let { formatDate(it) } ?: "Set Due Date")
                    }
                    if (dueDateMillis != null) {
                        IconButton(
                            onClick = { dueDateMillis = null },
                            modifier = Modifier
                                .background(
                                    StatusOverdue.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text("✕", color = StatusOverdue)
                        }
                    }
                }
            }

            // Reminder
            Section(title = "🔔 Reminder", themeColor = themeColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showDateTimePicker(context, reminderMillis) { millis ->
                                reminderMillis = millis
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColor.copy(alpha = 0.2f),
                            contentColor = themeColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Notifications, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(reminderMillis?.let { "Remind: ${formatDate(it)}" } ?: "Set Reminder")
                    }
                    if (reminderMillis != null) {
                        IconButton(
                            onClick = { reminderMillis = null },
                            modifier = Modifier
                                .background(
                                    StatusOverdue.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text("✕", color = StatusOverdue)
                        }
                    }
                }
            }

            // Save button
            val canSave = title.isNotBlank() && selectedResident != null
            Button(
                onClick = {
                    viewModel.saveTask(
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority,
                        residentType = selectedResident!!,
                        dueDateMillis = dueDateMillis,
                        reminderMillis = reminderMillis,
                        onSaved = onSaved
                    )
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    disabledContainerColor = CardBorder
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (existingTask != null) "💾 Update Resident" else "🏠 Move In Resident",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Section(
    title: String,
    themeColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = themeColor,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

private fun showDateTimePicker(
    context: Context,
    initialMillis: Long?,
    onSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()
    initialMillis?.let { calendar.timeInMillis = it }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onSelected(selectedCal.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
