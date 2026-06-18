package com.example.sensordashboard.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensordashboard.data.SensorCategory
import com.example.sensordashboard.data.SensorRepository
import com.example.sensordashboard.theme.DarkBackground
import com.example.sensordashboard.theme.DarkSurface
import com.example.sensordashboard.theme.GlassBorder
import com.example.sensordashboard.theme.NeonCyan
import com.example.sensordashboard.theme.TextSecondary
import com.example.sensordashboard.ui.components.SensorCard

@Composable
fun DashboardScreen(
    onSensorClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { SensorRepository(context) }
    val vm: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(repository)
    )
    val state by vm.uiState.collectAsStateWithLifecycle()
    val filtered = vm.filteredSensors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            DashboardHeader(
                totalCount = state.sensors.size,
                liveCount = state.readings.size
            )

            Spacer(Modifier.height(12.dp))

            // Search bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { vm.setSearchQuery(it) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Category filter chips
            CategoryFilterRow(
                selected = state.selectedCategory,
                onSelect = { vm.selectCategory(it) }
            )

            Spacer(Modifier.height(12.dp))

            // Sensor stats row
            SensorStatsRow(
                categories = SensorCategory.entries,
                sensors = state.sensors,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Sensor grid
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            } else {
                AnimatedVisibility(
                    visible = filtered.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, bottom = 32.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filtered, key = { it.sensorType }) { info ->
                            SensorCard(
                                info = info,
                                reading = state.readings[info.sensorType],
                                onClick = { onSensorClick(info.sensorType) },
                            )
                        }
                    }
                }

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No sensors found",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(totalCount: Int, liveCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkSurface, DarkBackground)
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NeonCyan, shape = RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "SENSOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "$totalCount sensors · $liveCount active",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            cursorBrush = SolidColor(NeonCyan),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        "Search sensors...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                inner()
            }
        )
    }
}

@Composable
private fun CategoryFilterRow(
    selected: SensorCategory?,
    onSelect: (SensorCategory?) -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        CategoryChip(
            label = "All",
            color = NeonCyan,
            isSelected = selected == null,
            onClick = { onSelect(null) }
        )
        SensorCategory.entries.forEach { cat ->
            CategoryChip(
                label = cat.displayName,
                color = cat.color,
                isSelected = selected == cat,
                onClick = { onSelect(cat) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else DarkSurface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) color else GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) color else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SensorStatsRow(
    categories: List<SensorCategory>,
    sensors: List<com.example.sensordashboard.data.SensorInfo>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val count = sensors.count { it.category == cat }
            StatChip(
                label = cat.displayName,
                count = count,
                color = cat.color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f),
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )
    }
}
