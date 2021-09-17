package com.techzilla.odak.main.viewcontrollers

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        var isOpenMenu = false
        backgroundMenu(isOpenMenu)
        binding.container.isSelected = isOpenMenu

        supportFragmentManager.beginTransaction().apply {
            this.add(binding.fragmentContainer.id, MarketFragment())
            commit()
        }


        binding.menuButton.setOnClickListener {
            isOpenMenu = !isOpenMenu
            if (isOpenMenu){
                binding.menuButton.animate().rotation(90.0f).setInterpolator(AccelerateInterpolator()).duration=1500
                binding.container.animate()
                    .translationX((binding.root.width * 0.6).toFloat())
                    .translationY((binding.root.height * 0.25).toFloat())
                    .setInterpolator(AccelerateInterpolator()).duration = 1500
            }
            else{
                binding.menuButton.animate().rotation(0.0f).setInterpolator(AccelerateInterpolator()).duration=1500
                binding.container.animate()
                    .translationX(0.0f)
                    .translationY(0.0f)
                    .setInterpolator(AccelerateInterpolator()).duration = 1500
            }
            binding.container.isSelected = isOpenMenu
            backgroundMenu(isOpenMenu)
        }

        binding.piyasa.setOnClickListener {
            println("piyasa")
        }
        binding.converter.setOnClickListener {
            println("converter")
        }
        binding.call.setOnClickListener {
            println("call")
        }
        binding.notification.setOnClickListener {
            println("notification")
        }
        binding.logout.setOnClickListener {
            println("logout")
        }
    }



    private fun backgroundMenu(isEnable:Boolean){
        binding.piyasa.isEnabled = isEnable
        binding.converter.isEnabled = isEnable
        binding.call.isEnabled = isEnable
        binding.notification.isEnabled = isEnable
        binding.logout.isEnabled = isEnable
    }
}