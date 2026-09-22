package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.GameEntity
import com.example.model.ConsoleSystem
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary
import com.example.ui.theme.ZenTextTertiary

@Composable
fun GameCard(
    game: GameEntity,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val system = ConsoleSystem.fromId(game.consoleSystem)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}"),
        shape = RoundedCornerShape(22.dp),
        onClick = onCardClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Game Banner / Artwork Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                system.accentColor.copy(alpha = 0.28f),
                                ZenSurfaceElevated,
                                ZenOnyx
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!game.coverArtUri.isNullOrBlank()) {
                    AsyncImage(
                        model = game.coverArtUri,
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Minimal stylized banner
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(system.accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideogameAsset,
                                contentDescription = null,
                                tint = system.accentColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = system.shortName,
                            style = MaterialTheme.typography.labelSmall,
                            color = system.accentColor.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Top system badge tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = system.shortName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Game Details & Play Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = ZenTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = system.fullName,
                            style = MaterialTheme.typography.bodySmall,
                            color = ZenTextSecondary
                        )
                        if (game.playTimeSeconds > 0) {
                            Text(
                                text = " • ${formatPlayTime(game.playTimeSeconds)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZenTextTertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Apple-style circular Play Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ZenAccent)
                        .clickable(onClick = onPlayClick)
                        .testTag("play_button_${game.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play ${game.title}",
                        tint = ZenOnyx,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

fun formatPlayTime(seconds: Long): String {
    val mins = seconds / 60
    val hours = mins / 60
    return when {
        hours > 0 -> "${hours}h ${mins % 60}m"
        mins > 0 -> "${mins}m"
        else -> "${seconds}s"
    }
}
