package com.techzilla.odak.main.adapters

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ItemInnerViewBinding
import com.techzilla.odak.shared.model.CurrencyModel

class InnerViewRecyclerViewAdapter(private val listener: InnerViewListener, private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<InnerViewRecyclerViewAdapter.ViewHolder>() {

    private val favoriteArrayList = ArrayList<CurrencyModel>()
    private val dollarArrayList = ArrayList<CurrencyModel>()
    private val goldBarArrayList = ArrayList<CurrencyModel>()
    private val cryptoArrayList = ArrayList<CurrencyModel>()
    private val arrayList = ArrayList<CurrencyModel>()
    private var selectedModel: CurrencyModel? = null
    private var _type = 4

    class ViewHolder(private val binding: ItemInnerViewBinding, private val listener: InnerViewListener) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(currencyModel: CurrencyModel, isSelected:Boolean, isFavorite:Boolean){
            if (isSelected){
                binding.container.setContentPadding(0,15,0,15)
                binding.menuContainer.visibility = VISIBLE
            }
            else{
                binding.container.setContentPadding(0,0,0,0)
                binding.menuContainer.visibility = GONE
            }

            binding.title.text = currencyModel.currencyCode
            binding.subTitle.text = currencyModel.currencyName
            binding.buyText.text = currencyModel.buyPrice.toString()
            binding.sellText.text = currencyModel.salePrice.toString()
            if (isFavorite){
                binding.favorite.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_selected_favorite, binding.root.resources.newTheme()))
            }
            else{
                binding.favorite.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_favorite, binding.root.resources.newTheme()))
            }

            if (currencyModel.percentage.contains("-")){
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_down, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_red, binding.root.resources.newTheme()))
            }
            else{
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_up, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_green, binding.root.resources.newTheme()))
            }
            binding.increaseText.text = currencyModel.percentage

            binding.detail.setOnClickListener {
                listener.innerViewForDetailOnClickListener(currencyModel)
            }
            binding.favorite.setOnClickListener {
                listener.innerViewAddFavoriteOnClickListener(currencyModel, isFavorite)
            }
            binding.converter.setOnClickListener {
                println("converter")
            }
            binding.alarm.setOnClickListener {
                println("alarm")
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemInnerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isFavorite = false
        favoriteArrayList.forEach {
           if (it.currencyCode == arrayList[position].currencyCode){
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
                selectedModel = arrayList[0]
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun insertNewParam(currencyModelList: List<CurrencyModel>, favoriteArray: ArrayList<String>){
        currencyModelList.forEach {
            when (it.currencyType){
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
            favoriteArray.let { favoriteArray->
                if (favoriteArray.contains(it.currencyCode) && !favoriteArrayList.contains(it)){
                    favoriteArrayList.add(it)
                }
            }
        }
        arrayList.addAll(favoriteArrayList)
        notifyItemInserted(arrayList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFavorite(currencyModel: CurrencyModel){
        favoriteArrayList.add(currencyModel)
        notifyItemChanged(arrayList.indexOf(currencyModel))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFavorite(currencyModel: CurrencyModel){
        if (_type>2){
            favoriteArrayList.remove(currencyModel)
            arrayList.remove(currencyModel)
            notifyDataSetChanged()
        }
        else{
            favoriteArrayList.remove(currencyModel)
            notifyItemChanged(arrayList.indexOf(currencyModel))
        }
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
            else ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                arrayList.addAll(favoriteArrayList)
                notifyItemInserted(arrayList.size)
            }
        }
        selectedModel = null
        _type = type
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeItems(currencyCode:List<String>, currencyModel: CurrencyModel){
        val isFavorite = currencyCode.contains(currencyModel.currencyCode)
        if (isFavorite){
            addFavorite(currencyModel)
        }
        else{
            deleteFavorite(currencyModel)
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

    interface InnerViewListener{
        fun innerViewOnClickListener(position: Int)
        fun innerViewForDetailOnClickListener(currencyModel:CurrencyModel)
        fun innerViewAddFavoriteOnClickListener(currencyModel:CurrencyModel, isFavorite: Boolean)
    }
}