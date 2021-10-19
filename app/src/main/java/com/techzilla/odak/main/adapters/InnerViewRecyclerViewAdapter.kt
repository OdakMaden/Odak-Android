package com.techzilla.odak.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ItemInnerViewBinding
import com.techzilla.odak.shared.constants.timePatternYearMountDayHourMinuteSecond
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.model.ExchangeRateDTO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InnerViewRecyclerViewAdapter(private val listener: InnerViewListener, private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<InnerViewRecyclerViewAdapter.ViewHolder>() {

    private val favoriteArrayList = ArrayList<ExchangeRateDTO>()
    private val dollarMapList = HashMap<String, Int>()
    private val dollarArrayList = ArrayList<ExchangeRateDTO>()
    private val goldBarMapList = HashMap<String, Int>()
    private val goldBarArrayList = ArrayList<ExchangeRateDTO>()
    private val cryptoMapList = HashMap<String, Int>()
    private val cryptoArrayList = ArrayList<ExchangeRateDTO>()

    private val arrayListMap = HashMap<String, Int>()
    private val arrayList = ArrayList<ExchangeRateDTO>()
    private var selectedModel: ExchangeRateDTO? = null
    private var _type = 4

    class ViewHolder(private val binding: ItemInnerViewBinding, private val listener: InnerViewListener) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "SimpleDateFormat")
        fun bind(exchangeRateDTO: ExchangeRateDTO, isSelected:Boolean, isFavorite:Boolean){

            val percentage="0.0"

            if (isSelected){
                binding.container.setContentPadding(0,15,0,15)
                binding.menuContainer.visibility = VISIBLE
            }
            else{
                binding.container.setContentPadding(0,0,0,0)
                binding.menuContainer.visibility = GONE
            }

            binding.title.text = exchangeRateDTO.code
            binding.subTitle.text = exchangeRateDTO.name
            binding.buyText.text = exchangeRateDTO.buyingRate.toString()
            binding.sellText.text = exchangeRateDTO.sellingRate.toString()
            if (isFavorite){
                binding.favorite.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_selected_favorite, binding.root.resources.newTheme()))
            }
            else{
                binding.favorite.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_favorite, binding.root.resources.newTheme()))
            }

            if (percentage.contains("-")){
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_down, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_red, binding.root.resources.newTheme()))
            }
            else{
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_up, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_green, binding.root.resources.newTheme()))
            }
            binding.increaseText.text =  percentage//exchangeRateDTO.percentage

            val dateShow = Calendar.getInstance()
            val date = SimpleDateFormat(timePatternYearMountDayHourMinuteSecond).parse(exchangeRateDTO.lastChangeTimeStamp.substring(0, exchangeRateDTO.lastChangeTimeStamp.length-14))
            dateShow.time = date!!
            val termMinute = if (dateShow.get(Calendar.MINUTE)> 10){dateShow.get(Calendar.MINUTE).toString()}else{"0${dateShow.get(Calendar.MINUTE)}"}
            val termHour = if (dateShow.get(Calendar.HOUR_OF_DAY) > 10){dateShow.get(Calendar.HOUR_OF_DAY).toString()}else{"0${dateShow.get(Calendar.HOUR_OF_DAY)}"}
            binding.subDate.text = "${termHour}:${termMinute}"


            binding.detail.setOnClickListener {
                listener.innerViewForDetailOnClickListener(exchangeRateDTO)
            }
            binding.favorite.setOnClickListener {
                listener.innerViewAddFavoriteOnClickListener(exchangeRateDTO, isFavorite)
            }
            binding.converter.setOnClickListener {
                listener.innerViewConverterOnClickListener(exchangeRateDTO)
            }
            binding.alarm.setOnClickListener {
                listener.innerViewAlarmOnClickListener(exchangeRateDTO)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemInnerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isFavorite = false
        favoriteArrayList.forEach {
           if (it.code == arrayList[position].code){
               isFavorite = true
           }
        }
        holder.bind(arrayList[position], arrayList[position] == selectedModel, isFavorite)
        holder.itemView.setOnClickListener {
            if (arrayList[position] == selectedModel){
                listener.innerViewForDetailOnClickListener(arrayList[position])
            }
            else{
                listener.innerViewOnClickListener(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun insertNewParam(exchangeRateDTOList: List<ExchangeRateDTO>, favoriteString: String){
        exchangeRateDTOList.forEach {
            when (it.currencyType){
                CurrencyTypeEnum.Money->{
                    dollarArrayList.add(it)
                    dollarMapList[it.code] = dollarArrayList.indexOf(it)
                }
                CurrencyTypeEnum.Metal ->{
                    goldBarArrayList.add(it)
                    goldBarMapList[it.code] = goldBarArrayList.indexOf(it)
                }
                CurrencyTypeEnum.Crypto ->{
                    cryptoArrayList.add(it)
                    cryptoMapList[it.code] = cryptoArrayList.indexOf(it)
                }
                CurrencyTypeEnum.Parity ->{
                    dollarArrayList.add(it)
                    dollarMapList[it.code] = dollarArrayList.indexOf(it)
                }
            }
            favoriteString.let { favoriteString->
                if (favoriteString.contains(it.code) && !favoriteArrayList.contains(it)){
                    favoriteArrayList.add(it)
                }
            }
        }
        arrayList.addAll(favoriteArrayList)
        notifyItemInserted(arrayList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFavorite(exchangeRateDTO: ExchangeRateDTO){
        favoriteArrayList.add(exchangeRateDTO)
        notifyItemChanged(arrayList.indexOf(exchangeRateDTO))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFavorite(exchangeRateDTO: ExchangeRateDTO){
        if (_type>2){
            favoriteArrayList.remove(exchangeRateDTO)
            arrayList.remove(exchangeRateDTO)
            notifyDataSetChanged()
        }
        else{
            favoriteArrayList.remove(exchangeRateDTO)
            notifyItemChanged(arrayList.indexOf(exchangeRateDTO))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeType(type : Int){
        var term = 0
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
            else ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(favoriteArrayList)
                notifyItemInserted(arrayList.size)
            }
        }
        arrayListMap.clear()
        arrayList.forEach {
            arrayListMap[it.code] = term
            term++
        }
        selectedModel = null
        _type = type
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeItems(exchangeRateDTO: ExchangeRateDTO){
        when(exchangeRateDTO.currencyType){
            CurrencyTypeEnum.Money ->{
                dollarArrayList.removeAt(dollarMapList[exchangeRateDTO.code]!!)
                dollarArrayList.add(dollarMapList[exchangeRateDTO.code]!!, exchangeRateDTO)
            }
            CurrencyTypeEnum.Metal -> {
                goldBarArrayList.removeAt(goldBarMapList[exchangeRateDTO.code]!!)
                goldBarArrayList.add(goldBarMapList[exchangeRateDTO.code]!!, exchangeRateDTO)
            }
            CurrencyTypeEnum.Crypto -> {
                cryptoArrayList.removeAt(cryptoMapList[exchangeRateDTO.code]!!)
                cryptoArrayList.add(cryptoMapList[exchangeRateDTO.code]!!, exchangeRateDTO)
            }
            CurrencyTypeEnum.Parity ->{
                dollarArrayList.removeAt(dollarMapList[exchangeRateDTO.code]!!)
                dollarArrayList.add(dollarMapList[exchangeRateDTO.code]!!, exchangeRateDTO)
            }
        }
        val isShowList = arrayListMap[exchangeRateDTO.code]
        if (isShowList != null){
            arrayList.removeAt(isShowList)
            arrayList.add(isShowList, exchangeRateDTO)
            notifyDataSetChanged()
        }
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
            else ->{
                arrayList.addAll(searchFunction(favoriteArrayList, text))
            }
        }
        notifyItemInserted(0)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun selectItem(position: Int){
        /*
        val selected = arrayList.removeAt(position)
        arrayList.add(0, selected)
        notifyItemMoved(position, 0)
        layoutManager.scrollToPosition(0)
        object : CountDownTimer(200, 200){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                notifyDataSetChanged()
            }
        }.start()

         */
        selectedModel = arrayList[position]
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

    interface InnerViewListener{
        fun innerViewOnClickListener(position: Int)
        fun innerViewForDetailOnClickListener(exchangeRateDTO:ExchangeRateDTO)
        fun innerViewAddFavoriteOnClickListener(exchangeRateDTO:ExchangeRateDTO, isFavorite: Boolean)
        fun innerViewAlarmOnClickListener(exchangeRateDTO:ExchangeRateDTO)
        fun innerViewConverterOnClickListener(exchangeRateDTO:ExchangeRateDTO)
    }
}