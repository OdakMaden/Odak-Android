package com.techzilla.odak.converter.viewcontrollers

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.converter.adapter.ItemPickerAdapter
import com.techzilla.odak.databinding.ActivityConverterBinding
import com.techzilla.odak.shared.constants.converterList
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.model.CurrencyModel
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.service.repository.MainRepository
import java.text.DecimalFormat

class ConverterActivity : AppCompatActivity() {
    private val binding : ActivityConverterBinding by lazy { ActivityConverterBinding.inflate(layoutInflater) }

    private val fromAdapter by lazy { ItemPickerAdapter() }
    private val toAdapter by lazy { ItemPickerAdapter() }
    private val decimalFormat = DecimalFormat("#.#####")

    private lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
/*
        mainRepository = MainRepository()
        //selectBottomItem(binding.dollarContainer, true)

        mainRepository.getExchangeRateList()
        mainRepository.exchangeRateListLiveData.observe(this, { list->

            selectBottomItem(binding.dollarContainer, true)


        })

 */
        binding.fromRecyclerview.adapter = fromAdapter
        binding.toRecyclerview.adapter = toAdapter

        fromAdapter.addNewItem(exchangeRateList)
        toAdapter.addNewItem(exchangeRateList)
        updateChangeText()
        binding.toPiecePrice.text = decimalFormat.format(toAdapter.getSelectedItem().sellingRate / fromAdapter.getSelectedItem().sellingRate)
        binding.resultPrice.text = decimalFormat.format(binding.fromPiece.text.toString().toDouble() * binding.toPiecePrice.text.toString().toDouble())

        binding.dollarContainer.setOnClickListener {
            selectBottomItem(binding.dollarContainer, false)
        }
        binding.goldBarsContainer.setOnClickListener {
            selectBottomItem(binding.goldBarsContainer, false)
        }
        binding.cryptoContainer.setOnClickListener {
            selectBottomItem(binding.cryptoContainer, false)
        }

        binding.backBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        //fromAdapter.addNewItem(converterList)
        //toAdapter.addNewItem(converterList)

        /*
        currencyModelForDetail?.let {
            fromAdapter.setFocusItemWithCode(CurrencyModel("TÜRK LİRASI", "TRY", 1.0, 1.0, "0", it.currencyType),
                binding.fromRecyclerview.layoutManager as LinearLayoutManager
            )
            toAdapter.setFocusItemWithCode(it,
                binding.toRecyclerview.layoutManager as LinearLayoutManager
            )
        }

         */

        binding.fromRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                        val offset = (binding.fromRecyclerview.height / itemHeight - 1) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (position+1 != fromAdapter.getSelectedPosition()){
                                position += offset
                                if (position in 0 until fromAdapter.getShowListSize()) {
                                    fromAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                            else if (lastPosition == fromAdapter.getSelectedPosition()){
                                if (position in 0 until fromAdapter.getShowListSize()) {
                                    fromAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                        }
                    }
                }
            }
        })

        binding.changeItemButton.setOnClickListener {
            val fromPosition = fromAdapter.getSelectedPosition()
            val toPosition = toAdapter.getSelectedPosition()
            fromAdapter.setPositionToItem(toPosition)
            toAdapter.setPositionToItem(fromPosition)
            resources?.getDimensionPixelSize(R.dimen.item_height)?.let {
                LinearLayoutManager::class.java.cast(binding.fromRecyclerview.layoutManager)?.let { fromLayoutManager ->
                    scrollToItem(it, fromLayoutManager, toPosition, binding.fromRecyclerview)
                }
                LinearLayoutManager::class.java.cast(binding.toRecyclerview.layoutManager)?.let { toLayoutManager ->
                    scrollToItem(it, toLayoutManager, fromPosition, binding.toRecyclerview)
                }
            }
            updateChangeText()
        }

        binding.toRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                        val offset = (binding.toRecyclerview.height / itemHeight - 1) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (position+1 != toAdapter.getSelectedPosition()){
                                position += offset
                                if (position in 0 until toAdapter.getShowListSize()) {
                                    toAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                            else if (lastPosition == toAdapter.getSelectedPosition()){
                                if (position in 0 until toAdapter.getShowListSize()) {
                                    toAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                        }
                    }
                }
            }
        })

        binding.fromPiece.addTextChangedListener { _it ->
            if (_it.toString() != ""){
                binding.resultPrice.text = decimalFormat.format(_it.toString().toDouble() * binding.toPiecePrice.text.toString().toDouble())
            }
            else{
                binding.resultPrice.text = decimalFormat.format(0 * binding.toPiecePrice.text.toString().toDouble())
            }
        }
    }


    private fun updateChangeText(){
        binding.toPiecePrice.text = decimalFormat.format(toAdapter.getSelectedItem().sellingRate / fromAdapter.getSelectedItem().sellingRate)
        binding.fromPiece.text.toString().let { fromPiece->
            if (fromPiece == ""){
                binding.resultPrice.text = decimalFormat.format(0 * binding.toPiecePrice.text.toString().toDouble())
            }else{
                binding.resultPrice.text = decimalFormat.format(fromPiece.toDouble() * binding.toPiecePrice.text.toString().toDouble())
            }
        }
    }

    private fun scrollToItem(itemHeight: Int, layoutManager : LinearLayoutManager, position:Int, recyclerView: RecyclerView) {
        val height = recyclerView.height
        if (height > 0) {
            layoutManager.scrollToPositionWithOffset(position,
                (height / 2 - itemHeight / 2)
            )
        }else{
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    resources?.getDimensionPixelSize(R.dimen.item_height)?.let {
                        layoutManager.scrollToPositionWithOffset(position, (height / 2 - it / 2))
                    }
                }
            })
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectBottomItem(constraintLayout: ConstraintLayout, isFirst:Boolean){
        when(constraintLayout){
            binding.dollarContainer->{
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar_selected, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                fromAdapter.changeType(CurrencyTypeEnum.Money)
                toAdapter.changeType(CurrencyTypeEnum.Money)
                if (!isFirst){
                    updateChangeText()
                }
            }
            binding.goldBarsContainer->{
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars_selected, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                fromAdapter.changeType(CurrencyTypeEnum.Metal)
                toAdapter.changeType(CurrencyTypeEnum.Metal)
                if (!isFirst){
                    updateChangeText()
                }
            }
            binding.cryptoContainer->{
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin_selected, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))

                fromAdapter.changeType(CurrencyTypeEnum.Crypto)
                toAdapter.changeType(CurrencyTypeEnum.Crypto)
                if (!isFirst){
                    updateChangeText()
                }
            }
        }
    }

    private fun getStatusBarHeight(): Int{
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId>0){
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}