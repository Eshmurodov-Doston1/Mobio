package uz.idea.mobio.database.daoAndEntity.error

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ErrorDao {
    @Insert
    fun saveError(errorEntity: ErrorEntity)

    @Query("DELETE FROM errorEntity")
    fun removeErrorTable()

    @Query("SELECT*FROM errorentity")
    fun getAllError():LiveData<List<ErrorEntity>>
}