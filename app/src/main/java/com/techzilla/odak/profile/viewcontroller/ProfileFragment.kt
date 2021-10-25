package com.techzilla.odak.profile.viewcontroller

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.techzilla.odak.R
import com.techzilla.odak.auth.viewcontroller.LoginActivity
import com.techzilla.odak.databinding.FragmentProfileBinding
import com.techzilla.odak.main.viewcontrollers.MarketFragment
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.helper_interface.MenuButtonListener

class ProfileFragment constructor(private val listener: MenuButtonListener) : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener.menuVisible(View.GONE)

        rememberMemberDTO?.let {
            binding.nameText.text = it.firstName
            binding.name.text = it.firstName
            binding.surname.text = it.lastName
            binding.email.text = it.email

        }


        binding.logoutButton.setOnClickListener {
            binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE
            val sharedPref = requireActivity().getSharedPreferences(resources.getString(R.string.Odak_shared_pref), MODE_PRIVATE)
            with(sharedPref.edit()){
                putBoolean(resources.getString(R.string.isLogin), false)
                apply()
            }
            Intent(requireActivity(), LoginActivity::class.java).apply {
                binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE
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

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(MarketFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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