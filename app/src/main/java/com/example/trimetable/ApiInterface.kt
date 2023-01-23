package com.example.trimetable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {
    @GET("Metrolinks?\$filter=PIDREF%20eq%20%27EDL-TPID02%27%20or%20PIDREF%20eq%20%27EDL-TPID03%27%20")
    @Headers("Ocp-Apim-Subscription-Key: xxxxxxxxxx")
    fun getMetroData(): Call<MetrolinkResponse>
}