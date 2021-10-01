package com.techzilla.odak.converter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemPickerElementBinding
import com.techzilla.odak.shared.model.ConverterCurrency

class ItemPickerAdapter : RecyclerView.Adapter<ItemPickerAdapter.ViewHolder>() {

    private val dollarArrayList = ArrayList<ConverterCurrency>()
    private val goldBarArrayList = ArrayList<ConverterCurrency>()
    private val cryptoArrayList = ArrayList<ConverterCurrency>()
    private val arrayList = ArrayList<ConverterCurrency>()
    private var _type = 0
    private var selectedPosition = 1

    class ViewHolder(private val binding: ItemPickerElementBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(converterCurrency: ConverterCurrency, isSelected: Boolean){
            binding.currencyCode.text = converterCurrency.currencyCode
            binding.currencyName.text = converterCurrency.currencyName

            if (isSelected){
                binding.currencyName.isSelected = true
                binding.currencyCode.isSelected = true
            }
            else{
                binding.currencyName.isSelected = false
                binding.currencyCode.isSelected = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPickerElementBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList[position], selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun addNewItem(currencies: List<ConverterCurrency>){
        currencies.forEach {
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
        dollarArrayList.add(0, ConverterCurrency(" ", " ",0.0, 0))
        dollarArrayList.add(ConverterCurrency(" ", " ",0.0, 0))
        goldBarArrayList.add(0, ConverterCurrency(" ", " ",0.0, 1))
        goldBarArrayList.add(ConverterCurrency(" ", " ",0.0, 1))
        cryptoArrayList.add(0,ConverterCurrency(" ", " ",0.0, 2))
        cryptoArrayList.add(ConverterCurrency(" ", " ",0.0, 2))

        arrayList.addAll(dollarArrayList)
        notifyItemInserted(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPositionToItem(position: Int): ConverterCurrency{
        if (position != 0 || position != arrayList.size-1){
            selectedPosition = position
            notifyDataSetChanged()
        }
        return arrayList[position]
    }

    fun getSelectedPosition():Int{
        return selectedPosition
    }

    fun getSelectedItem():ConverterCurrency{
        return arrayList[selectedPosition]
    }

    fun getShowListSize():Int{
        return arrayList.size
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
        selectedPosition = 1
        _type = type
    }
}