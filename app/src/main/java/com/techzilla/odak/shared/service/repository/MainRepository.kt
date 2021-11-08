package com.techzilla.odak.shared.service.repository

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.*
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.model.TimeStamp
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainRepository {
    private val service = ApiService.invoke()

    private val periodTimeGetExchangeRateList = object : CountDownTimer(3000, 3000){
        override fun onTick(millisUntilFinished: Long) {

        }

        @SuppressLint("SimpleDateFormat")
        override fun onFinish() {
            val calendar = Calendar.getInstance()
            val timeStamp = SimpleDateFormat(odakTimePattern).format(calendar.time)
            getExchangeRateList(rememberMemberDTO!!.memberID, timeStamp)
        }
    }

    private var exchangeRateListMutableLiveData = MutableLiveData<List<ExchangeRateDTO>>()
    val exchangeRateListLiveData get() = exchangeRateListMutableLiveData

    private var errorMutableLiveData = MutableLiveData<String>()
    val errorLiveData get() = errorMutableLiveData

    fun getExchangeRateList(memberID : String, timeStamp:TimeStamp){
        service.getExchangeRateList(memberID, timeStamp).enqueue(object : Callback<List<ExchangeRateDTO>>{
            override fun onResponse(
                call: Call<List<ExchangeRateDTO>>,
                response: Response<List<ExchangeRateDTO>>
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        response.body()?.let {
                            exchangeRateListMutableLiveData.postValue(exchangeRateList)
                            addOrChangeList(it)
                        }
                        periodicRequest()
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
            }

            override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addOrChangeList(list : List<ExchangeRateDTO>){
        if (exchangeRateList.size == 0){
            exchangeRateList.addAll(list)
            var term = 0
            exchangeRateList.forEach { exDTO ->
                exchangeRateDTOListMap[exDTO.code] = exDTO
                exchangeRateListForIndexMap[exDTO.code] = term
                term++
            }
        }
        else{
            list.forEach { exDTO->
                exchangeRateDTOListMap[exDTO.code] = exDTO
                exchangeRateList.removeAt(exchangeRateListForIndexMap[exDTO.code]!!)
                exchangeRateList.add(exchangeRateListForIndexMap[exDTO.code]!!, exDTO)
            }
        }
    }

    private fun periodicRequest(){
        periodTimeGetExchangeRateList.start()
    }

    fun periodicRequestClose(){
        periodTimeGetExchangeRateList.cancel()
    }
}