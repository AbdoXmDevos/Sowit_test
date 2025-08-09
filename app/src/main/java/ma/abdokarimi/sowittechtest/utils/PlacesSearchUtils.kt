package ma.abdokarimi.sowittechtest.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place as GooglePlace
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Utility class for Google Places API search
class PlacesSearchUtils(private val context: Context, private val apiKey: String) {
    
    private val placesClient: PlacesClient by lazy {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        Places.createClient(context)
    }

    // Public search method
    suspend fun searchPlace(query: String): LatLng? {
        if (query.isBlank()) return null

        return try {
            // Get autocomplete predictions
            val predictions = getAutocompletePredictions(query)
            if (predictions.isEmpty()) return null

            // Fetch place details for the first prediction
            val placeId = predictions[0].placeId
            fetchPlaceCoordinates(placeId)
        } catch (e: Exception) {
            Log.e("PlacesSearchUtils", "Search failed", e)
            null
        }
    }

    // Autocomplete predictions
    private suspend fun getAutocompletePredictions(query: String) = suspendCancellableCoroutine { continuation ->
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                continuation.resume(response.autocompletePredictions)
            }
            .addOnFailureListener { exception ->
                Log.e("PlacesSearchUtils", "Autocomplete failed", exception)
                continuation.resume(emptyList())
            }
    }

    // Place details
    private suspend fun fetchPlaceCoordinates(placeId: String): LatLng? = suspendCancellableCoroutine { continuation ->
        val placeFields = listOf(GooglePlace.Field.ID, GooglePlace.Field.LAT_LNG, GooglePlace.Field.NAME)
        val fetchRequest = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(fetchRequest)
            .addOnSuccessListener { fetchResponse ->
                val latLng = fetchResponse.place.latLng?.let { 
                    LatLng(it.latitude, it.longitude) 
                }
                continuation.resume(latLng)
            }
            .addOnFailureListener { exception ->
                Log.e("PlacesSearchUtils", "Place fetch failed", exception)
                continuation.resume(null)
            }
    }
}
