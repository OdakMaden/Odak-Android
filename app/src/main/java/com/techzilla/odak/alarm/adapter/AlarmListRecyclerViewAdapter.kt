package com.techzilla.odak.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemUserAlarmBinding
import com.techzilla.odak.shared.constants.timePatternYearMountDayHourMinuteSecond
import com.techzilla.odak.shared.model.AlarmDTO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmListRecyclerViewAdapter : RecyclerView.Adapter<AlarmListRecyclerViewAdapter.ViewHolder>(){

    private val alarmArrayList = ArrayList<AlarmDTO>()

    inner class ViewHolder(private val binding: ItemUserAlarmBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind (alarm: AlarmDTO){

            binding.aimPriceText.text = alarm.targetValue.toString()
            binding.nowPriceText.text = alarm.referenceValue.toString()

            val dateShow = Calendar.getInstance()
            val date = SimpleDateFormat(timePatternYearMountDayHourMinuteSecond).parse(alarm.creationTimeStamp.substring(0, alarm.creationTimeStamp.length-14))
            dateShow.time = date!!
            val termMinute = if (dateShow.get(Calendar.MINUTE)> 10){dateShow.get(Calendar.MINUTE).toString()}else{"0${dateShow.get(
                Calendar.MINUTE)}"}
            val termHour = if (dateShow.get(Calendar.HOUR_OF_DAY) > 10){dateShow.get(Calendar.HOUR_OF_DAY).toString()}else{"0${dateShow.get(
                Calendar.HOUR_OF_DAY)}"}
            //binding.subDate.text = "${termHour}:${termMinute}"

            binding.title.text = alarm.currencyCode
            binding.subTitle.text = alarm.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alarmArrayList[position])
    }

    override fun getItemCount(): Int {
        return alarmArrayList.size
    }

    fun addItemsToList(alarms : List<AlarmDTO>){
        alarmArrayList.addAll(alarms)
        notifyItemInserted(alarmArrayList.size)
    }

    fun addItemToList(alarm: AlarmDTO){
        alarmArrayList.add(alarm)
        notifyItemInserted(alarmArrayList.size)
    }
}