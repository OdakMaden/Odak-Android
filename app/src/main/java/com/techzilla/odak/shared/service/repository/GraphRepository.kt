package com.techzilla.odak.shared.service.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.ExchangeRateGraphDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GraphRepository {

    private val service = ApiService.invoke()

    private var exchangeRateGraphMutableLiveData = MutableLiveData<ExchangeRateGraphDTO>()
    val exchangeRateGraphLiveData get() = exchangeRateGraphMutableLiveData

    fun getGraphData(currencyCode: String, graphPeriodEnum:String, timeStamp:String){
        service.getGraphData(rememberMemberDTO!!.memberID, currencyCode, graphPeriodEnum, timeStamp).enqueue(object :
            Callback<ExchangeRateGraphDTO>{
            override fun onResponse(
                call: Call<ExchangeRateGraphDTO>,
                response: Response<ExchangeRateGraphDTO>
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        exchangeRateGraphMutableLiveData.postValue(response.body())
                    }
                }
                println("graph -> ${response.code()}")
            }

            override fun onFailure(call: Call<ExchangeRateGraphDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun updateMemberDTO(memberDTO: JsonObject){
        service.patchMemberDTO(rememberMemberDTO!!.memberID, memberDTO).enqueue(object : Callback<MemberDTO>{
            override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        rememberMemberDTO = response.body()
                    }
                }
                println(response.code())
            }

            override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}