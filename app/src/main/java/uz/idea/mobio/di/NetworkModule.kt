package uz.idea.mobio.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.idea.mobio.BuildConfig
import uz.idea.mobio.BuildConfig.BASE_URL
import uz.idea.mobio.network.apiService.ApiService
import uz.idea.mobio.network.interCeptor.TokenInterceptor
import uz.idea.mobio.network.pagingService.PagingService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val READ_TIMEOUT = 300
private val WRITE_TIMEOUT = 300
private val CONNECTION_TIMEOUT = 100

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Suppress
    fun providesBaseUrl():String{
       return if (BuildConfig.DEBUG) BASE_URL
       else BASE_URL
    }


    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context, tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(ChuckerInterceptor(context)).build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(baseurl:String,okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseurl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit):ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesPagingService(retrofit: Retrofit):PagingService = retrofit.create(PagingService::class.java)
}