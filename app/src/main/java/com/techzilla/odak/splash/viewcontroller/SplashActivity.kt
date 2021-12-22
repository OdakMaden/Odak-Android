package com.techzilla.odak.splash.viewcontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.appcompat.app.AppCompatActivity
import com.techzilla.odak.R
import com.techzilla.odak.auth.viewcontroller.LoginActivity
import com.techzilla.odak.databinding.ActivitySplashBinding
import com.techzilla.odak.main.viewcontrollers.MainActivity
import com.techzilla.odak.shared.constants.phoneNumber1
import com.techzilla.odak.shared.constants.refreshInterval
import com.techzilla.odak.shared.service.repository.LoginRepository
import com.techzilla.odak.shared.service.repository.MainActivityRepository

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), LoginRepository.CheckListener {

    private var _binding : ActivitySplashBinding? = null
    private val binding get() =  _binding!!

    private val loginRepository by lazy { LoginRepository() }
    private val repository by lazy { MainActivityRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
           // window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        repository.getPhoneNumber()
        repository.phoneNumberLiveData.observe(this, {
           /* if (it.phoneNo01 != null){
                phoneNumber1 = it.phoneNo01
            }
            else if(it.phoneNo02 != null){
                phoneNumber1 = it.phoneNo02
            }

            */
            phoneNumber1 = it.phoneNo01 ?: it.phoneNo02
            refreshInterval = it.refreshInterval ?: 3
        })



        binding.videoView.apply {
            setBackgroundColor(resources.getColor(android.R.color.transparent, resources.newTheme()))
            setZOrderOnTop(true)
            setVideoPath("android.resource://${packageName}/${R.raw.logoanimasyon}")
            start()
        }.also {
            object : CountDownTimer(3500,3500){
                override fun onTick(p0: Long) {
                }

                override fun onFinish() {
                    val sharedPref = getSharedPreferences(getString(R.string.Odak_shared_pref), MODE_PRIVATE)
                    val password = sharedPref.getString(resources.getString(R.string.password), "")
                    val isLogin = sharedPref.getBoolean(resources.getString(R.string.isLogin), false)
                    if (password != null && password != "" && isLogin){
                        loginRepository.checkPassword(password, this@SplashActivity, sharedPref, this@SplashActivity)
                    }
                    else{
                        Intent(this@SplashActivity, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }.start()
        }
    }

    override fun checkPasswordListener(isCheck: Boolean) {
        if (isCheck){
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
                finish()
            }
        }else{
            Intent(this@SplashActivity, LoginActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }
}