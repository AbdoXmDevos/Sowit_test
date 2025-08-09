package ma.abdokarimi.sowittechtest.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ma.abdokarimi.sowittechtest.database.AppDatabase
import ma.abdokarimi.sowittechtest.entity.Area
import ma.abdokarimi.sowittechtest.utils.LocationUtils
import ma.abdokarimi.sowittechtest.utils.PlacesSearchUtils
import ma.abdokarimi.sowittechtest.utils.SerializationUtils

class MainViewModel(app: Application) : AndroidViewModel(app) {
    // Initialize database
    private val db = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "places-db"
    ).fallbackToDestructiveMigration().build()

    // Initialize DAO
    private val areaDao = db.areaDao()

    // Initialize utility classes
    private val placesSearchUtils = PlacesSearchUtils(
        app,
        "AIzaSyA-njYF7cbffSFJ2199ZOL_nGZGP3tDHvc" // Your API key from manifest
    )

    // UI state
    val areas = areaDao.getAllAreas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Selected location state
    private val _selectedLatLng = MutableStateFlow<LatLng?>(null)
    val selectedLatLng: StateFlow<LatLng?> = _selectedLatLng

    // Polygon creation state
    private val _polygonPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val polygonPoints: StateFlow<List<LatLng>> = _polygonPoints

    // Polygon mode state
    private val _isPolygonMode = MutableStateFlow(false)
    val isPolygonMode: StateFlow<Boolean> = _isPolygonMode

    // Selected area state
    private val _selectedArea = MutableStateFlow<Area?>(null)
    val selectedArea: StateFlow<Area?> = _selectedArea

    // Search functionality
    private val _searchResult = MutableStateFlow<LatLng?>(null)
    val searchResult: StateFlow<LatLng?> = _searchResult

    // UI actions
    fun selectLocation(latLng: LatLng) {
        _selectedLatLng.value = latLng
    }

    fun togglePolygonMode() {
        _isPolygonMode.value = !_isPolygonMode.value
        if (!_isPolygonMode.value) {
            // Clear polygon points when exiting polygon mode
            _polygonPoints.value = emptyList()
        }
    }

    fun addPolygonPoint(latLng: LatLng) {
        val currentPoints = _polygonPoints.value.toMutableList()

        // Check if point already exists (for deletion) using utility
        val existingPointIndex = LocationUtils.findNearPointIndex(currentPoints, latLng)

        if (existingPointIndex != -1) {
            // Remove existing point
            currentPoints.removeAt(existingPointIndex)
        } else {
            // Add new point
            currentPoints.add(latLng)
        }

        _polygonPoints.value = currentPoints
    }

    fun clearPolygonPoints() {
        _polygonPoints.value = emptyList()
    }



    fun saveArea(name: String) {
        val points = _polygonPoints.value
        if (points.size >= 3) {
            viewModelScope.launch {
                val pointsJson = SerializationUtils.serializePolygonPoints(points)
                areaDao.insert(
                    Area(
                        name = name,
                        polygonPointsJson = pointsJson
                    )
                )
                // Clear polygon and exit polygon mode
                _polygonPoints.value = emptyList()
                _isPolygonMode.value = false
            }
        }
    }

    fun getAreaPolygonPoints(area: Area): List<LatLng> {
        return SerializationUtils.deserializePolygonPoints(area.polygonPointsJson)
    }

    fun selectArea(area: Area) {
        _selectedArea.value = area
    }

    fun clearSelectedArea() {
        _selectedArea.value = null
    }

    fun deleteArea(area: Area) {
        viewModelScope.launch {
            areaDao.delete(area)
            // Clear selected area if it was the one being deleted
            if (_selectedArea.value?.id == area.id) {
                _selectedArea.value = null
            }
        }
    }

    fun searchPlace(query: String) {
        if (query.isBlank()) {
            _searchResult.value = null
            return
        }

        viewModelScope.launch {
            val result = placesSearchUtils.searchPlace(query)
            _searchResult.value = result
        }
    }

    fun clearSearchResult() {
        _searchResult.value = null
    }
}
