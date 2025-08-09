package ma.abdokarimi.sowittechtest.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ma.abdokarimi.sowittechtest.entity.Area

// Data Access Object for Area entity operations
@Dao
interface AreaDao {

    @Insert
    suspend fun insert(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Query("SELECT * FROM areas")
    fun getAllAreas(): Flow<List<Area>>

    @Query("SELECT * FROM areas WHERE id = :id")
    suspend fun getAreaById(id: Int): Area?
}
