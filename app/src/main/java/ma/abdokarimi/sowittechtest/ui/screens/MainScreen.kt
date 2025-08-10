package ma.abdokarimi.sowittechtest.ui.screens

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
import ma.abdokarimi.sowittechtest.ui.viewmodels.MainViewModel
import ma.abdokarimi.sowittechtest.ui.components.AreaListDropdown
import ma.abdokarimi.sowittechtest.ui.components.DeleteAreaDialog
import ma.abdokarimi.sowittechtest.ui.components.InstructionToast
import ma.abdokarimi.sowittechtest.ui.components.ModeChip
import ma.abdokarimi.sowittechtest.ui.components.SaveAreaDialog
import ma.abdokarimi.sowittechtest.ui.components.SearchTopBar

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

    LaunchedEffect(isPolygonMode) {
        if (isPolygonMode && !hasShownInstructionBefore) {
            viewModel.setShowInstructionToast(true)
            viewModel.setHasShownInstructionBefore(true)
        } else if (!isPolygonMode) {
            viewModel.setShowInstructionToast(false)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Icon(
                    painter = painterResource(id = R.drawable.sowit_icon),
                    contentDescription = "Logo Sowit",
                    modifier = Modifier.height(30.dp),
                    tint = Color.Unspecified
                )
            },
            actions = {
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

@Composable
fun MapWithPlaces(
    viewModel: MainViewModel,
    isPolygonMode: Boolean = false,
    selectedArea: ma.abdokarimi.sowittechtest.entity.Area? = null,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    showInstructionToast: Boolean = false,
    onDismissToast: () -> Unit = {}
) {

    val casablancaPosition = LatLng(33.5731, -7.5898)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(casablancaPosition, 12f)
    }
    val polygonPoints by viewModel.polygonPoints.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()

    LaunchedEffect(selectedArea) {
        selectedArea?.let { area ->
            val areaPoints = viewModel.getAreaPolygonPoints(area)
            if (areaPoints.isNotEmpty()) {
                if (areaPoints.size == 1) {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(areaPoints[0], 15f),
                        durationMs = 1000
                    )
                } else {
                    val boundsBuilder = LatLngBounds.Builder()
                    areaPoints.forEach { point ->
                        boundsBuilder.include(point)
                    }
                    val bounds = boundsBuilder.build()

                    val padding = 100
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
                        durationMs = 1000
                    )
                }
            }
        }
    }

    LaunchedEffect(searchResult) {
        searchResult?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f),
                durationMs = 1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (isPolygonMode) {
                    viewModel.addPolygonPoint(latLng)
                } else {
                    viewModel.selectLocation(latLng)
                }
            }
        ) {

            searchResult?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Résultat de recherche"
                )
            }

            if (!isPolygonMode) {
                areas.forEach { area ->
                    val areaPoints = viewModel.getAreaPolygonPoints(area)
                    if (areaPoints.size >= 3) {
                        val isSelected = selectedArea?.id == area.id
                        Polygon(
                            points = areaPoints,
                            fillColor = if (isSelected) Color.Green.copy(alpha = 0.4f) else Color.Blue.copy(
                                alpha = 0.2f
                            ),
                            strokeColor = if (isSelected) Color.Green else Color.Blue,
                            strokeWidth = if (isSelected) 4f else 2f
                        )
                    }
                }
            }

            if (isPolygonMode) {
                polygonPoints.forEach { point ->
                    Circle(
                        center = point,
                        radius = 50.0,
                        fillColor = Color.White,
                        strokeColor = Color.Red,
                        strokeWidth = 4f
                    )
                }
            }

            if (isPolygonMode && polygonPoints.size >= 3) {
                Polygon(
                    points = polygonPoints,
                    fillColor = Color.Blue.copy(alpha = 0.3f),
                    strokeColor = Color.Blue,
                    strokeWidth = 3f
                )
            }

            if (isPolygonMode && polygonPoints.size >= 2) {
                Polyline(
                    points = polygonPoints,
                    color = Color.Blue,
                    width = 3f
                )
            }
        }

        ModeChip(
            isCreationMode = isPolygonMode,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                .align(Alignment.TopCenter)
        )

        SearchTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearchClick = {
                viewModel.searchPlace(searchQuery)
            },
            onClearClick = {
                onSearchQueryChange("")
                viewModel.clearSearchResult()
            },
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = if (isPolygonMode) 60.dp else 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .align(Alignment.TopCenter)
        )

        InstructionToast(
            message = "Tapez sur la carte pour créer des points de polygone. Tapez à nouveau sur un point pour le supprimer.",
            isVisible = showInstructionToast,
            onDismiss = onDismissToast,
            modifier = Modifier
                .padding(top = if (isPolygonMode) 120.dp else 80.dp)
                .align(Alignment.TopCenter),
            durationMs = 10000L
        )
    }
}
