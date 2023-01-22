package com.example.trimetable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {
    @GET("Metrolinks")
    @Headers("Ocp-Apim-Subscription-Key: XXXXXXXXXXXXXXXXXXX")
    fun getMetroData(): Call<MetrolinkResponse>
}