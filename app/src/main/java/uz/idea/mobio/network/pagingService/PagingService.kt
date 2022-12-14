package uz.idea.mobio.network.pagingService

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface PagingService {
    @GET
    suspend fun methodeGet(
        @Url url:String,
        @QueryMap queryMap:HashMap<String,String>?
    ): Response<JsonElement>
}