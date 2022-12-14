package uz.idea.mobio.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.idea.mobio.database.daoAndEntity.error.ErrorDao
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity

@Database(entities = [ErrorEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun errorDao():ErrorDao
}