package com.techzilla.odak.shared.service.repository

import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    private val service = ApiService.invoke()


    fun checkPassword(password:String, listener: CheckListener){
        service.checkPassword(password).enqueue(object : Callback<MemberDTO>{
            override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                if (response.isSuccessful){
                    if(response.code() == 200){
                        rememberMemberDTO = response.body()
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

    interface CheckListener{
        fun checkPasswordListener(isCheck:Boolean)
    }
}