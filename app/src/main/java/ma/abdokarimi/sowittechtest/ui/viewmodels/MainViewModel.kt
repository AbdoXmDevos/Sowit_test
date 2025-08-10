package ma.abdokarimi.sowittechtest.ui.viewmodels

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
    private val db = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "places-db"
    ).fallbackToDestructiveMigration().build()

    private val areaDao = db.areaDao()

    private val placesSearchUtils = PlacesSearchUtils(app)

    val areas = areaDao.getAllAreas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedLatLng = MutableStateFlow<LatLng?>(null)
    val selectedLatLng: StateFlow<LatLng?> = _selectedLatLng

    private val _polygonPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val polygonPoints: StateFlow<List<LatLng>> = _polygonPoints

    private val _isPolygonMode = MutableStateFlow(false)
    val isPolygonMode: StateFlow<Boolean> = _isPolygonMode

    private val _selectedArea = MutableStateFlow<Area?>(null)
    val selectedArea: StateFlow<Area?> = _selectedArea

    private val _searchResult = MutableStateFlow<LatLng?>(null)
    val searchResult: StateFlow<LatLng?> = _searchResult

    private val _showAreasList = MutableStateFlow(false)
    val showAreasList: StateFlow<Boolean> = _showAreasList

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog: StateFlow<Boolean> = _showSaveDialog

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _areaToDelete = MutableStateFlow<Area?>(null)
    val areaToDelete: StateFlow<Area?> = _areaToDelete

    private val _areaName = MutableStateFlow("")
    val areaName: StateFlow<String> = _areaName

    private val _showInstructionToast = MutableStateFlow(false)
    val showInstructionToast: StateFlow<Boolean> = _showInstructionToast

    private val _hasShownInstructionBefore = MutableStateFlow(false)
    val hasShownInstructionBefore: StateFlow<Boolean> = _hasShownInstructionBefore

    fun selectLocation(latLng: LatLng) {
        _selectedLatLng.value = latLng
    }

    fun togglePolygonMode() {
        _isPolygonMode.value = !_isPolygonMode.value
        if (!_isPolygonMode.value) {
            _polygonPoints.value = emptyList()
        }
    }

    fun addPolygonPoint(latLng: LatLng) {
        val currentPoints = _polygonPoints.value.toMutableList()

        val existingPointIndex = LocationUtils.findNearPointIndex(currentPoints, latLng)

        if (existingPointIndex != -1) {
            currentPoints.removeAt(existingPointIndex)
        } else {
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

    fun setShowAreasList(show: Boolean) {
        _showAreasList.value = show
    }

    fun setShowSaveDialog(show: Boolean) {
        _showSaveDialog.value = show
    }

    fun setShowDeleteDialog(show: Boolean) {
        _showDeleteDialog.value = show
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setAreaToDelete(area: Area?) {
        _areaToDelete.value = area
    }

    fun setAreaName(name: String) {
        _areaName.value = name
    }

    fun setShowInstructionToast(show: Boolean) {
        _showInstructionToast.value = show
    }

    fun setHasShownInstructionBefore(hasShown: Boolean) {
        _hasShownInstructionBefore.value = hasShown
    }
}
