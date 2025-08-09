package ma.abdokarimi.sowittechtest.utils

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Declaration of the SerializationUtils object
object SerializationUtils {
    private val gson = Gson()

    // Serializes a list of LatLng points to a JSON string
    fun serializePolygonPoints(points: List<LatLng>): String {
        val pointMaps = points.map {
            mapOf("latitude" to it.latitude, "longitude" to it.longitude)
        }
        return gson.toJson(pointMaps)
    }



    // Deserializes a JSON string back to a list of LatLng points
    fun deserializePolygonPoints(json: String): List<LatLng> {
        return try {
            val type = object : TypeToken<List<Map<String, Double>>>() {}.type
            val pointMaps: List<Map<String, Double>> = gson.fromJson(json, type)
            pointMaps.map { LatLng(it["latitude"] ?: 0.0, it["longitude"] ?: 0.0) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
