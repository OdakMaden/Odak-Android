package com.techzilla.odak.converter.viewcontrollers

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.converter.adapter.ItemPickerAdapter
import com.techzilla.odak.databinding.ActivityConverterBinding
import com.techzilla.odak.shared.constants.exchangeRateDTOListMap
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import java.text.DecimalFormat

class ConverterActivity : AppCompatActivity() , ItemPickerAdapter.ChangeTypeListener{
    private val binding : ActivityConverterBinding by lazy { ActivityConverterBinding.inflate(layoutInflater) }

    private val fromAdapter by lazy { ItemPickerAdapter(0, this) }
    private val toAdapter by lazy { ItemPickerAdapter(1, this) }
    private val decimalFormat = DecimalFormat("#.#####")
    private var isCrypto : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            //window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        selectBottomItem(binding.dollarContainer, true)
        binding.fromRecyclerview.adapter = fromAdapter
        binding.toRecyclerview.adapter = toAdapter

        fromAdapter.addNewItem(exchangeRateList)
        toAdapter.addNewItem(exchangeRateList)

        exchangeRateDTOForDetail?.let {
            if (it.currencyType == CurrencyTypeEnum.Parity){
                bottomMenu(CurrencyTypeEnum.Money)
                fromAdapter.changeType(CurrencyTypeEnum.Money)
                toAdapter.changeType(CurrencyTypeEnum.Money)
                fromAdapter.setFocusItemWithCodeForParity(it, binding.fromRecyclerview.layoutManager as LinearLayoutManager)
                toAdapter.setFocusItemWithCodeForParity(it, binding.toRecyclerview.layoutManager as LinearLayoutManager)
            }
            else{
                bottomMenu(it.currencyType)
                fromAdapter.changeType(it.currencyType)
                toAdapter.changeType(it.currencyType)
                fromAdapter.setFocusItemWithCode(it, binding.fromRecyclerview.layoutManager as LinearLayoutManager)
            }
        }
        updateChangeText()

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

        binding.fromRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                        val offset = (binding.fromRecyclerview.height / itemHeight) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (position+1 != fromAdapter.getSelectedPosition()){
                                if (lastPosition < fromAdapter.getShowListSize()) {
                                    position += offset
                                    if (position in 0 until fromAdapter.getShowListSize()) {
                                        fromAdapter.setPositionToItem(position)
                                        scrollToItem(itemHeight, it, position, recyclerView)
                                        updateChangeText()
                                    }
                                }else{
                                    position += offset
                                    if (position in 0 until fromAdapter.getShowListSize()) {
                                        fromAdapter.setPositionToItem(position - 1)
                                        scrollToItem(itemHeight, it, position - 1, recyclerView)
                                        updateChangeText()
                                    }
                                }
                            }
                            else if (lastPosition == fromAdapter.getSelectedPosition()){
                                if (position != 0) {
                                    if (position in 0 until fromAdapter.getShowListSize()) {
                                        fromAdapter.setPositionToItem(position)
                                        scrollToItem(itemHeight, it, position, recyclerView)
                                        updateChangeText()
                                    }
                                }
                                else{
                                    if (position+1 in 0 until fromAdapter.getShowListSize()) {
                                        fromAdapter.setPositionToItem(position+1)
                                        scrollToItem(itemHeight, it, position + 1, recyclerView)
                                        updateChangeText()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        binding.changeItemButton.setOnClickListener {
            val fromPosition = if(fromAdapter.getSelectedItem().code != "TRY")fromAdapter.getSelectedPosition() + 1 else 1
            val toPosition = if(toAdapter.getSelectedItem().code != "TRY") toAdapter.getSelectedPosition() - 1 else toAdapter.getShowListSize()-2
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
                        val offset = (binding.toRecyclerview.height / itemHeight) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (position+1 != toAdapter.getSelectedPosition()){
                                if (lastPosition < toAdapter.getShowListSize()) {
                                    position += offset
                                    if (position in 0 until toAdapter.getShowListSize()) {
                                        toAdapter.setPositionToItem(position)
                                        scrollToItem(itemHeight, it, position, recyclerView)
                                        updateChangeText()
                                    }
                                }
                                else{
                                    position += offset
                                    if (position in 0 until toAdapter.getShowListSize()) {
                                        toAdapter.setPositionToItem(position - 1)
                                        scrollToItem(itemHeight, it, position - 1, recyclerView)
                                        updateChangeText()
                                    }
                                }
                            }
                            else if (lastPosition == toAdapter.getSelectedPosition()){
                                if (position != 0) {
                                    if (position in 0 until toAdapter.getShowListSize()) {
                                        toAdapter.setPositionToItem(position)
                                        scrollToItem(itemHeight, it, position, recyclerView)
                                        updateChangeText()
                                    }
                                }
                                else{
                                    if (position+1 in 0 until toAdapter.getShowListSize()) {
                                        toAdapter.setPositionToItem(position+1)
                                        scrollToItem(itemHeight, it, position + 1, recyclerView)
                                        updateChangeText()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        binding.fromPiece.addTextChangedListener { _it ->
            val fromTermUSDPrice = if (isCrypto && toAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toDouble() else 1.0
            val toTermUSDPrice = if (isCrypto && fromAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toDouble() else 1.0
            if (_it.toString() != ""){
                val fromPiece = decimalFormat.parse(_it.toString())?.toDouble()
                val toPiece = decimalFormat.parse(binding.toPiecePrice.text.toString())?.toDouble()
                binding.resultPrice.text = decimalFormat.format(fromPiece!! * toPiece!! * fromTermUSDPrice / toTermUSDPrice)
            }
            else{
                val toPiece = decimalFormat.parse(binding.toPiecePrice.text.toString())?.toDouble()
                binding.resultPrice.text = decimalFormat.format(0 * toPiece!! )
            }
        }
    }
    
    private fun updateChangeText(){
        val fromTermUSDPrice = if (isCrypto && toAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toString().toDouble() else 1.0
        val toTermUSDPrice = if (isCrypto && fromAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toString().toDouble() else 1.0
        if (toAdapter.getSelectedItem().sellingRate != 0f) {
            binding.toPiecePrice.text =
                decimalFormat.format((fromAdapter.getSelectedItem().sellingRate * fromTermUSDPrice) / (toAdapter.getSelectedItem().sellingRate * toTermUSDPrice))
        }
        binding.fromPiece.text.toString().let { fromPieceString->
            val fromPiece = decimalFormat.parse(fromPieceString)?.toDouble()
            val toPiece = decimalFormat.parse(binding.toPiecePrice.text.toString())?.toDouble()
            if (fromPiece == 0.0){
                binding.resultPrice.text = decimalFormat.format(0 * (toPiece!!* toTermUSDPrice))
            }else{
                binding.resultPrice.text = decimalFormat.format(fromPiece!! * toPiece!!)
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
                bottomMenu(CurrencyTypeEnum.Metal)
                /*
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar_selected, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                 */
                fromAdapter.changeType(CurrencyTypeEnum.Money)
                toAdapter.changeType(CurrencyTypeEnum.Money)
                if (!isFirst){
                    updateChangeText()
                }
            }
            binding.goldBarsContainer->{
                bottomMenu(CurrencyTypeEnum.Metal)
                /*
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars_selected, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                 */
                fromAdapter.changeType(CurrencyTypeEnum.Metal)
                toAdapter.changeType(CurrencyTypeEnum.Metal)
                if (!isFirst){
                    updateChangeText()
                }
            }
            binding.cryptoContainer->{
                /*
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin_selected, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))

                 */
                bottomMenu(CurrencyTypeEnum.Crypto)
                fromAdapter.changeType(CurrencyTypeEnum.Crypto)
                toAdapter.changeType(CurrencyTypeEnum.Crypto)
                if (!isFirst){
                    updateChangeText()
                }
            }
        }
    }

    private fun bottomMenu(type : CurrencyTypeEnum){
        when(type){
            CurrencyTypeEnum.Money ->{
                setColorAndImage(R.drawable.icon_dollar_selected, R.color.odak_light_blue, binding.dollarIcon, binding.dollarText)
                setColorAndImage(R.drawable.icon_gold_bars, R.color.white, binding.goldBarIcon, binding.goldBarText)
                setColorAndImage(R.drawable.icon_bitcoin, R.color.white, binding.bitcoinIcon, binding.bitcoinText)
            }
            CurrencyTypeEnum.Metal ->{
                setColorAndImage(R.drawable.icon_dollar, R.color.white, binding.dollarIcon, binding.dollarText)
                setColorAndImage(R.drawable.icon_gold_bars_selected, R.color.odak_light_blue, binding.goldBarIcon, binding.goldBarText)
                setColorAndImage(R.drawable.icon_bitcoin, R.color.white, binding.bitcoinIcon, binding.bitcoinText)
            }
            CurrencyTypeEnum.Crypto ->{
                setColorAndImage(R.drawable.icon_dollar, R.color.white, binding.dollarIcon, binding.dollarText)
                setColorAndImage(R.drawable.icon_gold_bars, R.color.white, binding.goldBarIcon, binding.goldBarText)
                setColorAndImage(R.drawable.icon_bitcoin_selected, R.color.odak_light_blue, binding.bitcoinIcon, binding.bitcoinText)
            }
            CurrencyTypeEnum.Parity->{}
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setColorAndImage(icon:Int, color:Int, imageView:ImageView, textView:TextView){
        resources.let {
            imageView.setImageDrawable(it.getDrawable(icon, it.newTheme()))
            textView.setTextColor(it.getColor(color, it.newTheme()))
        }
    }

    override fun changeTypeListener(isCrypto: Boolean) {
        this.isCrypto = isCrypto
    }

    /*
    private fun reformForDoubleToString(priceString:String):String{
        var result = ""
        if (priceString.length >7) {
            result = priceString.subSequence(0,7).toString()
            if (result.last() == '.'){
                result.replace(".","")
            }
        } else {
            result=priceString
        }
        return result
    }
     */
}