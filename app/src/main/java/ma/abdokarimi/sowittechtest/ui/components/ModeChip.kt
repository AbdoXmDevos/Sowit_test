package ma.abdokarimi.sowittechtest.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Chip component that displays creation mode status
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeChip(
    isCreationMode: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isCreationMode,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "Mode de création actif",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
