package com.techzilla.odak.main.adapters

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ItemInnerViewBinding
import com.techzilla.odak.shared.model.InnerViewCurrencyModel

class InnerViewRecyclerViewAdapter(private val listener: InnerViewListener, private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.Adapter<InnerViewRecyclerViewAdapter.ViewHolder>() {

    private val favoriteArrayList = ArrayList<InnerViewCurrencyModel>()
    private val dollarArrayList = ArrayList<InnerViewCurrencyModel>()
    private val goldBarArrayList = ArrayList<InnerViewCurrencyModel>()
    private val cryptoArrayList = ArrayList<InnerViewCurrencyModel>()
    private val arrayList = ArrayList<InnerViewCurrencyModel>()

    class ViewHolder(private val binding: ItemInnerViewBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(innerViewCurrencyModel: InnerViewCurrencyModel){
            if (innerViewCurrencyModel.isSelected){
                binding.container.setContentPadding(0,15,0,15)
                binding.menuContainer.visibility = VISIBLE
            }
            else{
                binding.container.setContentPadding(0,0,0,0)
                binding.menuContainer.visibility = GONE
            }

            binding.title.text = innerViewCurrencyModel.currencyModel.currencyCode
            binding.subTitle.text = innerViewCurrencyModel.currencyModel.currencyName
            binding.buyText.text = innerViewCurrencyModel.currencyModel.buyPrice
            binding.sellText.text = innerViewCurrencyModel.currencyModel.salePrice
            if (innerViewCurrencyModel.currencyModel.percentage.contains("-")){
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_down, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_red, binding.root.resources.newTheme()))
            }
            else{
                binding.increaseImage.setImageDrawable(binding.root.resources.getDrawable(R.drawable.icon_increase_up, binding.root.resources.newTheme()))
                binding.increaseText.setTextColor(binding.root.resources.getColor(R.color.odak_green, binding.root.resources.newTheme()))
            }
            binding.increaseText.text = innerViewCurrencyModel.currencyModel.percentage

            binding.detail.setOnClickListener {
                println("detail")
            }
            binding.favorite.setOnClickListener {
                println("favorite")
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
        return ViewHolder(ItemInnerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList[position])
        holder.itemView.setOnClickListener {
            if (arrayList[position].isSelected){
                println(true)
            }
            else{
                listener.innerViewOnClickListener(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun insertNewParam(currencyModelList: List<InnerViewCurrencyModel>){
        currencyModelList.forEach {
            when (it.currencyModel.currencyType){
                0 ->{
                    dollarArrayList.add(it)
                }
                1 ->{
                    goldBarArrayList.add(it)
                }
                2 ->{
                    cryptoArrayList.add(it)
                }
                else ->{
                    favoriteArrayList.add(it)
                }
            }
        }
        arrayList.addAll(favoriteArrayList)
        notifyItemInserted(arrayList.size)
    }

    fun changeType(type : Int){
        when(type){
            0 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                dataSetChange(dollarArrayList)
                arrayList.addAll(dollarArrayList)
                notifyItemInserted(arrayList.size)
            }
            1 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                dataSetChange(goldBarArrayList)
                arrayList.addAll(goldBarArrayList)
                notifyItemInserted(arrayList.size)
            }
            2 ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                dataSetChange(cryptoArrayList)
                arrayList.addAll(cryptoArrayList)
                notifyItemInserted(arrayList.size)
            }
            else ->{
                notifyItemRangeRemoved(0, arrayList.size)
                arrayList.clear()
                dataSetChange(favoriteArrayList)
                arrayList.addAll(favoriteArrayList)
                notifyItemInserted(arrayList.size)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectItem(position: Int){
        val selected = arrayList.removeAt(position)
        arrayList[0].isSelected = false
        selected.isSelected = true
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

    private fun dataSetChange(list:List<InnerViewCurrencyModel>){
        list.forEach {
            it.isSelected = false
        }
    }

    interface InnerViewListener{
        fun innerViewOnClickListener(position: Int)
    }
}