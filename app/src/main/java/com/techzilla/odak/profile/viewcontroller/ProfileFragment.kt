package com.techzilla.odak.profile.viewcontroller

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import com.techzilla.odak.R
import com.techzilla.odak.auth.viewcontroller.LoginActivity
import com.techzilla.odak.databinding.FragmentProfileBinding
import com.techzilla.odak.shared.constants.odakTimePattern
import com.techzilla.odak.shared.constants.rememberMemberDTO

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rememberMemberDTO?.let {
            binding.nameText.text = it.firstName
            binding.name.text = it.firstName
            binding.surname.text = it.lastName
            binding.email.text = it.email

        }


        binding.logoutButton.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences(resources.getString(R.string.Odak_shared_pref), MODE_PRIVATE)
            with(sharedPref.edit()){
                putBoolean(resources.getString(R.string.isLogin), false)
                apply()
            }
            Intent(requireActivity(), LoginActivity::class.java).apply {
                startActivity(this)
                requireActivity().finish()
            }
        }

        binding.notificationButton.setOnClickListener {
            openNotificationSetting()
        }

        binding.changePasswordButton.setOnClickListener {
            Intent(requireActivity(), ChangePasswordActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getNotificationPermission()
    }

    private fun getNotificationPermission(){
        val notificationManagerCompat : NotificationManagerCompat = NotificationManagerCompat.from(requireContext())
        binding.notificationSwitchCompat.isChecked = notificationManagerCompat.areNotificationsEnabled()
    }

    private fun openNotificationSetting(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName).apply {
                    startActivity(this)
                }
        }
    }
}