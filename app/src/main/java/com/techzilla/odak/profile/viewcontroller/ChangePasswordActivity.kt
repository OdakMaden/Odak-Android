package com.techzilla.odak.profile.viewcontroller

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.techzilla.odak.databinding.ActivityChangePasswordBinding
import com.techzilla.odak.shared.service.repository.LoginRepository

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
            checkSamePassword(binding.newPassword.text.toString(), binding.repeatPassword.text.toString(), binding.oldPassword.text.toString())
        }

    }

    private fun checkSamePassword(newPassword: String, repeatPassword: String, oldPassword:String){
        if(!newPassword.contains(repeatPassword)){
            AlertDialog.Builder(this)
                .setMessage("YENİ ŞİFRENİZ, YENİ ŞİFRE TEKRAR İLE UYUŞMAMAKTADIR. LÜTFEN AYNISINI GİRİNİZ.")
                .setPositiveButton("TAMAM"
                ) { dialog, which -> dialog.dismiss() }
        }
        else if (oldPassword.contains(newPassword)){
            AlertDialog.Builder(this)
                .setMessage("YENİ ŞİFRE, ESKİ ŞİFRENİZ İLE AYNI OLAMAZ.")
                .setPositiveButton("TAMAM"
                ) { dialog, which -> dialog.dismiss() }
        }
        else {
            val updateMemberDTO = JsonObject()
            updateMemberDTO.addProperty("Password", newPassword)
            repository.updateMemberDTO(updateMemberDTO, this)
        }
    }

    override fun updatePasswordListener(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("TAMAM"
            ) { dialog, which -> dialog.dismiss() }
    }
}