package com.techzilla.odak.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemAlarmBinding
import com.techzilla.odak.shared.model.CurrencyModel

class AddAlarmRecyclerviewAdapter(private val listener:AddAlarmListener) : RecyclerView.Adapter<AddAlarmRecyclerviewAdapter.ViewHolder>(){

    private val dollarArrayList = ArrayList<CurrencyModel>()
    private val goldBarArrayList = ArrayList<CurrencyModel>()
    private val cryptoArrayList = ArrayList<CurrencyModel>()
    private val arrayList = ArrayList<CurrencyModel>()
    private var selectedModel: CurrencyModel? = null
    private var _type = 0


    inner class ViewHolder(private val binding: ItemAlarmBinding): RecyclerView.ViewHolder(binding.root){
        fun bindHolder(currencyModel:CurrencyModel, isSelected:Boolean){
            binding.currencyCode.text = currencyModel.currencyCode
            binding.currencyName.text = currencyModel.currencyName

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
    fun insertParseList(fullData : List<CurrencyModel>){
        fullData.forEach {
            when(it.currencyType){
                0 ->{
                    dollarArrayList.add(it)
                }
                1 ->{
                    goldBarArrayList.add(it)
                }
                2 ->{
                    cryptoArrayList.add(it)
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
    fun selectItem(currencyModel: CurrencyModel){
        selectedModel = currencyModel
        notifyDataSetChanged()
    }

    private fun searchFunction(searchList: ArrayList<CurrencyModel>, text: String):ArrayList<CurrencyModel>{
        val result = ArrayList<CurrencyModel>()
        searchList.forEach {
            if (it.currencyCode.lowercase().contains(text) || it.currencyName.lowercase().contains(text)){
                result.add(it)
            }
        }
        return result
    }

    interface AddAlarmListener{
        fun addAlarmForDetail(currencyModel: CurrencyModel)
    }
}