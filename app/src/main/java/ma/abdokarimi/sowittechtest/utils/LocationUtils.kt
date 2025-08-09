package ma.abdokarimi.sowittechtest.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

// Utility object for location-related calculations
object LocationUtils {
    
    // Calculate the distance between two points using the Haversine formula
    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val earthRadius = 6371000.0 // Earth radius in meters
        val lat1Rad = Math.toRadians(point1.latitude)
        val lat2Rad = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLng = Math.toRadians(point2.longitude - point1.longitude)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLng / 2) * sin(deltaLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    // Check if two points are within a certain distance of each other
    fun arePointsNear(point1: LatLng, point2: LatLng, thresholdMeters: Double = 10.0): Boolean {
        return calculateDistance(point1, point2) < thresholdMeters
    }

    // Find the index of a point near the target point
    fun findNearPointIndex(points: List<LatLng>, target: LatLng, thresholdMeters: Double = 10.0): Int {
        return points.indexOfFirst { point ->
            arePointsNear(point, target, thresholdMeters)
        }
    }
}
