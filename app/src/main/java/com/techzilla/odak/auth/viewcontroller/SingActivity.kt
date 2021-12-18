package com.techzilla.odak.auth.viewcontroller

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivitySingBinding
import com.techzilla.odak.shared.service.repository.LoginRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController

class SingActivity : AppCompatActivity(), LoginRepository.SingListener {

    private val binding by lazy { ActivitySingBinding.inflate(layoutInflater) }

    private var gSMNoString = ""
    private val repository = LoginRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            //window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.gSMNo.addTextChangedListener {
            gSMNoString = "9$it"
        }

        binding.singButton.setOnClickListener {
            if (checkInfo()){
                AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_sing_check),
                    "","", resources.getString(R.string.shared_Ok))
            }
            else{
                val jsonObject = JsonObject()
                jsonObject.addProperty("FirstName", binding.firstName.text.toString())
                jsonObject.addProperty("LastName", binding.lastName.text.toString())
                jsonObject.addProperty("GSMNo", gSMNoString)
                jsonObject.addProperty("Email", binding.email.text.toString())
                repository.createMember(jsonObject, this)
            }
        }
    }

    private fun checkInfo(): Boolean {
        return (binding.email.text.isEmpty() && !binding.email.text.contains("@")
                || binding.gSMNo.text.isEmpty() && binding.gSMNo.text.toString().length != 11 ||
                binding.firstName.text.isEmpty() || binding.lastName.text.isEmpty())
    }


    override fun singMemberDTO(string: String) {
        if (string == "Success"){
            binding.gSMNo.visibility = GONE
            binding.email.visibility = GONE
            binding.firstName.visibility = GONE
            binding.lastName.visibility = GONE
            binding.progressbar.visibility = VISIBLE
            binding.successText.visibility = VISIBLE
            binding.singButton.visibility = GONE
            object : CountDownTimer(5000,5000){
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    finish()
                }
            }.start()
        }
        else{
            AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_sing_check),
                "","", resources.getString(R.string.shared_Ok))
        }
    }
}