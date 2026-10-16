package com.taskonrecord.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskonrecord.data.PreferencesHelper
import com.taskonrecord.data.model.*
import com.taskonrecord.ui.components.HapticHelper
import com.taskonrecord.ui.components.PixelCharacter
import com.taskonrecord.ui.components.formatDate
import com.taskonrecord.ui.theme.*
import com.taskonrecord.viewmodel.VillageViewModel
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillageScreen(
    onHouseClick: (Long) -> Unit,
    onAddHouse: () -> Unit,
    viewModel: VillageViewModel = viewModel()
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    // App theme & Gamification state
    var currentTheme by remember { mutableStateOf(PreferencesHelper.getTheme(context)) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val xp = remember { PreferencesHelper.getXp(context) }
    val gold = remember { PreferencesHelper.getGold(context) }
    val level = (xp / 50) + 1

    // Theme color mappings
    val (bgAppColor, cardBgColor, accentColor) = when (currentTheme) {
        "FOREST" -> Triple(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF34D399))
        "GOLD" -> Triple(Color(0xFF451A03), Color(0xFF291101), Color(0xFFFBBF24))
        "CYBER" -> Triple(Color(0xFF3B0764), Color(0xFF2E1065), Color(0xFFE879F9))
        else -> Triple(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFFFACC15)) // DARK_SLATE
    }

    // Dialog state for editing house name
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var newHouseName by remember { mutableStateOf("") }

    // Diary note input state & search
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var isPrivateNote by remember { mutableStateOf(false) }
    var diarySearchQuery by remember { mutableStateOf("") }

    val filteredNotes = remember(notes, diarySearchQuery) {
        notes.filter { note ->
            note.title.contains(diarySearchQuery, ignoreCase = true) ||
                    note.content.contains(diarySearchQuery, ignoreCase = true)
        }
    }

    // Password / Unlock state for private notes
    var unlockingNote by remember { mutableStateOf<Note?>(null) }
    var enteredPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var setupMode by remember { mutableStateOf(false) }
    var unlockedNoteIds = remember { mutableStateOf(setOf<Long>()) }

    Scaffold(
        containerColor = bgAppColor,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP BANNER (1/3 Screen Height, Bible Verses & Motivational Quotes)
            item {
                Box {
                    MotivationalBibleBanner(accentColor = accentColor)
                    // Theme picker icon button at top right of banner
                    IconButton(
                        onClick = {
                            HapticHelper.vibrate(context)
                            showThemeDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Palette, "Change Theme", tint = Color.White)
                    }
                }
            }

            // VILLAGE STATS BAR (Level, XP, Gold)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VILLAGE LEVEL $level",
                                style = MaterialTheme.typography.titleSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "XP: $xp / ${level * 50}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🪙 $gold Gold",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFACC15),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. HOMESTEAD CAROUSEL WITH BUILD BUTTON AT THE END
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOMESTEAD CAROUSEL",
                            style = MaterialTheme.typography.titleSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "${categories.size} Buildings",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories, key = { it.id }) { category ->
                            CarouselHouseCard(
                                category = category,
                                tasksFlow = viewModel.getTasks(category.id),
                                cardBg = cardBgColor,
                                accentColor = accentColor,
                                onClick = {
                                    HapticHelper.vibrate(context)
                                    onHouseClick(category.id)
                                },
                                onEdit = {
                                    HapticHelper.vibrate(context)
                                    editingCategory = category
                                    newHouseName = category.name
                                },
                                onDelete = {
                                    HapticHelper.vibrate(context)
                                    viewModel.deleteCategory(category)
                                }
                            )
                        }
                        item {
                            BuildBuildingCard(
                                accentColor = accentColor,
                                cardBg = cardBgColor,
                                onClick = {
                                    HapticHelper.vibrate(context)
                                    onAddHouse()
                                }
                            )
                        }
                    }
                }
            }

            // 3. DIARY & NOTES SECTION AT THE BOTTOM
            item {
                Spacer(modifier = Modifier.height(12.dp))
                DiaryNotesSection(
                    notes = filteredNotes,
                    noteTitle = noteTitle,
                    onTitleChange = { noteTitle = it },
                    noteContent = noteContent,
                    onContentChange = { noteContent = it },
                    isPrivate = isPrivateNote,
                    onPrivateChange = { isPrivateNote = it },
                    searchQuery = diarySearchQuery,
                    onSearchQueryChange = { diarySearchQuery = it },
                    cardBg = cardBgColor,
                    accentColor = accentColor,
                    onSaveNote = {
                        HapticHelper.vibrate(context, 50)
                        viewModel.addNote(noteTitle, noteContent, isPrivateNote)
                        noteTitle = ""
                        noteContent = ""
                        isPrivateNote = false
                    },
                    onDeleteNote = {
                        HapticHelper.vibrate(context)
                        viewModel.deleteNote(it)
                    },
                    unlockedIds = unlockedNoteIds.value,
                    onOpenPrivateNote = { note ->
                        HapticHelper.vibrate(context)
                        val savedPin = getSavedPin(context)
                        setupMode = savedPin.isNullOrBlank()
                        unlockingNote = note
                        enteredPassword = ""
                        passwordError = false
                    },
                    onShareNote = { note ->
                        HapticHelper.vibrate(context)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, note.title)
                            putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.content}\n\n[Created: ${formatDate(note.timestamp)}]")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                )
            }
        }
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Retro Theme", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "DARK_SLATE" to "Default Dark Slate",
                        "FOREST" to "Forest Green",
                        "GOLD" to "Royal Gold",
                        "CYBER" to "Cyberpunk Neon"
                    ).forEach { (code, name) ->
                        Button(
                            onClick = {
                                HapticHelper.vibrate(context)
                                currentTheme = code
                                PreferencesHelper.setTheme(context, code)
                                showThemeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = if (currentTheme == code) accentColor else Color(0xFF334155))
                        ) {
                            Text(name, color = if (currentTheme == code) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = cardBgColor
        )
    }

    // Edit House Name Dialog
    editingCategory?.let { cat ->
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Edit Building Name", color = Color(0xFFF8FAFC)) },
            text = {
                OutlinedTextField(
                    value = newHouseName,
                    onValueChange = { newHouseName = it },
                    label = { Text("Building Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.vibrate(context)
                        if (newHouseName.isNotBlank()) {
                            viewModel.updateCategoryName(cat, newHouseName.trim())
                        }
                        editingCategory = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = cardBgColor
        )
    }

    // Password Unlock / Setup Dialog for Private Notes
    unlockingNote?.let { note ->
        var passwordVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { unlockingNote = null },
            title = { Text(if (setupMode) "Setup Diary PIN" else "Unlock Private Diary", color = Color(0xFFF8FAFC)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (setupMode) "Create a PIN to protect your private entries." else "Enter your PIN to view this private note.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    OutlinedTextField(
                        value = enteredPassword,
                        onValueChange = { enteredPassword = it; passwordError = false },
                        label = { Text("PIN / Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        isError = passwordError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    if (passwordError) {
                        Text("Incorrect PIN. Access denied.", color = Color(0xFFEF4444), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.vibrate(context)
                        if (setupMode) {
                            if (enteredPassword.isNotBlank()) {
                                savePin(context, enteredPassword.trim())
                                unlockedNoteIds.value = unlockedNoteIds.value + note.id
                                unlockingNote = null
                            } else {
                                passwordError = true
                            }
                        } else {
                            val savedPin = getSavedPin(context)
                            if (enteredPassword.trim() == savedPin) {
                                unlockedNoteIds.value = unlockedNoteIds.value + note.id
                                unlockingNote = null
                            } else {
                                passwordError = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(if (setupMode) "Set PIN & Open" else "Unlock", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { unlockingNote = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = cardBgColor
        )
    }
}

// Helpers for Diary PIN SharedPreferences
private const val PREF_DIARY = "diary_prefs"
private const val KEY_PIN = "diary_pin"

private fun getSavedPin(context: Context): String? {
    val prefs = context.getSharedPreferences(PREF_DIARY, Context.MODE_PRIVATE)
    return prefs.getString(KEY_PIN, null)
}

private fun savePin(context: Context, pin: String) {
    val prefs = context.getSharedPreferences(PREF_DIARY, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_PIN, pin).apply()
}

@Composable
private fun MotivationalBibleBanner(accentColor: Color) {
    val quotes = remember {
        listOf(
            "\"Whatever you do, work at it with all your heart, as working for the Lord...\" — Colossians 3:23",
            "\"Go to the ant, you sluggard; consider its ways and be wise!\" — Proverbs 6:6",
            "\"The plans of the diligent lead to profit as surely as haste leads to poverty.\" — Proverbs 21:5",
            "\"Whatever your hand finds to do, do it with all your might.\" — Ecclesiastes 9:10",
            "\"Let us not become weary in doing good, for at the proper time we will reap a harvest.\" — Galatians 6:9",
            "\"The hand of the diligent will rule, but laziness ends in forced labor.\" — Proverbs 12:24",
            "\"Action is the foundational key to all success.\"",
            "\"Do something today that your future self will thank you for.\"",
            "\"Procrastination is opportunity's assassin. Stay focused.\"",
            "\"Small progress each day adds up to big results.\""
        )
    }

    var clickOffset by remember { mutableStateOf(0) }
    var isGlittering by remember { mutableStateOf(false) }

    val quoteIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 3) + clickOffset).toInt() % quotes.size
    val currentQuote = quotes[if (quoteIndex < 0) quoteIndex + quotes.size else quoteIndex]

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val (timeLabel, gradientColors) = when (hour) {
        in 6..11 -> "Morning Devotion" to listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF1E3A8A))
        in 12..14 -> "Daylight Focus" to listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF1E293B))
        in 15..17 -> "Evening Strength" to listOf(Color(0xFFC2410C), Color(0xFF7E22CE), Color(0xFF0F172A))
        else -> "Night Vigil" to listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF090D16))
    }

    val scaleAnim by animateFloatAsState(
        targetValue = if (isGlittering) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { isGlittering = false },
        label = "glitter"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // 1/3 screen height banner
            .scale(scaleAnim)
            .background(Brush.verticalGradient(gradientColors), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .border(2.dp, Color(0xFF475569), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .clickable {
                clickOffset++
                isGlittering = true
            }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "DAILY WISDOM [$timeLabel]",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentQuote,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF8FAFC),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            if (isGlittering) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "✨ Sparkle & Renewed Focus ✨", fontSize = 12.sp, color = accentColor)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap anywhere to shuffle verse",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CarouselHouseCard(
    category: Category,
    tasksFlow: Flow<List<Task>>,
    cardBg: Color,
    accentColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tasks by tasksFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(150.dp)
            .clickable { onClick() }
            .border(2.dp, accentColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFF8FAFC),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.MoreVert, "Options", tint = Color(0xFF94A3B8))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Demolish") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tasks.take(3).forEach { task ->
                    PixelCharacter(
                        residentType = task.residentType,
                        status = task.status,
                        size = 32.dp
                    )
                }
                if (tasks.isEmpty()) {
                    Text("Empty ecosystem", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontSize = 10.sp)
                }
            }

            Text(
                text = "${tasks.size} residents",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor
            )
        }
    }
}

@Composable
private fun BuildBuildingCard(accentColor: Color, cardBg: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(150.dp)
            .clickable { onClick() }
            .border(2.dp, Color(0xFF475569), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Build",
                tint = accentColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Build House",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFF8FAFC),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DiaryNotesSection(
    notes: List<Note>,
    noteTitle: String,
    onTitleChange: (String) -> Unit,
    noteContent: String,
    onContentChange: (String) -> Unit,
    isPrivate: Boolean,
    onPrivateChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    cardBg: Color,
    accentColor: Color,
    onSaveNote: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    unlockedIds: Set<Long>,
    onOpenPrivateNote: (Note) -> Unit,
    onShareNote: (Note) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VILLAGE DIARY & NOTES",
                style = MaterialTheme.typography.titleSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "${notes.size} entries",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8)
            )
        }

        // Search Bar for Diary
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search notes...", color = Color(0xFF64748B)) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = accentColor) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color(0xFF475569),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(6.dp)
        )

        // Note Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = onTitleChange,
                    placeholder = { Text("Diary Title...", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                )

                OutlinedTextField(
                    value = noteContent,
                    onValueChange = onContentChange,
                    placeholder = { Text("Write your thoughts or progress journal...", color = Color(0xFF64748B)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Checkbox(
                            checked = isPrivate,
                            onCheckedChange = onPrivateChange,
                            colors = CheckboxDefaults.colors(checkedColor = accentColor)
                        )
                        Icon(
                            if (isPrivate) Icons.Default.Lock else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isPrivate) "Private (PIN Locked)" else "Public",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = onSaveNote,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Save Entry", color = Color(0xFFF8FAFC))
                    }
                }
            }
        }

        // Saved Notes List with Date Created and Privacy Lock
        if (notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            notes.forEach { note ->
                val isUnlocked = !note.isPrivate || unlockedIds.contains(note.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (note.isPrivate) {
                                    Icon(
                                        if (isUnlocked) Icons.Default.Visibility else Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = if (isUnlocked) Color(0xFF22C55E) else Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = formatDate(note.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                IconButton(onClick = { onShareNote(note) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Share, "Share Note", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteNote(note) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, "Delete Note", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Content with Smooth Animation & Privacy Lock Check
                        AnimatedVisibility(
                            visible = isUnlocked,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        if (!isUnlocked) {
                            OutlinedButton(
                                onClick = { onOpenPrivateNote(note) },
                                modifier = Modifier.align(Alignment.Start),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                                border = BorderStroke(1.dp, accentColor),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unlock Private Note", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
