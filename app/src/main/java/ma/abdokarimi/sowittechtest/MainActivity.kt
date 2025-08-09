package ma.abdokarimi.sowittechtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*

import ma.abdokarimi.sowittechtest.mvvm.MainViewModel
import ma.abdokarimi.sowittechtest.ui.components.AreaListDropdown
import ma.abdokarimi.sowittechtest.ui.components.DeleteAreaDialog
import ma.abdokarimi.sowittechtest.ui.components.InstructionToast
import ma.abdokarimi.sowittechtest.ui.components.ModeChip
import ma.abdokarimi.sowittechtest.ui.components.SaveAreaDialog
import ma.abdokarimi.sowittechtest.ui.components.SearchTopBar

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isPolygonMode by viewModel.isPolygonMode.collectAsState()
    val polygonPoints by viewModel.polygonPoints.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val selectedArea by viewModel.selectedArea.collectAsState()
    var showAreasList by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var areaToDelete by remember { mutableStateOf<ma.abdokarimi.sowittechtest.entity.Area?>(null) }
    var areaName by remember { mutableStateOf("") }
    var showInstructionToast by remember { mutableStateOf(false) }
    var hasShownInstructionBefore by remember { mutableStateOf(false) }

    // Show instruction toast only the first time creation mode is entered
    LaunchedEffect(isPolygonMode) {
        if (isPolygonMode && !hasShownInstructionBefore) {
            showInstructionToast = true
            hasShownInstructionBefore = true
        } else if (!isPolygonMode) {
            showInstructionToast = false
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
                                showSaveDialog = true
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
                        onExpandedChange = { showAreasList = it },
                        onAreaSelect = { area ->
                            viewModel.selectArea(area)
                        },
                        onAreaDelete = { area ->
                            areaToDelete = area
                            showDeleteDialog = true
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
            onSearchQueryChange = { searchQuery = it },
            showInstructionToast = showInstructionToast,
            onDismissToast = { showInstructionToast = false }
        )
    }

    // Save area dialog
    SaveAreaDialog(
        isVisible = showSaveDialog,
        areaName = areaName,
        onAreaNameChange = { areaName = it },
        onSave = {
            if (areaName.isNotBlank()) {
                viewModel.saveArea(areaName)
                areaName = ""
                showSaveDialog = false
            }
        },
        onDismiss = {
            showSaveDialog = false
            areaName = ""
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
            showDeleteDialog = false
            areaToDelete = null
        },
        onDismiss = {
            showDeleteDialog = false
            areaToDelete = null
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

    // Initialize camera position to Casablanca, Morocco
    val casablancaPosition = LatLng(33.5731, -7.5898)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(casablancaPosition, 12f)
    }
    val polygonPoints by viewModel.polygonPoints.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()

    // Navigate to selected area when it changes
    LaunchedEffect(selectedArea) {
        selectedArea?.let { area ->
            val areaPoints = viewModel.getAreaPolygonPoints(area)
            if (areaPoints.isNotEmpty()) {
                if (areaPoints.size == 1) {
                    // Single point - zoom to it
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(areaPoints[0], 15f),
                        durationMs = 1000
                    )
                } else {
                    // Multiple points - create bounds to fit all points with padding
                    val boundsBuilder = LatLngBounds.Builder()
                    areaPoints.forEach { point ->
                        boundsBuilder.include(point)
                    }
                    val bounds = boundsBuilder.build()

                    // Add padding around the bounds (100 pixels on all sides)
                    val padding = 100
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
                        durationMs = 1000
                    )
                }
            }
        }
    }

    // Navigate to search result when it changes
    LaunchedEffect(searchResult) {
        searchResult?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f),
                durationMs = 1000
            )
        }
    }

    // Box to overlay search bar on top of map
    Box(modifier = Modifier.fillMaxSize()) {
        // Map takes full available space
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


            // Search result marker
            searchResult?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Résultat de recherche"
                )
            }

            // Show all saved area polygons (except when in polygon creation mode)
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

            // Show polygon points as circles when in polygon mode
            if (isPolygonMode) {
                polygonPoints.forEach { point ->
                    Circle(
                        center = point,
                        radius = 50.0, // 50 meters radius - larger for better visibility
                        fillColor = Color.White,
                        strokeColor = Color.Red, // Changed to red for better contrast
                        strokeWidth = 4f // Thicker stroke for better visibility
                    )
                }
            }

            // Draw polygon if we have at least 3 points
            if (isPolygonMode && polygonPoints.size >= 3) {
                Polygon(
                    points = polygonPoints,
                    fillColor = Color.Blue.copy(alpha = 0.3f),
                    strokeColor = Color.Blue,
                    strokeWidth = 3f
                )
            }

            // Draw polyline to show current polygon outline
            if (isPolygonMode && polygonPoints.size >= 2) {
                Polyline(
                    points = polygonPoints,
                    color = Color.Blue,
                    width = 3f
                )
            }
        }

        // Mode indicator chip (above search bar, only in creation mode)
        ModeChip(
            isCreationMode = isPolygonMode,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                .align(Alignment.TopCenter)
        )

        // Floating Search Bar (overlaid on top of map, outside GoogleMap)
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

        // Instruction Toast (positioned below search bar)
        InstructionToast(
            message = "Tapez sur la carte pour créer des points de polygone. Tapez à nouveau sur un point pour le supprimer.",
            isVisible = showInstructionToast,
            onDismiss = onDismissToast,
            modifier = Modifier
                .padding(top = if (isPolygonMode) 120.dp else 80.dp) // Position below search bar, accounting for mode chip
                .align(Alignment.TopCenter),
            durationMs = 10000L
        )
    }
}
