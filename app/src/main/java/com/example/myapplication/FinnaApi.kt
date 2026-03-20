package com.example.myapplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnaApi {

    @GET("api/v1/search")
    suspend fun getLandscapes(
        @Query("lookfor") lookfor: String,

        @Query("filter[]") filters: List<String>,

        @Query("field[]") fields: List<String> = listOf("id", "title", "year", "images"),

        @Query("sort") sort: String = "random_seed",

        @Query("page") page: Int = 1,

        @Query("limit") limit: Int = 20
    ): FinnaResponse
}

object FinnaNetwork {
    private const val BASE_URL = "https://api.finna.fi/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FinnaApi = retrofit.create(FinnaApi::class.java)
}