package com.techzilla.odak.shared.service.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.shared.constants.application
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    private val service = ApiService.invoke()


    fun checkPassword(password:String, listener: CheckListener, sharedPref: SharedPreferences, context: Context){
        service.checkPassword(password).enqueue(object : Callback<MemberDTO>{
            override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                if (response.isSuccessful){
                    if(response.code() == 200){
                        rememberMemberDTO = response.body()
                        rememberUserGSMNoAndPassword(password, sharedPref,context)
                        listener.checkPasswordListener(true)
                    }
                    else{
                        listener.checkPasswordListener(false)
                    }
                }
            }

            override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun updateMemberDTO(memberDTO: JsonObject, listener:UpdatePasswordListener){
        service.patchMemberDTO(rememberMemberDTO!!.memberID, memberDTO).enqueue(object : Callback<MemberDTO>{
            override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                if (response.isSuccessful){
                    if (response.code() == 200){
                        rememberMemberDTO = response.body()
                    }
                    else{
                        listener.updatePasswordListener(response.message())
                    }
                }
                else{
                    listener.updatePasswordListener(response.message())
                }
            }

            override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun rememberUserGSMNoAndPassword(password:String, sharedPref: SharedPreferences, context: Context){
        with(sharedPref.edit()){
            putString(context.getString(R.string.password), password)
            putBoolean(context.getString(R.string.isLogin), true)
            apply()
        }
    }

    interface CheckListener{
        fun checkPasswordListener(isCheck:Boolean)
    }
    interface UpdatePasswordListener{
        fun updatePasswordListener(message:String)
    }
}