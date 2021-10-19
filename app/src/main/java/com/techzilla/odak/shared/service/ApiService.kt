package com.techzilla.odak.shared.service

import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.BASEURL
import com.techzilla.odak.shared.constants.apiKey
import com.techzilla.odak.shared.constants.application
import com.techzilla.odak.shared.constants.sessionKey
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.model.TimeStamp
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("MemberByPassword/{Password}")
    @Headers(apiKey, sessionKey, application)
    fun checkPassword(@Path("Password") password:String) : Call<MemberDTO>

    @GET("ExchangeRate")
    @Headers(apiKey, sessionKey, application)
    fun getExchangeRateList(@Query("MemberID") memberID: String, @Query("TimeStamp") timeStamp: TimeStamp) : Call<List<ExchangeRateDTO>>

    @PATCH("Member/{MemberID}")
    @Headers(apiKey, sessionKey, application)
    fun patchMemberDTO(@Path("MemberID") memberID: String, @Body memberDTO: JsonObject) : Call<MemberDTO>

    companion object{
        operator fun invoke():ApiService{
            val client = OkHttpClient.Builder().readTimeout(1200, TimeUnit.SECONDS).connectTimeout(1200,
                TimeUnit.SECONDS).build()
            val retrofit = Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
            return retrofit.create(ApiService::class.java)
        }
    }
}