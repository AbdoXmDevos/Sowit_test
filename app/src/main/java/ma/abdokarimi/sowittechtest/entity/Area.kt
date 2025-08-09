package ma.abdokarimi.sowittechtest.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Area entity representing a geographical area
@Entity(tableName = "areas")
data class Area(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val polygonPointsJson: String // JSON string containing list of LatLng points
)
