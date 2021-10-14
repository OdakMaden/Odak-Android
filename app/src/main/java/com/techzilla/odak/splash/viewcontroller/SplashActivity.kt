package com.techzilla.odak.splash.viewcontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.techzilla.odak.R
import com.techzilla.odak.auth.viewcontroller.LoginActivity
import com.techzilla.odak.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var _binding : ActivitySplashBinding? = null
    private val binding get() =  _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

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
                    Intent(this@SplashActivity, LoginActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                }
            }.start()
        }



    }
}