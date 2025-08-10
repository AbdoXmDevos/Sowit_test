package ma.abdokarimi.sowittechtest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import ma.abdokarimi.sowittechtest.R
import ma.abdokarimi.sowittechtest.mvvm.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isPolygonMode by viewModel.isPolygonMode.collectAsState()
    val polygonPoints by viewModel.polygonPoints.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val selectedArea by viewModel.selectedArea.collectAsState()
    val showAreasList by viewModel.showAreasList.collectAsState()
    val showSaveDialog by viewModel.showSaveDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val areaToDelete by viewModel.areaToDelete.collectAsState()
    val areaName by viewModel.areaName.collectAsState()
    val showInstructionToast by viewModel.showInstructionToast.collectAsState()
    val hasShownInstructionBefore by viewModel.hasShownInstructionBefore.collectAsState()

    // Show instruction toast only the first time creation mode is entered
    LaunchedEffect(isPolygonMode) {
        if (isPolygonMode && !hasShownInstructionBefore) {
            viewModel.setShowInstructionToast(true)
            viewModel.setHasShownInstructionBefore(true)
        } else if (!isPolygonMode) {
            viewModel.setShowInstructionToast(false)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = {
                Icon(
                    painter = painterResource(id = R.drawable.sowit_icon),
                    contentDescription = "Logo Sowit",
                    modifier = Modifier.height(30.dp),
                    tint = Color.Unspecified // Prevents automatic tinting
                )
            },
            actions = {
                // Cancel button (only show when in polygon mode)
                if (isPolygonMode) {
                    IconButton(
                        onClick = {
                            viewModel.clearPolygonPoints()
                            viewModel.togglePolygonMode()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Annuler la création de zone"
                        )
                    }
                }

                // Save/Add button
                IconButton(
                    onClick = {
                        if (isPolygonMode) {
                            if (polygonPoints.size >= 3) {
                                viewModel.setShowSaveDialog(true)
                            }
                        } else {
                            viewModel.togglePolygonMode()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isPolygonMode) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isPolygonMode) "Sauvegarder la zone" else "Créer une zone"
                    )
                }

                // List button (only show when not in polygon mode)
                if (!isPolygonMode) {
                    AreaListDropdown(
                        areas = areas,
                        isExpanded = showAreasList,
                        onExpandedChange = { viewModel.setShowAreasList(it) },
                        onAreaSelect = { area ->
                            viewModel.selectArea(area)
                        },
                        onAreaDelete = { area ->
                            viewModel.setAreaToDelete(area)
                            viewModel.setShowDeleteDialog(true)
                        },
                        getAreaPointsCount = { area ->
                            viewModel.getAreaPolygonPoints(area).size
                        }
                    )
                }
            }
        )

        // Map Content with floating search bar
        MapWithPlaces(
            viewModel = viewModel,
            isPolygonMode = isPolygonMode,
            selectedArea = selectedArea,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            showInstructionToast = showInstructionToast,
            onDismissToast = { viewModel.setShowInstructionToast(false) }
        )
    }

    // Save area dialog
    SaveAreaDialog(
        isVisible = showSaveDialog,
        areaName = areaName,
        onAreaNameChange = { viewModel.setAreaName(it) },
        onSave = {
            if (areaName.isNotBlank()) {
                viewModel.saveArea(areaName)
                viewModel.setAreaName("")
                viewModel.setShowSaveDialog(false)
            }
        },
        onDismiss = {
            viewModel.setShowSaveDialog(false)
            viewModel.setAreaName("")
        }
    )

    // Delete confirmation dialog
    DeleteAreaDialog(
        isVisible = showDeleteDialog,
        areaToDelete = areaToDelete,
        onConfirmDelete = {
            areaToDelete?.let { area ->
                viewModel.deleteArea(area)
            }
            viewModel.setShowDeleteDialog(false)
            viewModel.setAreaToDelete(null)
        },
        onDismiss = {
            viewModel.setShowDeleteDialog(false)
            viewModel.setAreaToDelete(null)
        }
    )
}
