package com.techzilla.odak.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ItemFavoriteBinding
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO
import java.text.DecimalFormat

class FavoriteRecyclerViewAdapter : RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder>() {

    private val favoriteMap = HashMap<String, ExchangeRateDTO>()
    private val favoritePositionMap = HashMap<Int, String>()
    private var lastCode = ""

    inner class ViewHolder(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun binding(exchangeRateDTO: ExchangeRateDTO, isNotLast:Boolean){
            binding.title.text = exchangeRateDTO.code

            if (exchangeRateDTO.currencyType == CurrencyTypeEnum.Crypto) {
                binding.price.text = DecimalFormat("#.##").format(exchangeRateDTO.sellingRate)
            } else {
                binding.price.text = DecimalFormat("#.####").format(exchangeRateDTO.sellingRate)
            }

            binding.root.resources.let {
                if (exchangeRateDTO.changePercentage < 0) {
                    binding.percent.setTextColor(it.getColor(R.color.odak_favorite_red, it.newTheme()))
                } else {
                    binding.percent.setTextColor(it.getColor(R.color.odak_favorite_green, it.newTheme()))
                }
            }
            binding.percent.text = "% ${DecimalFormat("#.##").format(exchangeRateDTO.changePercentage)}"

            if (isNotLast){
                binding.line.visibility = VISIBLE
            }
            else{
                binding.line.visibility = GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        favoriteMap[favoritePositionMap[position]]?.let {
            holder.binding(it, lastCode != it.code)
        }
    }

    override fun getItemCount(): Int {
        return favoriteMap.size
    }

    fun insertNewExchangeRateRecyclerView(exchangeRateDTOList : List<ExchangeRateDTO>, favoriteString: String){
        var index  = 0
        exchangeRateDTOList.forEach {
            favoriteString.let { favoriteString->
                if (favoriteString.contains(it.code)&& !favoriteMap.keys.contains(it.code)){
                    favoriteMap[it.code] = it
                    favoritePositionMap[index] = it.code
                    lastCode = it.code
                    index++
                }
            }
        }
        notifyItemInserted(favoriteMap.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFavorite(exchangeRateDTO:ExchangeRateDTO){
        favoritePositionMap[favoriteMap.size] = exchangeRateDTO.code
        favoriteMap[exchangeRateDTO.code] = exchangeRateDTO
        lastCode = exchangeRateDTO.code
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFavorite(exchangeRateDTO:ExchangeRateDTO){
        favoriteMap.remove(exchangeRateDTO.code)
        favoritePositionMap.clear()
        var index = 0
        favoriteMap.forEach {
            favoritePositionMap[index] = it.key
            lastCode = it.key
            index++
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeItems(exchangeRateDTOList : List<ExchangeRateDTO>){
        exchangeRateDTOList.forEach { exchangeRateDTO->
            if (favoriteMap[exchangeRateDTO.code] != null) {
                favoriteMap[exchangeRateDTO.code] = exchangeRateDTO
            }
        }
        notifyDataSetChanged()
    }
}