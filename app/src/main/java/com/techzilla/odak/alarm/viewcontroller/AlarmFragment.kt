package com.techzilla.odak.alarm.viewcontroller

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.techzilla.odak.alarm.adapter.AlarmListRecyclerViewAdapter
import com.techzilla.odak.alarm.constant.alarmDTO
import com.techzilla.odak.databinding.FragmentAlarmBinding
import com.techzilla.odak.shared.model.AlarmDTO
import com.techzilla.odak.shared.service.repository.AlarmRepository

class AlarmFragment : Fragment(), AlarmListRecyclerViewAdapter.AlarmListMenuListener {

    private val binding: FragmentAlarmBinding by lazy { FragmentAlarmBinding.inflate(layoutInflater) }

    private val adapter by lazy { AlarmListRecyclerViewAdapter(this) }

    private lateinit var repository: AlarmRepository

    private val alarmUpdateTimer = object : CountDownTimer(3000, 3000){
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            adapter.alarmUpdatePrice()
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            if (alarmDTO != null){
                binding.listContainer.visibility = View.VISIBLE
                binding.firstAlarmContainer.visibility = View.GONE
                adapter.addItemToList(alarmDTO!!)
                alarmDTO = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alarmRecyclerview.adapter = adapter
        repository = AlarmRepository()
        repository.getAlarms()

        repository.alarmsListLiveData.observe(requireActivity(), {
            if (it.isNotEmpty()){
                binding.listContainer.visibility = View.VISIBLE
                binding.firstAlarmContainer.visibility = View.GONE
                adapter.addItemsToList(it)
                alarmUpdateTimer.start()
            }
        })

        repository.alarmDeleteLiveData.observe(requireActivity(), {
            if (it != null){
                adapter.deleteItem(it)
            }
        })


        binding.addAlarm.setOnClickListener {
            startForResult.launch(
                Intent(requireActivity(), AddAlarmActivity::class.java)
            )
        }

        binding.createAlarmButton.setOnClickListener {
            startForResult.launch(
                Intent(requireActivity(), AddAlarmActivity::class.java)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmUpdateTimer.cancel()
    }

    override fun alarmListMenuClick(alarmDTO: AlarmDTO) {
        val alarmDefaultMenuFragment = AlarmDefaultMenuFragment(alarmDTO, repository, startForResult)
        alarmDefaultMenuFragment.show(requireActivity().supportFragmentManager, AlarmDefaultMenuFragment.TAG)
    }
}