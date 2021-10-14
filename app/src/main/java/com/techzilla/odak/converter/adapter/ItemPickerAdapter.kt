package com.techzilla.odak.converter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.databinding.ItemPickerElementBinding
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO

class ItemPickerAdapter (private val adapterType:Int) : RecyclerView.Adapter<ItemPickerAdapter.ViewHolder>() {

    private val dollarArrayList = ArrayList<ExchangeRateDTO>()
    private val goldBarArrayList = ArrayList<ExchangeRateDTO>()
    private val cryptoArrayList = ArrayList<ExchangeRateDTO>()
    private val arrayList = ArrayList<ExchangeRateDTO>()
    private var _type : CurrencyTypeEnum = CurrencyTypeEnum.Money
    private var selectedPosition = 1
   // private var _layoutManager:RecyclerView.LayoutManager? = null

    class ViewHolder(private val binding: ItemPickerElementBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(converterCurrency: ExchangeRateDTO, isSelected: Boolean){
            binding.currencyCode.text = converterCurrency.code
            binding.currencyName.text = converterCurrency.name

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

    fun addNewItem(currencies: List<ExchangeRateDTO>){
        currencies.forEach {
            when(it.currencyType){
                CurrencyTypeEnum.Money ->{
                    dollarArrayList.add(it)
                }
                CurrencyTypeEnum.Metal ->{
                    goldBarArrayList.add(it)
                }
                CurrencyTypeEnum.Crypto ->{
                    cryptoArrayList.add(it)
                }
                CurrencyTypeEnum.Parity ->{
                    dollarArrayList.add(it)
                }
            }
        }
        dollarArrayList.add(0, ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Money, "", 0.0f,0.0f))
        dollarArrayList.add(ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Money, "", 0.0f,0.0f))
        goldBarArrayList.add(0, ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Metal, "", 0.0f,0.0f))
        goldBarArrayList.add(ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Metal, "", 0.0f,0.0f))
        cryptoArrayList.add(0,ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Crypto, "", 0.0f,0.0f))
        cryptoArrayList.add(ExchangeRateDTO(" ", " ","",CurrencyTypeEnum.Crypto, "", 0.0f,0.0f))
        if (adapterType == 0){
            dollarArrayList.add(dollarArrayList.size - 1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Money, "", 1.0f, 1.0f))
            goldBarArrayList.add(goldBarArrayList.size - 1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Metal, "", 1.0f, 1.0f))
            cryptoArrayList.add(cryptoArrayList.size - 1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Crypto, "", 1.0f, 1.0f))
        }
        else{
            dollarArrayList.add(1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Money, "", 1.0f, 1.0f))
            goldBarArrayList.add(1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Metal, "", 1.0f, 1.0f))
            cryptoArrayList.add(1, ExchangeRateDTO("TRY", "TRY","Türk Lirası",CurrencyTypeEnum.Crypto, "", 1.0f, 1.0f))
        }
        arrayList.addAll(dollarArrayList)
        notifyItemInserted(dollarArrayList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPositionToItem(position: Int): ExchangeRateDTO{
        if (position != 0 || position != arrayList.size-1){
            selectedPosition = position
            notifyDataSetChanged()
        }
        return arrayList[position]
    }

    fun getSelectedPosition():Int{
        return selectedPosition
    }

    fun getSelectedItem():ExchangeRateDTO{
        return arrayList[selectedPosition]
    }

    fun getShowListSize():Int{
        return arrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFocusItemWithCode(forItem:ExchangeRateDTO, layoutManager: LinearLayoutManager){
        changeType(forItem.currencyType)

        selectedPosition = arrayList.indexOf(forItem)
        layoutManager.scrollToPosition(arrayList.indexOf(forItem) - 1)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeType(type : CurrencyTypeEnum){
        when(type){
            CurrencyTypeEnum.Money ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(dollarArrayList)
                notifyItemInserted(arrayList.size)
            }
            CurrencyTypeEnum.Metal ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(goldBarArrayList)
                notifyItemInserted(arrayList.size)
            }
            CurrencyTypeEnum.Crypto ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(cryptoArrayList)
                notifyItemInserted(arrayList.size)
            }
            CurrencyTypeEnum.Parity ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(dollarArrayList)
                notifyItemInserted(arrayList.size)
            }
        }
        selectedPosition = 1
        _type = type
    }
}