package com.techzilla.odak.shared.service.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository {
    private val service = ApiService.invoke()

    private var exchangeRateListMutableLiveData = MutableLiveData<List<ExchangeRateDTO>>()
    val exchangeRateListLiveData get() = exchangeRateListMutableLiveData

    private var errorMutableLiveData = MutableLiveData<String>()
    val errorLiveData get() = errorMutableLiveData

    fun getExchangeRateList(){
        service.getExchangeRateList().enqueue(object : Callback<List<ExchangeRateDTO>>{
            override fun onResponse(
                call: Call<List<ExchangeRateDTO>>,
                response: Response<List<ExchangeRateDTO>>
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        exchangeRateListMutableLiveData.postValue(response.body())
                    }
                    else{
                        errorLiveData.postValue("${response.code()}, ağ hatası")
                    }
                }
            }

            override fun onFailure(call: Call<List<ExchangeRateDTO>>, t: Throwable) {
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