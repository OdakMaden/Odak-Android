package com.techzilla.odak.main.viewcontrollers

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.converter.viewcontrollers.ConverterFragment
import com.techzilla.odak.databinding.ActivityMainBinding
import com.techzilla.odak.alarm.viewcontroller.AlarmFragment
import com.techzilla.odak.profile.viewcontroller.ProfileFragment
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.helper_interface.MenuButtonListener
import com.techzilla.odak.shared.service.repository.MainActivityRepository
import com.techzilla.odak.shared.service.repository.MainRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController

class MainActivity : AppCompatActivity(), MenuButtonListener {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val marketFragment = MarketFragment(this)
    private val converterFragment = ConverterFragment(this)
    private val profileFragment = ProfileFragment(this)
    private val alarmFragment = AlarmFragment(this)
    private var isOpenMenu = false

    private val repository by lazy { MainActivityRepository() }
    private var phoneNumber1 : String? = null

    private var requestSinglePermissionLauncher : ActivityResultLauncher<String>? = null

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == RESULT_OK){
            AlertDialogViewController.buildAlertDialog(this, resources.getString(R.string.alert_alarm_title),
                    resources.getString(R.string.alert_alarm_message), "","",resources.getString(R.string.shared_Ok))
            //AlertDialog.Builder(requireContext()).setTitle("Alarm").setMessage("Alarm Başarılı Olarak Kaydedilmiştir.").setPositiveButton("Tamam"
            //) { dialog, p1 ->  dialog.dismiss()}.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            //window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
       // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)



        requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (phoneNumber1 != null) {
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:${phoneNumber1}")).apply {
                        startActivity(this)
                    }
                }
                else{
                    AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_phone_number),
                        "","", resources.getString(R.string.shared_Ok))
                }
            }
            else{
                AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_phone_number),
                    "","", resources.getString(R.string.shared_Ok))
            }
        }

        backgroundMenu(isOpenMenu)
        binding.container.isSelected = isOpenMenu

        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainer.id, marketFragment, MarketFragment.TAG)
            commit()
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(Constants.TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result

                if (token != rememberMemberDTO!!.fCMToken){
                    println(token)
                    val repositoryMain = MainRepository()
                    val updateMemberDTO = JsonObject()
                    updateMemberDTO.addProperty("FCMToken", token)
                    repositoryMain.updateMemberDTO(updateMemberDTO)
                }
            })

        repository.getPhoneNumber()
        repository.phoneNumberLiveData.observe(this, {
            if (it.phoneNo01 != null){
                phoneNumber1 = it.phoneNo01
            }
            else if(it.phoneNo02 != null){
                phoneNumber1 = it.phoneNo02
            }
        })

        binding.defaultClickContainer.setOnClickListener {
            menuButtonAnimation(0.0f, false)
            binding.container.animate().apply {
                translationX(0.0f)
                interpolator = AccelerateInterpolator()
                duration = 600
            }
            binding.defaultClickContainer.visibility = GONE
            backgroundMenu(false)
        }

        binding.menuButton.setOnClickListener {
            binding.defaultClickContainer.visibility = VISIBLE
            menuButtonAnimation(90.0f, true)
            binding.container.animate().apply {
                translationX((binding.root.width * 0.6).toFloat())
                interpolator = AccelerateInterpolator()
                duration = 600
            }
            binding.container.isSelected = isOpenMenu
            backgroundMenu(isOpenMenu)
        }


        binding.converter.setOnClickListener {
            setFragment(converterFragment, MarketFragment.TAG)
        }
        binding.call.setOnClickListener {
            requestSinglePermissionLauncher!!.launch(Manifest.permission.CALL_PHONE)
            /*
            if (phoneNumber1 != null) {
                Intent(Intent.ACTION_CALL, Uri.parse("tel:${phoneNumber1}")).apply {
                    startActivity(this)
                }
            }
            else{
                AlertDialogViewController.buildAlertDialog(this, "",resources.getString(R.string.alert_phone_number),
                    "","", resources.getString(R.string.shared_Ok))
            }

             */
        }
        binding.notification.setOnClickListener {
            setFragment(alarmFragment, MarketFragment.TAG)
        }
        binding.profile.setOnClickListener {
            setFragment(profileFragment, MarketFragment.TAG)
        }

    }

    private fun setFragment(fragment:Fragment, TAG:String){
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainer.id, fragment)
            addToBackStack(TAG)
            commit()
        }
        menuButtonAnimation(0.0f, false)
        binding.container.animate().apply {
            translationX(0.0f)
            interpolator = AccelerateInterpolator()
            duration = 600
        }
        binding.defaultClickContainer.visibility = GONE
        binding.container.isSelected = isOpenMenu

        backgroundMenu(isOpenMenu)
    }

    private fun backgroundMenu(isEnable:Boolean){
        binding.converter.isEnabled = isEnable
        binding.call.isEnabled = isEnable
        binding.notification.isEnabled = isEnable
        binding.profile.isEnabled = isEnable
    }

    override fun menuVisible(visible: Int) {
        binding.menuButton.visibility = visible
        if (visible == VISIBLE){
            isOpenMenu = false
        }
    }


    private fun menuButtonAnimation(value: Float, isMenu:Boolean){
        isOpenMenu = isMenu
        binding.defaultClickContainer.isEnabled = false
        binding.menuButton.isEnabled = false

        object : CountDownTimer(600, 600){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                binding.defaultClickContainer.isEnabled = true
                binding.menuButton.isEnabled = true
            }
        }.start()

        binding.menuButton.animate().apply {
            rotation(value)
            interpolator = AccelerateInterpolator()
            duration=600
        }
    }
}