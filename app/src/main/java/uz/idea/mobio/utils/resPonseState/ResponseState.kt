package uz.idea.mobio.utils.resPonseState

import androidx.lifecycle.LiveData
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity

sealed class  ResponseState<out T>{
    object Loading: ResponseState<Nothing>()
    data class Success<T>(val data:T): ResponseState<T>()
    data class Error(val errorCode:Int?=null,val liveError:LiveData<List<ErrorEntity>>?=null): ResponseState<Nothing>()
}
