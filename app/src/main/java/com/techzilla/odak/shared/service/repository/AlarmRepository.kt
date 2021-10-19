package com.techzilla.odak.shared.service.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.AlarmDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlarmRepository {

    private val service = ApiService.invoke()

    private var addNewAlarmMutableLiveData = MutableLiveData<AlarmDTO>()
    val addNewAlarmLiveData get() = addNewAlarmMutableLiveData

    private var alarmsListMutableLiveData = MutableLiveData<List<AlarmDTO>>()
    val alarmsListLiveData get() = alarmsListMutableLiveData

    private var errorMutableLiveData = MutableLiveData<String>()
    val errorLiveData get() = errorMutableLiveData

    fun addAlarm(jsonObject: JsonObject){
        service.addAlarm(rememberMemberDTO!!.memberID, jsonObject).enqueue(object : Callback<AlarmDTO>{
            override fun onResponse(call: Call<AlarmDTO>, response: Response<AlarmDTO>) {
                if (response.isSuccessful){
                    if (response.code() == 201){
                        addNewAlarmMutableLiveData.postValue(response.body())
                    }
                    else{
                        errorMutableLiveData.postValue("Alarm Oluşturulamadı.")
                    }
                }
                println(response.code())
            }

            override fun onFailure(call: Call<AlarmDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun getAlarms(){
        service.getAlarms(rememberMemberDTO!!.memberID).enqueue(object : Callback<List<AlarmDTO>>{
            override fun onResponse(
                call: Call<List<AlarmDTO>>,
                response: Response<List<AlarmDTO>>
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        alarmsListMutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<List<AlarmDTO>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}