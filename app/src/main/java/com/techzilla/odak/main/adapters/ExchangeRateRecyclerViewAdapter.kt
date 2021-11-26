package com.techzilla.odak.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ItemInnerViewBinding
import com.techzilla.odak.shared.constants.toDateFormatISO8601
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap

class ExchangeRateRecyclerViewAdapter (private val listener: ExchangeRateRecyclerViewListener) : RecyclerView.Adapter<ExchangeRateRecyclerViewAdapter.ViewHolder>() {

    private val dollarMap = HashMap<String, ExchangeRateDTO>()
    private val metalMap = HashMap<String, ExchangeRateDTO>()
    private val cryptoMap = HashMap<String, ExchangeRateDTO>()
    private val favoriteMap = HashMap<String, ExchangeRateDTO>()

    private val showMap = HashMap<String, ExchangeRateDTO>()
    private val showPositionMap = HashMap<Int, String>()

    private var selectedPosition: Int = -1
    private var selectedType = -1


    inner class ViewHolder(private val binding: ItemInnerViewBinding, private val listener: ExchangeRateRecyclerViewListener) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(exchangeRateDTO: ExchangeRateDTO, isSelected: Boolean, isFavorite: Boolean) {
            if (isSelected) {
                binding.container.setContentPadding(0, 15, 0, 15)
                binding.menuContainer.visibility = View.VISIBLE
            } else {
                binding.container.setContentPadding(0, 0, 0, 0)
                binding.menuContainer.visibility = View.GONE
            }

            binding.title.text = exchangeRateDTO.code
            binding.subTitle.text = exchangeRateDTO.name
            if (exchangeRateDTO.currencyType == CurrencyTypeEnum.Crypto) {
                binding.buyText.text = DecimalFormat("#.##").format(exchangeRateDTO.buyingRate)
                binding.sellText.text = DecimalFormat("#.##").format(exchangeRateDTO.sellingRate)
            } else {
                binding.buyText.text = DecimalFormat("#.####").format(exchangeRateDTO.buyingRate)
                binding.sellText.text = DecimalFormat("#.####").format(exchangeRateDTO.sellingRate)
            }
            binding.root.resources.let {
                if (isFavorite) {
                    binding.favorite.setImageDrawable(it.getDrawable(R.drawable.icon_selected_favorite, it.newTheme()))
                } else {
                    binding.favorite.setImageDrawable(it.getDrawable(R.drawable.icon_favorite, it.newTheme()))
                }

                if (exchangeRateDTO.changePercentage < 0) {
                    binding.increaseImage.setImageDrawable(it.getDrawable(R.drawable.icon_increase_down, it.newTheme()))
                    binding.increaseText.setTextColor(it.getColor(R.color.odak_red, it.newTheme()))
                } else {
                    binding.increaseImage.setImageDrawable(it.getDrawable(R.drawable.icon_increase_up, it.newTheme()))
                    binding.increaseText.setTextColor(it.getColor(R.color.odak_green, it.newTheme()))
                }
            }

            binding.increaseText.text = "% ${DecimalFormat("#.##").format(exchangeRateDTO.changePercentage)}"

            val calendar = exchangeRateDTO.lastChangeTimeStamp.toDateFormatISO8601()
            calendar?.let { calendarTerm ->
                calendarTerm.add(Calendar.HOUR_OF_DAY, -3)
                val termMinute = if (calendarTerm.get(Calendar.MINUTE) >= 10) {
                    calendarTerm.get(Calendar.MINUTE).toString()
                } else {
                    "0${calendarTerm.get(Calendar.MINUTE)}"
                }
                val termHour = if (calendarTerm.get(Calendar.HOUR_OF_DAY) >= 10) {
                    calendarTerm.get(Calendar.HOUR_OF_DAY).toString()
                } else {
                    "0${calendarTerm.get(Calendar.HOUR_OF_DAY)}"
                }
                binding.subDate.text = "${termHour}:${termMinute}"
            }

            binding.detail.setOnClickListener {
                listener.exchangeRateRecyclerViewItemForDetailOnClickListener(exchangeRateDTO)
            }
            binding.favorite.setOnClickListener {
                listener.exchangeRateRecyclerViewItemAddFavoriteOnClickListener(exchangeRateDTO, isFavorite)
            }
            binding.alarm.setOnClickListener {
                listener.exchangeRateRecyclerViewItemAlarmOnClickListener(exchangeRateDTO)
            }
            binding.converter.setOnClickListener {
                listener.exchangeRateRecyclerViewItemConverterOnClickListener(exchangeRateDTO)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemInnerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        showMap[showPositionMap[position]]?.let {
            holder.bind(it, selectedPosition == position, favoriteMap.keys.contains(it.code))
        }

        holder.itemView.setOnClickListener {
            if (selectedPosition == position){
                showMap[showPositionMap[position]]?.let {
                    listener.exchangeRateRecyclerViewItemForDetailOnClickListener(it)
                }
            }
            else{
                listener.exchangeRateRecyclerViewItemOnClickListener(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return showMap.size
    }

    fun insertNewExchangeRateRecyclerView(exchangeRateDTOList : List<ExchangeRateDTO>, favoriteString: String){
        exchangeRateDTOList.forEach {
            when (it.currencyType){
                CurrencyTypeEnum.Money->{
                    dollarMap[it.code] = it
                }
                CurrencyTypeEnum.Metal ->{
                    metalMap[it.code] = it
                }
                CurrencyTypeEnum.Crypto ->{
                    cryptoMap[it.code] = it
                }
                CurrencyTypeEnum.Parity ->{
                    dollarMap[it.code] = it
                }
            }
            favoriteString.let { favoriteString->
                if (favoriteString.contains(it.code)&& !favoriteMap.keys.contains(it.code)){
                    favoriteMap[it.code] = it
                }
            }
        }
        var index  = 0
        favoriteMap.forEach {
            showMap[it.key] = it.value
            showPositionMap[index] = it.key
            index++
        }
        notifyItemInserted(showMap.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectItem(position: Int){
        selectedPosition = position
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeType(type : Int){
        var index = 0
        when(type){
             0->{
                 notifyItemRangeRemoved(0, showMap.size)
                 showMap.clear()
                 dollarMap.forEach{
                     showMap[it.key] = it.value
                     showPositionMap[index] = it.key
                     index++
                 }
                 notifyItemInserted(showMap.size)
            }
            1 ->{
                notifyItemRangeRemoved(0, showMap.size)
                showMap.clear()
                metalMap.forEach{
                    showMap[it.key] = it.value
                    showPositionMap[index] = it.key
                    index++
                }
                notifyItemInserted(showMap.size)
            }
            2 ->{
                notifyItemRangeRemoved(0, showMap.size)
                showMap.clear()
                cryptoMap.forEach{
                    showMap[it.key] = it.value
                    showPositionMap[index] = it.key
                    index++
                }
                notifyItemInserted(showMap.size)
            }
            else ->{
                notifyItemRangeRemoved(0, showMap.size)
                showMap.clear()
                favoriteMap.forEach{
                    showMap[it.key] = it.value
                    showPositionMap[index] = it.key
                    index++
                }
                notifyItemInserted(showMap.size)
            }
        }
        selectedPosition = -1
        selectedType = type
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeItems(exchangeRateDTOList : List<ExchangeRateDTO>){
        exchangeRateDTOList.forEach { exchangeRateDTO->
            when(exchangeRateDTO.currencyType){
                CurrencyTypeEnum.Money ->{
                    dollarMap[exchangeRateDTO.code] = exchangeRateDTO
                }
                CurrencyTypeEnum.Metal -> {
                    metalMap[exchangeRateDTO.code] = exchangeRateDTO
                }
                CurrencyTypeEnum.Crypto -> {
                    cryptoMap[exchangeRateDTO.code] = exchangeRateDTO
                }
                CurrencyTypeEnum.Parity ->{
                    dollarMap[exchangeRateDTO.code] = exchangeRateDTO
                }
            }

            if (showMap[exchangeRateDTO.code] != null) {
                showMap[exchangeRateDTO.code] = exchangeRateDTO
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFavorite(exchangeRateDTO: ExchangeRateDTO){
        favoriteMap[exchangeRateDTO.code] = exchangeRateDTO
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFavorite(exchangeRateDTO: ExchangeRateDTO){
        if (selectedType == -1){
            favoriteMap.remove(exchangeRateDTO.code)
            showMap.remove(exchangeRateDTO.code)
            showPositionMap.clear()
            var index = 0
            showMap.forEach {
                showPositionMap[index] = it.key
                index++
            }
        }
        else{
            favoriteMap.remove(exchangeRateDTO.code)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchItems(text: String){
        notifyItemRangeRemoved(0, showMap.size)
        showMap.clear()
        showPositionMap.clear()
        val result = when (selectedType){
            0 ->{
                searchFunction(dollarMap, text)
            }
            1 ->{
                searchFunction(metalMap, text)
            }
            2 ->{
                searchFunction(cryptoMap, text)
            }
            else ->{
                searchFunction(favoriteMap, text)
            }
        }
        var index = 0
        result.forEach{
            showMap[it.key] = it.value
            showPositionMap[index] = it.key
            index++
        }

        notifyDataSetChanged()
    }

    private fun searchFunction(searchList: HashMap<String,ExchangeRateDTO>, text: String):HashMap<String,ExchangeRateDTO>{
        val result = HashMap<String,ExchangeRateDTO>()
        searchList.forEach {
            if (it.key.lowercase().contains(text) || it.value.name.lowercase().contains(text)){
                result[it.key] = it.value
            }
        }
        return result
    }

    interface ExchangeRateRecyclerViewListener{
        fun exchangeRateRecyclerViewItemOnClickListener(position: Int)
        fun exchangeRateRecyclerViewItemForDetailOnClickListener(exchangeRateDTO:ExchangeRateDTO)
        fun exchangeRateRecyclerViewItemAddFavoriteOnClickListener(exchangeRateDTO:ExchangeRateDTO, isFavorite: Boolean)
        fun exchangeRateRecyclerViewItemAlarmOnClickListener(exchangeRateDTO:ExchangeRateDTO)
        fun exchangeRateRecyclerViewItemConverterOnClickListener(exchangeRateDTO:ExchangeRateDTO)
    }
}