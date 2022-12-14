package uz.idea.mobio.utils.responseFetcher

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import uz.idea.mobio.R
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.utils.extension.LogData
import uz.idea.mobio.utils.resPonseState.ResponseState
import java.io.IOException
import javax.inject.Inject

interface ResponseFetcher{
    fun <T> getFlowResponseState(response: Response<T>?): Flow<ResponseState<T?>>

    class Base @Inject constructor(
        private val errorRepository: ErrorRepository,
        @ApplicationContext private val context: Context
    ): ResponseFetcher {
        override fun <T> getFlowResponseState(response: Response<T>?) = flow {
            var flow = try {
                coroutineScope {
                    if (response?.isSuccessful == true) ResponseState.Success(response.body())
                    else throw HttpException(response)
                }
            }catch (e: IOException){
                val errorEntity = ErrorEntity(error = e.message.toString(), errorCode = e.hashCode())
                errorRepository.saveError(errorEntity)
                ResponseState.Error(errorCode = e.hashCode(), liveError = errorRepository.getLiveError())
            }catch (e: HttpException){
                when(e.code()){
                    in 400..499->{
                        val errorEntity = ErrorEntity(error = e.response()?.errorBody()?.string().toString(), errorCode = e.code())
                        errorRepository.saveError(errorEntity)
                    }
                    in 500..599->{
                        val errorEntity = ErrorEntity(error = context.getString(R.string.server_error), errorCode = e.code())
                        errorRepository.saveError(errorEntity)
                    }
                    else ->{
                        val errorEntity = ErrorEntity(error = e.message(), errorCode = e.code())
                        errorRepository.saveError(errorEntity)
                    }
                }
                ResponseState.Error(errorCode = e.code(), liveError = errorRepository.getLiveError())
            } catch (e:Exception){
                val errorEntity = ErrorEntity(error = e.message.toString(), errorCode = e.hashCode())
                errorRepository.saveError(errorEntity)
                ResponseState.Error(errorCode = e.hashCode(), liveError = errorRepository.getLiveError())
            }
            emit(flow)
        }.flowOn(Dispatchers.IO)
    }
}