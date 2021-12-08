package com.techzilla.odak.shared.service

import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.BASEURL
import com.techzilla.odak.shared.constants.apiKey
import com.techzilla.odak.shared.constants.application
import com.techzilla.odak.shared.constants.sessionKey
import com.techzilla.odak.shared.model.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @POST("Member")
    @Headers(apiKey, sessionKey, application)
    fun createMember(@Body jsonObject: JsonObject) : Call<MemberDTO>

    @GET("MemberByPassword/{Password}")
    @Headers(apiKey, sessionKey, application)
    fun checkPassword(@Path("Password") password:String) : Call<MemberDTO>

    @GET("ExchangeRate")
    @Headers(apiKey, sessionKey, application)
    fun getExchangeRateList(@Query("MemberID") memberID: String, @Query("TimeStamp") timeStamp: TimeStamp) : Call<List<ExchangeRateDTO>>

    @PATCH("Member/{MemberID}")
    @Headers(apiKey, sessionKey, application)
    fun patchMemberDTO(@Path("MemberID") memberID: String, @Body memberDTO: JsonObject) : Call<MemberDTO>

    @POST("Member/{MemberID}/Alarm")
    @Headers(apiKey, sessionKey, application)
    fun addAlarm(@Path("MemberID") memberID: String, @Body alarmMap: HashMap<String, Any>) : Call<AlarmDTO>

    @GET("Member/{MemberID}/Alarm")
    @Headers(apiKey, sessionKey, application)
    fun getAlarms(@Path("MemberID") memberID: String) : Call<List<AlarmDTO>>

    @GET("Member/{MemberID}/Alarm/{RID}")
    @Headers(apiKey, sessionKey, application)
    fun getAlarmDetail(@Path("MemberID") memberID: String, @Path("RID") rID: Int) : Call<AlarmDTO>

    @PATCH("Member/{MemberID}/Alarm/{RID}")
    @Headers(apiKey, sessionKey, application)
    fun updateAlarm(@Path("MemberID") memberID: String, @Path("RID") rID: Int, @Body alarmMap: HashMap<String, Any>): Call<AlarmDTO>

    @DELETE("Member/{MemberID}/Alarm/{RID}")
    @Headers(apiKey, sessionKey, application)
    fun deleteAlarm(@Path("MemberID") memberID: String, @Path("RID") rID: Int) : Call<Void>

    @GET("ExchangeRateGraph")
    @Headers(apiKey, sessionKey, application)
    fun getGraphData(@Query("MemberID") memberID : String, @Query("CurrencyCode") currencyCode:String, @Query("GraphPeriodEnum") graphPeriodEnum:String, @Query("TimeStamp") timeStamp:String): Call<ExchangeRateGraphDTO>

    @GET("System/Configuration")
    @Headers(apiKey, sessionKey, application)
    fun getPhoneNumbers(): Call<SystemConfigurationDTO>


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