package ma.abdokarimi.sowittechtest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ma.abdokarimi.sowittechtest.dao.AreaDao
import ma.abdokarimi.sowittechtest.entity.Area

@Database(entities = [Area::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
}
