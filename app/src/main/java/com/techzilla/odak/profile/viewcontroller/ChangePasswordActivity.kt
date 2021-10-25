package com.techzilla.odak.profile.viewcontroller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityChangePasswordBinding
import com.techzilla.odak.shared.service.repository.LoginRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController

class ChangePasswordActivity : AppCompatActivity(), LoginRepository.UpdatePasswordListener{

    private val binding : ActivityChangePasswordBinding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    private val repository by lazy { LoginRepository() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
           /* AlertDialog.Builder(this)
                .setMessage("YENİ ŞİFRENİZ, YENİ ŞİFRE TEKRAR İLE UYUŞMAMAKTADIR. LÜTFEN AYNISINI GİRİNİZ.")
                .setPositiveButton("TAMAM"
                ) { dialog, which -> dialog.dismiss() }.show()

            */
        }
        else if (oldPassword.contains(newPassword)){
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            AlertDialogViewController.buildAlertDialog(this, "", resources.getString(R.string.alert_dont_same_old_password_message),
            "","", resources.getString(R.string.shared_Ok))
            /*
            AlertDialog.Builder(this)
                .setMessage("YENİ ŞİFRE, ESKİ ŞİFRENİZ İLE AYNI OLAMAZ.")
                .setPositiveButton("TAMAM"
                ) { dialog, which -> dialog.dismiss() }.show()
             */
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
            return
        }else {
            AlertDialogViewController.buildAlertDialog(this, "", message,"","",resources.getString(R.string.shared_Ok))
            /*
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(
                    "TAMAM"
                ) { dialog, which -> dialog.dismiss() }

             */
        }
    }
}