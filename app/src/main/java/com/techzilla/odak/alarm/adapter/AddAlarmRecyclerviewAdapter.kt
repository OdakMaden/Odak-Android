package com.techzilla.odak.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemAlarmBinding
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO

class AddAlarmRecyclerviewAdapter(private val listener:AddAlarmListener) : RecyclerView.Adapter<AddAlarmRecyclerviewAdapter.ViewHolder>(){

    private val dollarArrayList = ArrayList<ExchangeRateDTO>()
    private val goldBarArrayList = ArrayList<ExchangeRateDTO>()
    private val cryptoArrayList = ArrayList<ExchangeRateDTO>()
    private val arrayList = ArrayList<ExchangeRateDTO>()
    private var selectedModel: ExchangeRateDTO? = null
    private var _type = 0


    inner class ViewHolder(private val binding: ItemAlarmBinding): RecyclerView.ViewHolder(binding.root){
        fun bindHolder(exchangeRateDTO:ExchangeRateDTO, isSelected:Boolean){
            binding.currencyCode.text = exchangeRateDTO.code
            binding.currencyName.text = exchangeRateDTO.name

            if (isSelected){
                binding.currencyCode.isSelected = true
                binding.currencyName.isSelected = true
                binding.container.isSelected = true
                binding.container.cardElevation = 10f
            }
            else{
                binding.currencyCode.isSelected = false
                binding.currencyName.isSelected = false
                binding.container.isSelected = false
                binding.container.cardElevation = 0f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        arrayList[position].let {
            holder.bindHolder(it, it == selectedModel)
        }
        holder.itemView.setOnClickListener {
            listener.addAlarmForDetail(arrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertParseList(fullData : List<ExchangeRateDTO>){
        fullData.forEach {
            when(it.currencyType){
                CurrencyTypeEnum.Money ->{
                    if (!dollarArrayList.contains(it)) {
                        dollarArrayList.add(it)
                    }
                }
                CurrencyTypeEnum.Metal ->{
                    if (!goldBarArrayList.contains(it)) {
                        goldBarArrayList.add(it)
                    }
                }
                CurrencyTypeEnum.Crypto ->{
                    if (!cryptoArrayList.contains(it)) {
                        cryptoArrayList.add(it)
                    }
                }
                CurrencyTypeEnum.Parity ->{
                    if (!dollarArrayList.contains(it)) {
                        dollarArrayList.add(it)
                    }
                }
            }
        }
        arrayList.addAll(dollarArrayList)
        notifyItemInserted(arrayList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeType(type : Int){
        when(type){
            0 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(dollarArrayList)
                notifyItemInserted(arrayList.size)
            }
            1 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(goldBarArrayList)
                notifyItemInserted(arrayList.size)
            }
            2 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(cryptoArrayList)
                notifyItemInserted(arrayList.size)
            }
        }
        selectedModel = null
        _type = type
    }

    fun searchItems(text: String){
        notifyItemRangeRemoved(0, arrayList.size)
        arrayList.clear()
        when (_type){
            0 ->{
                arrayList.addAll(searchFunction(dollarArrayList, text))
            }
            1 ->{
                arrayList.addAll(searchFunction(goldBarArrayList, text))
            }
            2 ->{
                arrayList.addAll(searchFunction(cryptoArrayList, text))
            }
        }
        notifyItemInserted(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectItem(exchangeRateDTO: ExchangeRateDTO){
        selectedModel = exchangeRateDTO
        notifyDataSetChanged()
    }

    private fun searchFunction(searchList: ArrayList<ExchangeRateDTO>, text: String):ArrayList<ExchangeRateDTO>{
        val result = ArrayList<ExchangeRateDTO>()
        searchList.forEach {
            if (it.code.lowercase().contains(text) || it.name.lowercase().contains(text)){
                result.add(it)
            }
        }
        return result
    }

    interface AddAlarmListener{
        fun addAlarmForDetail(exchangeRateDTO: ExchangeRateDTO)
    }
}