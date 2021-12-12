package com.example.books.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

private const val BASE_URL = "https://www.googleapis.com/books/v1/"

/*private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()*/

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BookSearchApiService {
    @GET
    fun getBooks(@Url url: String) : Call<OnSearch>
}

object BookSearchApi {
    val retrofitService: BookSearchApiService by lazy { retrofit.create(BookSearchApiService::class.java) }
}