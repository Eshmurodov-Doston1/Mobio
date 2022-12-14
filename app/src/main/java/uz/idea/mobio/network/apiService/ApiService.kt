package uz.idea.mobio.network.apiService

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun methodeGet(
        @Url url:String,
        @QueryMap queryMap:HashMap<String,String>?
    ):Response<JsonElement>

    @POST
    suspend fun methodePost(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap:HashMap<String,String>?
    ):Response<JsonElement>

    @PUT
    suspend fun methodePut(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap:HashMap<String,String>?
    ):Response<JsonElement>


    @DELETE
    suspend fun methodeDelete(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap:HashMap<String,String>?
    ):Response<JsonElement>
}