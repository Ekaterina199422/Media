package ru.netology.mypleyaer.Api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.netology.mypleyaer.BuildConfig
import ru.netology.mypleyaer.dto.Album


const val BASE_URL = "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/"

interface AlbomApi {
    @GET("album.json")
    suspend fun getAlbum(): Response<Album>

    companion object {
        private val logging = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        private val okhttp = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okhttp)
            .build()

        val service: AlbomApi by lazy {
            retrofit.create(AlbomApi::class.java)
        }
    }

}