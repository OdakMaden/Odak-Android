package com.techzilla.odak.profile.viewcontroller

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.auth.viewcontroller.LoginActivity
import com.techzilla.odak.databinding.ActivityChangePasswordBinding
import com.techzilla.odak.shared.service.repository.LoginRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController

class ChangePasswordActivity : AppCompatActivity(), LoginRepository.UpdatePasswordListener{

    private val binding : ActivityChangePasswordBinding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    private val repository by lazy { LoginRepository() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE
            checkSamePassword(binding.newPassword.text.toString(), binding.repeatPassword.text.toString(), binding.oldPassword.text.toString())
        }

    }

    private fun checkSamePassword(newPassword: String, repeatPassword: String, oldPassword:String){
        if(!newPassword.contains(repeatPassword)){
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            AlertDialogViewController.buildAlertDialog(this, "",
                resources.getString(R.string.alert_dont_same_password_message),
                "","", resources.getString(R.string.shared_Ok))
        }
        else if (oldPassword.contains(newPassword)){
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            AlertDialogViewController.buildAlertDialog(this, "", resources.getString(R.string.alert_dont_same_old_password_message),
            "","", resources.getString(R.string.shared_Ok))
        }
        else {
            val updateMemberDTO = JsonObject()
            updateMemberDTO.addProperty("Password", newPassword)
            repository.updateMemberDTO(updateMemberDTO, this)
        }
    }

    override fun updatePasswordListener(message: String) {
        binding.componentProgressBar.progressbarContainer.visibility = View.GONE
        if (message == "Success"){
            AlertDialog.Builder(this).setMessage(resources.getString(R.string.alert_change_password_message))
                .setPositiveButton(resources.getString(R.string.shared_Ok)
            ) { dialog, which ->
                    dialog?.dismiss()
                    Intent(this, LoginActivity::class.java).apply {
                        startActivity(this)
                        finishAffinity()
                    }
                }.show()
        }else {
            AlertDialogViewController.buildAlertDialog(this, "", message,"","",resources.getString(R.string.shared_Ok))
        }
    }
}