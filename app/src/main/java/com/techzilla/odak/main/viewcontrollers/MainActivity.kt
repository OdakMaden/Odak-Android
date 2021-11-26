package com.techzilla.odak.main.viewcontrollers

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

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
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
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


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


        binding.defaultClickContainer.setOnClickListener {
            isEnableChange()
            binding.menuButton.animate().rotation(0.0f).setInterpolator(AccelerateInterpolator()).duration=600
            binding.container.animate()
                .translationX(0.0f)
               // .translationY(0.0f)
                .setInterpolator(AccelerateInterpolator()).duration = 600
            binding.defaultClickContainer.visibility = GONE
            backgroundMenu(false)
        }


        binding.menuButton.setOnClickListener {
            isOpenMenu = !isOpenMenu
            if (isOpenMenu){
                binding.defaultClickContainer.visibility = VISIBLE
                isEnableChange()
                binding.menuButton.animate().rotation(90.0f).setInterpolator(AccelerateInterpolator()).duration=600
                binding.container.animate()
                    .translationX((binding.root.width * 0.6).toFloat())
                    //.translationY((binding.root.height * 0.25).toFloat())
                    .translationY(0.0f)
                    .setInterpolator(AccelerateInterpolator()).duration = 600
            }
            else{
                isEnableChange()
                binding.menuButton.animate().rotation(0.0f).setInterpolator(AccelerateInterpolator()).duration=600
                binding.container.animate()
                    .translationX(0.0f)
                    //.translationY(0.0f)
                    .setInterpolator(AccelerateInterpolator()).duration = 600
                binding.defaultClickContainer.visibility = GONE
            }
            binding.container.isSelected = isOpenMenu

            backgroundMenu(isOpenMenu)
        }

        /*
        binding.market.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, marketFragment)
                .addToBackStack(MarketFragment.TAG)
                .commit()

            isEnableChange()
            binding.menuButton.animate().rotation(0.0f).setInterpolator(AccelerateInterpolator()).duration=1000
            binding.container.animate()
                .translationX(0.0f)
                //.translationY(0.0f)
                .setInterpolator(AccelerateInterpolator()).duration = 1000
            binding.defaultClickContainer.visibility = GONE
            isOpenMenu = false
            binding.container.isSelected = isOpenMenu

            backgroundMenu(isOpenMenu)
        }

         */
        binding.converter.setOnClickListener {
            setFragment(converterFragment, MarketFragment.TAG)
        }
        binding.call.setOnClickListener {
            println("call")
        }
        binding.notification.setOnClickListener {
            setFragment(alarmFragment, MarketFragment.TAG)
        }
        binding.profile.setOnClickListener {
            setFragment(profileFragment, MarketFragment.TAG)
        }

    }

    private fun isEnableChange(){
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
    }

    private fun setFragment(fragment:Fragment, TAG:String){
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainer.id, fragment)
            addToBackStack(TAG)
            commit()
        }
        isEnableChange()
        binding.menuButton.animate().apply {
            rotation(0.0f)
            interpolator = AccelerateInterpolator()
            duration=600
        }
        binding.container.animate().apply {
            translationX(0.0f)
            interpolator = AccelerateInterpolator()
            duration = 600
        }
        binding.defaultClickContainer.visibility = GONE
        isOpenMenu = false
        binding.container.isSelected = isOpenMenu

        backgroundMenu(isOpenMenu)
    }

    private fun backgroundMenu(isEnable:Boolean){
       // binding.market.isEnabled = isEnable
        binding.converter.isEnabled = isEnable
        binding.call.isEnabled = isEnable
        binding.notification.isEnabled = isEnable
        binding.profile.isEnabled = isEnable
    }

    override fun menuVisible(visible: Int) {
        binding.menuButton.visibility = visible
    }
}