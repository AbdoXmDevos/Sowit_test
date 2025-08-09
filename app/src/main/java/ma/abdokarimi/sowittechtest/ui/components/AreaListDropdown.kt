package ma.abdokarimi.sowittechtest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ma.abdokarimi.sowittechtest.entity.Area

// Dropdown menu for selecting and managing areas
@Composable
fun AreaListDropdown(
    areas: List<Area>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAreaSelect: (Area) -> Unit,
    onAreaDelete: (Area) -> Unit,
    getAreaPointsCount: (Area) -> Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = { onExpandedChange(true) }
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Voir les zones"
            )
        }

        // Dropdown menu for areas
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            if (areas.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Aucune zone sauvegardée") },
                    onClick = { },
                    enabled = false
                )
            } else {
                areas.forEach { area ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = area.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${getAreaPointsCount(area)} points",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onAreaDelete(area)
                                        onExpandedChange(false)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer ${area.name}",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onAreaSelect(area)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}
