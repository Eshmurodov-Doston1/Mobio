package uz.idea.mobio.database.daoAndEntity.error

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ErrorEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    @ColumnInfo(name = "error")
    val error:String?=null,
    @ColumnInfo(name = "errorCode")
    val errorCode:Int?=null
)
