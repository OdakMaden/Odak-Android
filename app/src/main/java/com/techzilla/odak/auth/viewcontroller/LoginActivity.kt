package com.techzilla.odak.auth.viewcontroller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityLoginBinding
import com.techzilla.odak.main.viewcontrollers.MainActivity
import com.techzilla.odak.shared.service.repository.LoginRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController

class LoginActivity : AppCompatActivity(), LoginRepository.CheckListener {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() =  _binding!!

    private lateinit var loginRepository : LoginRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            //window.insetsController?.show(WindowInsets.Type.ime())
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
            else {
                AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_login_password),
                    "","", resources.getString(R.string.shared_Ok))
            }
        }

        binding.singButton.setOnClickListener {
            Intent(this, SingActivity::class.java).apply {
                startActivity(this)
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
            AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_login_message),
                "","", resources.getString(R.string.shared_Ok))
        }
    }
}