package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SaveStateEntity
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenAccentRed
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenGlassBorderStrong
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary
import com.example.ui.theme.ZenTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveStateDialog(
    isSaveMode: Boolean, // true for Save State, false for Load State
    existingStates: List<SaveStateEntity>,
    onSlotSelected: (Int) -> Unit,
    onDeleteSlot: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val title = if (isSaveMode) "Save State" else "Load State"
    val statesBySlot = existingStates.associateBy { it.slotIndex }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ZenGlassBackground)
                .border(1.dp, ZenGlassBorderStrong, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                        Text(
                            text = if (isSaveMode) "Select slot to overwrite or save" else "Select state to restore",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZenTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZenTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ZenGlassBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Slots 1 to 5
                for (slot in 1..5) {
                    val state = statesBySlot[slot]
                    val isSaved = state != null
                    val timeString = if (isSaved) formatRelativeTimestamp(state.timestamp) else "Empty"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSaved) ZenSurfaceElevated.copy(alpha = 0.6f) else Color.Transparent)
                            .clickable {
                                onSlotSelected(slot)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("save_slot_$slot"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSaved) ZenAccent.copy(alpha = 0.2f)
                                        else Color.White.copy(alpha = 0.06f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$slot",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSaved) ZenAccent else ZenTextTertiary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Slot $slot",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZenTextPrimary
                                )
                                Text(
                                    text = timeString,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSaved) ZenAccent else ZenTextTertiary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSaved && isSaveMode) {
                                IconButton(
                                    onClick = { onDeleteSlot(slot) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Slot $slot",
                                        tint = ZenAccentRed.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isSaveMode) Icons.Default.FileUpload else Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = if (isSaved || isSaveMode) ZenAccent else ZenTextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatRelativeTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneMinute = 60 * 1000L
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour

    return when {
        diff < oneMinute -> "Saved just now"
        diff < oneHour -> "Saved ${diff / oneMinute}m ago"
        diff < oneDay -> {
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            "Saved ${sdf.format(Date(timestamp))}"
        }
        diff < 2 * oneDay -> "Saved Yesterday"
        else -> {
            val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            "Saved ${sdf.format(Date(timestamp))}"
        }
    }
}
