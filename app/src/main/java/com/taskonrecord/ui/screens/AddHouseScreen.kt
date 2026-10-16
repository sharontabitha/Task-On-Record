package com.taskonrecord.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskonrecord.data.model.HouseType
import com.taskonrecord.ui.components.PixelCharacter
import com.taskonrecord.data.model.ResidentType
import com.taskonrecord.data.model.TaskStatus
import com.taskonrecord.ui.theme.*
import com.taskonrecord.viewmodel.VillageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHouseScreen(
    onBack: () -> Unit,
    onAdded: () -> Unit,
    viewModel: VillageViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<HouseType?>(null) }

    Scaffold(
        containerColor = DarkNight,
        topBar = {
            TopAppBar(
                title = {
                    Text("🏗️ Build New House", color = StarWhite, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = StarWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SkyBlueDark.copy(alpha = 0.3f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Choose what kind of house\nto build in your village:",
                style = MaterialTheme.typography.headlineMedium,
                color = StarWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // House type selection
            HouseTypeOption(
                type = HouseType.COLLEGE,
                title = "🏫 College / Study",
                subtitle = "For study tasks & assignments",
                residents = "Residents: Pig, Dog, Cat, Mouse",
                previewResidents = listOf(ResidentType.PIG, ResidentType.DOG, ResidentType.CAT, ResidentType.MOUSE),
                isSelected = selectedType == HouseType.COLLEGE,
                color = CollegeBlue,
                bgColor = androidx.compose.ui.graphics.Color(0xFF0D1B3E),
                onClick = { selectedType = HouseType.COLLEGE }
            )

            HouseTypeOption(
                type = HouseType.HOME,
                title = "🏠 Home Chores",
                subtitle = "For household tasks",
                residents = "Residents: Boy, Girl, Baby, Granny",
                previewResidents = listOf(ResidentType.BOY, ResidentType.GIRL, ResidentType.BABY, ResidentType.GRANNY),
                isSelected = selectedType == HouseType.HOME,
                color = HomeOrange,
                bgColor = androidx.compose.ui.graphics.Color(0xFF2D1206),
                onClick = { selectedType = HouseType.HOME }
            )

            HouseTypeOption(
                type = HouseType.GARDEN,
                title = "🌿 Garden",
                subtitle = "For personal goals & hobbies",
                residents = "Residents: Flowers & Crops",
                previewResidents = listOf(ResidentType.SUNFLOWER, ResidentType.ROSE, ResidentType.CARROT),
                isSelected = selectedType == HouseType.GARDEN,
                color = GardenGreen,
                bgColor = androidx.compose.ui.graphics.Color(0xFF0A2010),
                onClick = { selectedType = HouseType.GARDEN }
            )

            // House name input
            AnimatedVisibility(visible = selectedType != null) {
                val themeColor = when (selectedType) {
                    HouseType.COLLEGE -> CollegeBlue
                    HouseType.HOME -> HomeOrange
                    HouseType.GARDEN -> GardenGreen
                    null -> SkyBlueDark
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("House Name", color = PixelGray) },
                    placeholder = { Text("e.g. My College, Biology Class...", color = PixelGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite,
                        cursorColor = themeColor,
                        focusedLabelColor = themeColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Build button
            val canBuild = selectedType != null && name.isNotBlank()
            Button(
                onClick = {
                    viewModel.addCategory(
                        name = name.trim(),
                        type = selectedType!!
                    )
                    onAdded()
                },
                enabled = canBuild,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyBlueDark,
                    disabledContainerColor = CardBorder
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "🏗️ Build House!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HouseTypeOption(
    type: HouseType,
    title: String,
    subtitle: String,
    residents: String,
    previewResidents: List<ResidentType>,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else CardBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) bgColor else CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Preview characters
            Row {
                previewResidents.take(3).forEach { resident ->
                    PixelCharacter(
                        residentType = resident,
                        status = TaskStatus.PENDING,
                        size = 32.dp
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) color else StarWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelGray
                )
                Text(
                    text = residents,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) color.copy(alpha = 0.7f) else PixelGray.copy(alpha = 0.6f)
                )
            }

            if (isSelected) {
                Text("✅", fontSize = 22.sp)
            }
        }
    }
}
