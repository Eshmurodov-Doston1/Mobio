package uz.idea.mobio.repository.apiServiceRepository.apiRepositoryImpl

import com.google.gson.JsonElement
import retrofit2.Response
import uz.idea.mobio.network.apiService.ApiService
import uz.idea.mobio.repository.apiServiceRepository.apiRepository.ApiRepository
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
):ApiRepository {
    override suspend fun methodGet(
        fullUrl: String,
        queryMap: HashMap<String, String>?
    ): Response<JsonElement> = apiService.methodeGet(url = fullUrl,queryMap)

    override suspend fun methodPost(
        fullUrl: String,
        data: Any,
        queryMap: HashMap<String, String>?
    ): Response<JsonElement> = apiService.methodePost(url = fullUrl, body = data,queryMap)

    override suspend fun methodPut(
        fullUrl: String,
        data: Any,
        queryMap: HashMap<String, String>?
    ): Response<JsonElement> = apiService.methodePut(url = fullUrl, body = data,queryMap)

    override suspend fun methodeDelete(
        fullUrl: String,
        data: Any,
        queryMap: HashMap<String, String>?
    ): Response<JsonElement> = apiService.methodeDelete(url = fullUrl, body = data,queryMap)
}