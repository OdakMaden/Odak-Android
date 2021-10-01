package com.techzilla.odak.alarm.viewcontroller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techzilla.odak.databinding.FragmentAlarmBinding

class AlarmFragment : Fragment() {

    private val binding: FragmentAlarmBinding by lazy { FragmentAlarmBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAlarm.setOnClickListener {
            Intent(requireActivity(), AddAlarmActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.createAlarmButton.setOnClickListener {
            Intent(requireActivity(), AddAlarmActivity::class.java).apply {
                startActivity(this)
            }
        }
    }


}