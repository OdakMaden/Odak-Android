package com.techzilla.odak.alarm.viewcontroller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techzilla.odak.databinding.FragmentAlarmDefaultMenuBinding

class AlarmDefaultMenuFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAlarmDefaultMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmDefaultMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.editButton.setOnClickListener {  }
        binding.deleteButton.setOnClickListener {  }

        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}