package uz.idea.mobio.network.interCeptor

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.net.HttpHeaders
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import uz.idea.mobio.BuildConfig.BASE_URL
import uz.idea.mobio.models.auth.ResAuth
import uz.idea.mobio.utils.appConstant.AppConstant.APPLICATION_JSON
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TokenInterceptor @Inject constructor(
    private val preferenceHelper: MySharedPreferences
) : Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest: Request = newRequestWithAccessToken(chain.request(), preferenceHelper.accessToken.toString())
        var oldResponse = chain.proceed(oldRequest)
        if (oldResponse.code == HttpURLConnection.HTTP_UNAUTHORIZED){
            val client = OkHttpClient()
            val params = JSONObject()
         synchronized(this){
             params.put("refresh_token", preferenceHelper.refreshToken ?: "")
             val body: RequestBody = RequestBody.create(oldResponse.body?.contentType(),params.toString())
             val nRequest = Request.Builder()
                 .url("${BASE_URL}/api/refresh/token")
                 .put(body)
                 .build()

             val responseRefresh = client.newCall(nRequest).execute()
             if (responseRefresh.code ==HttpURLConnection.HTTP_OK){
                 val jsonData = responseRefresh.body?.string() ?: ""
                 val gson = Gson()
                 val resAuth: ResAuth = gson.fromJson(jsonData, ResAuth::class.java)
                 preferenceHelper.accessToken = resAuth.access_token
                 preferenceHelper.refreshToken = resAuth.refresh_token
                 preferenceHelper.tokenType = resAuth.token_type
                 oldResponse.close()
                 return chain.proceed(newRequestWithAccessToken(oldRequest,preferenceHelper.accessToken.toString()))
             } else {
                 preferenceHelper.clearToken()
             }
         }
        }
        return oldResponse
    }


    private fun newRequestWithAccessToken(request: Request,accessToken: String): Request {
        return request.newBuilder()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .header(HttpHeaders.ACCEPT,APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE,APPLICATION_JSON)
            .build()
    }
}
