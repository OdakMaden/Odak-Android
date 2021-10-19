package com.techzilla.odak.shared.service.repository

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.constants.exchangeRateListMap
import com.techzilla.odak.shared.constants.odakTimePattern
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.model.TimeStamp
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainRepository {
    private val service = ApiService.invoke()

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
                println(response.code())
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
                exchangeRateListMap[exDTO.code] = term
                term++
            }
        }
        else{
            list.forEach { exDTO->
                exchangeRateList.removeAt(exchangeRateListMap[exDTO.code]!!)
                exchangeRateList.add(exchangeRateListMap[exDTO.code]!!, exDTO)
            }
        }
    }

    private fun periodicRequest(){
        object : CountDownTimer(3000, 3000){
            override fun onTick(millisUntilFinished: Long) {

            }

            @SuppressLint("SimpleDateFormat")
            override fun onFinish() {
                val calendar = Calendar.getInstance()
                val timeStamp = SimpleDateFormat(odakTimePattern).format(calendar.time)
                getExchangeRateList(rememberMemberDTO!!.memberID, timeStamp)
                println(timeStamp)
            }
        }.start()
    }
}