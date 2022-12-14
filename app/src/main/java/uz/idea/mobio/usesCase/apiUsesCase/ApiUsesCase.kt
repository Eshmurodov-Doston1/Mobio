package uz.idea.mobio.usesCase.apiUsesCase

import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.models.mainProduct.MainProductModel
import uz.idea.mobio.repository.apiServiceRepository.apiRepository.ApiRepository
import uz.idea.mobio.repository.databaseRepository.errorRepository.ErrorRepository
import uz.idea.mobio.utils.extension.LogData
import uz.idea.mobio.utils.extension.parseClass
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.utils.responseFetcher.ResponseFetcher
import java.io.IOException
import javax.inject.Inject

class ApiUsesCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val responseFetcher: ResponseFetcher.Base,
    private val errorRepository: ErrorRepository
) {
    suspend fun methodeGet(url:String,queryMap:HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodGet(url,queryMap))

    suspend fun methodePost(url:String,body:Any,queryMap:HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodPost(url,body,queryMap))

    suspend fun methodePut(url:String,body:Any,queryMap:HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodPut(url,body,queryMap))

    suspend fun methodeDelete(url:String,body:Any,queryMap:HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodeDelete(url,body,queryMap))

    suspend fun methodeMainData(
        urlMain:String,queryMainMap: HashMap<String, String>,
        urlMainChild:String,queryMainChildMap:HashMap<String,String>) = flow {
        val flow =  try {
            coroutineScope {
                val listResponse = ArrayList<JsonElement?>()
                val responseMainData = async { apiRepository.methodGet(urlMain, queryMainMap) }.await()
                val responseMainChildData = async { apiRepository.methodGet(urlMainChild, queryMainChildMap) }.await()
                if (responseMainData.isSuccessful) listResponse.add(responseMainData.body())
                else throw HttpException(responseMainData)
                if (responseMainChildData.isSuccessful) listResponse.add(responseMainChildData.body())
                else throw HttpException(responseMainChildData)
                ResponseState.Success(listResponse)
            }
        }catch (e:IOException){
            val errorEntity = ErrorEntity(error = e.message, errorCode = e.hashCode())
            errorRepository.saveError(errorEntity)
            ResponseState.Error(e.hashCode(),errorRepository.getLiveError())
        } catch (e: HttpException){
            val errorEntity = ErrorEntity(error = e.response()?.errorBody()?.string().toString(), errorCode = e.code())
            errorRepository.saveError(errorEntity)
            ResponseState.Error(e.code(),errorRepository.getLiveError())
        } catch (e:Exception){
            val errorEntity = ErrorEntity(error = e.message, errorCode = e.hashCode())
            errorRepository.saveError(errorEntity)
            ResponseState.Error(e.hashCode(),errorRepository.getLiveError())
        }
        emit(flow)
    }.flowOn(Dispatchers.IO)
}