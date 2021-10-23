package com.techzilla.odak.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemUserAlarmBinding
import com.techzilla.odak.shared.constants.exchangeRateDTOListMap
import com.techzilla.odak.shared.model.AlarmDTO
import com.techzilla.odak.shared.model.AlarmTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO
import java.text.DecimalFormat

class AlarmListRecyclerViewAdapter (private val listener : AlarmListMenuListener) : RecyclerView.Adapter<AlarmListRecyclerViewAdapter.ViewHolder>(){

    private val alarmArrayList = ArrayList<AlarmDTO>()

    inner class ViewHolder(private val binding: ItemUserAlarmBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind (alarm: AlarmDTO, listener: AlarmListMenuListener, nowExchangeRateDTO: ExchangeRateDTO){
            val decimalFormat = DecimalFormat("#.####")
            if (alarm.alarmType == AlarmTypeEnum.PriceOver || alarm.alarmType == AlarmTypeEnum.PriceUnder){
                binding.aimPriceText.text = alarm.targetValue.toString()
                binding.nowPriceText.text = decimalFormat.format(nowExchangeRateDTO.sellingRate)
            }
            else{
                binding.aimPriceText.text = decimalFormat.format(alarm.referenceValue * (1 + alarm.targetValue / 100))
                binding.nowPriceText.text = decimalFormat.format(nowExchangeRateDTO.sellingRate)
            }
            //binding.aimPriceText.text = alarm.targetValue.toString()
            //binding.nowPriceText.text = alarm.referenceValue.toString()

            /*
            val dateShow = Calendar.getInstance()
            val date = SimpleDateFormat(timePatternYearMountDayHourMinuteSecond).parse(alarm.creationTimeStamp.substring(0, alarm.creationTimeStamp.length-14))
            dateShow.time = date!!
            val termMinute = if (dateShow.get(Calendar.MINUTE)> 10){dateShow.get(Calendar.MINUTE).toString()}else{"0${dateShow.get(
                Calendar.MINUTE)}"}
            val termHour = if (dateShow.get(Calendar.HOUR_OF_DAY) > 10){dateShow.get(Calendar.HOUR_OF_DAY).toString()}else{"0${dateShow.get(
                Calendar.HOUR_OF_DAY)}"}

             */
            //binding.subDate.text = "${termHour}:${termMinute}"
            binding.title.text = alarm.currencyCode
            binding.subTitle.text = alarm.name

            binding.itemMenu.setOnClickListener {
                listener.alarmListMenuClick(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alarmArrayList[position], listener, exchangeRateDTOListMap[alarmArrayList[position].currencyCode]!!)
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

    fun deleteItem(alarm: AlarmDTO){
        notifyItemRemoved(alarmArrayList.indexOf(alarm))
        alarmArrayList.remove(alarm)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun alarmUpdatePrice(){
        notifyDataSetChanged()
    }

    interface AlarmListMenuListener{
        fun alarmListMenuClick(alarmDTO: AlarmDTO)
    }
}