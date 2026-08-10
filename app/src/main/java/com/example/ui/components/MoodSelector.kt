package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MoodCalm
import com.example.ui.theme.MoodEnergetic
import com.example.ui.theme.MoodFocused
import com.example.ui.theme.MoodGrateful
import com.example.ui.theme.MoodRelaxed

data class MoodOption(
    val rating: Int,
    val name: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val defaultMoodOptions = listOf(
    MoodOption(1, "Stressed", Color(0xFFEF4444), Icons.Default.SentimentDissatisfied),
    MoodOption(2, "Relaxed", MoodRelaxed, Icons.Default.SentimentNeutral),
    MoodOption(3, "Calm", MoodCalm, Icons.Default.SentimentSatisfied),
    MoodOption(4, "Focused", MoodFocused, Icons.Default.SentimentVerySatisfied),
    MoodOption(5, "Energetic", MoodEnergetic, Icons.Default.WbSunny)
)

@Composable
fun MoodSelector(
    selectedRating: Int,
    onMoodSelected: (MoodOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        defaultMoodOptions.forEach { mood ->
            val isSelected = mood.rating == selectedRating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onMoodSelected(mood) }
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) mood.color else mood.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mood.icon,
                        contentDescription = mood.name,
                        tint = if (isSelected) Color.White else mood.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = mood.name,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
