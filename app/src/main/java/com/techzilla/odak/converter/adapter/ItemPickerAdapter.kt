package com.techzilla.odak.converter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.TEXT_ALIGNMENT_VIEW_END
import android.view.View.TEXT_ALIGNMENT_VIEW_START
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.converter.model.ConverterCurrency
import com.techzilla.odak.databinding.ItemPickerElementBinding

class ItemPickerAdapter(private val type : Int) : RecyclerView.Adapter<ItemPickerAdapter.ViewHolder>() {

    private val arrayList = ArrayList<ConverterCurrency>()
    private var selectedPosition = 1

    class ViewHolder(private val binding: ItemPickerElementBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(converterCurrency: ConverterCurrency, isSelected: Boolean, type:Int){
            if (type == 1){
                binding.currencyCode.textAlignment = TEXT_ALIGNMENT_VIEW_END
                binding.currencyName.textAlignment = TEXT_ALIGNMENT_VIEW_END
            }
            else{
                binding.currencyCode.textAlignment = TEXT_ALIGNMENT_VIEW_START
                binding.currencyName.textAlignment = TEXT_ALIGNMENT_VIEW_START
            }
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
        holder.bind(arrayList[position], selectedPosition == position, type)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun addNewItem(currencies: List<ConverterCurrency>){
        arrayList.addAll(currencies)
        notifyItemInserted(arrayList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPositionToItem(position: Int): ConverterCurrency{
        selectedPosition = position
        notifyDataSetChanged()
        return arrayList[position]
    }

    fun getSelectedPosition():Int{
        return selectedPosition
    }

    fun getSelectedItem():ConverterCurrency{
        return arrayList[selectedPosition]
    }
}