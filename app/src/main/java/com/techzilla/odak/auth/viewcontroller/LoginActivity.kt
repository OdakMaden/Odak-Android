package com.techzilla.odak.auth.viewcontroller

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityLoginBinding
import com.techzilla.odak.main.viewcontrollers.MainActivity
import com.techzilla.odak.shared.service.repository.LoginRepository

class LoginActivity : AppCompatActivity(), LoginRepository.CheckListener {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() =  _binding!!

    private lateinit var loginRepository : LoginRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        loginRepository = LoginRepository()

        binding.loginButton.setOnClickListener {
            if (binding.password.text.length==6){
                binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE
                val sharedPref = getSharedPreferences(getString(R.string.Odak_shared_pref), MODE_PRIVATE)
                loginRepository.checkPassword(binding.password.text.toString(), this, sharedPref, this)
            }
        }
    }

    override fun checkPasswordListener(isCheck: Boolean) {
        binding.componentProgressBar.progressbarContainer.visibility = View.GONE
        if (isCheck){
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
                finish()
            }
        }else{
           AlertDialog.Builder(this).setMessage(resources.getString(R.string.alert_login_message)).setPositiveButton(
               resources.getText(R.string.shared_Ok)
           ) { dialog, p1 ->  dialog.dismiss()}.show()
        }
    }
}