package com.techzilla.odak.alarm.viewcontroller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techzilla.odak.alarm.constant.alarmDTO
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.databinding.FragmentAlarmDefaultMenuBinding
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.model.AlarmDTO
import com.techzilla.odak.shared.service.repository.AlarmRepository

class AlarmDefaultMenuFragment
    (private val _alarmDTO: AlarmDTO, private val repository: AlarmRepository, private val startForResult: ActivityResultLauncher<Intent>)
    : BottomSheetDialogFragment() {

    private var _binding: FragmentAlarmDefaultMenuBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val TAG = "AlarmDefaultMenuFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmDefaultMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editButton.setOnClickListener {
            startForResult.launch(
                Intent(requireActivity(), AlarmDetailActivity::class.java).apply {
                    putExtra("edit",true)
                }.also {
                    exchangeRateList.forEach {
                        if (it.code == _alarmDTO.currencyCode){
                            exchangeRateDTOForDetail = it
                            alarmDTO = _alarmDTO
                            return@forEach
                        }
                    }
                }
            )
            dialog?.dismiss()
        }

        binding.deleteButton.setOnClickListener {
            repository.deleteAlarm(_alarmDTO)
            dialog?.dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dialog?.cancel()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}