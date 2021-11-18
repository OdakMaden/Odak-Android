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
    private val alarmArrayMap = HashMap<Int, Int>()

    inner class ViewHolder(private val binding: ItemUserAlarmBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind (alarm: AlarmDTO, listener: AlarmListMenuListener, nowExchangeRateDTO: ExchangeRateDTO){
            val decimalFormat = DecimalFormat("#.####")
            if (alarm.alarmType == AlarmTypeEnum.PriceOver || alarm.alarmType == AlarmTypeEnum.PriceUnder){
                binding.aimPriceText.text = reformForDoubleToString(alarm.targetValue.toString())
                binding.nowPriceText.text = reformForDoubleToString(decimalFormat.format(nowExchangeRateDTO.sellingRate))
            }
            else{
                binding.aimPriceText.text = reformForDoubleToString(decimalFormat.format(alarm.referenceValue * (1 + alarm.targetValue / 100)))
                binding.nowPriceText.text = reformForDoubleToString(decimalFormat.format(nowExchangeRateDTO.sellingRate))
            }
            binding.title.text = alarm.currencyCode
            binding.subTitle.text = alarm.name.replaceFirst(" ", "\n")

            binding.itemMenu.setOnClickListener {
                listener.alarmListMenuClick(alarm)
            }
        }


        private fun reformForDoubleToString(priceString:String):String{
            var result = ""
            if (priceString.length >7) {
                result = priceString.subSequence(0,7).toString()
                if (result.last() == '.'){
                    result.replace(".","")
                }
            } else {
                result=priceString
            }
            return result
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
        alarms.forEach {
            if (!alarmArrayList.contains(it)){
                alarmArrayList.add(it)
                alarmArrayMap[it.rID] = alarmArrayList.indexOf(it)
                notifyItemInserted(alarmArrayList.size)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItemToList(alarm: AlarmDTO){
        if (alarmArrayMap[alarm.rID] != null){
            alarmArrayList.removeAt(alarmArrayMap[alarm.rID]!!)
            alarmArrayList.add(alarmArrayMap[alarm.rID]!!, alarm)
            notifyItemChanged(alarmArrayMap[alarm.rID]!!)
        }
        else {
            if(alarmArrayList.add(alarm)){
                alarmArrayMap[alarm.rID] = alarmArrayList.size - 1
                notifyItemInserted(alarmArrayList.size)
            }
        }
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