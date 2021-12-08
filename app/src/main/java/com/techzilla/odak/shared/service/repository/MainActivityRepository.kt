package com.techzilla.odak.shared.service.repository

import androidx.lifecycle.MutableLiveData
import com.techzilla.odak.shared.model.SystemConfigurationDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityRepository {
    private val service = ApiService.invoke()

    private var mutableLiveData = MutableLiveData<SystemConfigurationDTO>()
    val phoneNumberLiveData get() = mutableLiveData

    fun getPhoneNumber(){
        service.getPhoneNumbers().enqueue(object : Callback<SystemConfigurationDTO>{
            override fun onResponse(
                call: Call<SystemConfigurationDTO>,
                response: Response<SystemConfigurationDTO>
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<SystemConfigurationDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}