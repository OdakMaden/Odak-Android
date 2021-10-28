package com.techzilla.odak.converter.viewcontrollers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.converter.adapter.ItemPickerAdapter
import com.techzilla.odak.databinding.FragmentConverterBinding
import com.techzilla.odak.main.viewcontrollers.MarketFragment
import com.techzilla.odak.shared.constants.exchangeRateDTOListMap
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.helper_interface.MenuButtonListener
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import java.text.DecimalFormat


class ConverterFragment constructor(private val listener: MenuButtonListener) : Fragment(), ItemPickerAdapter.ChangeTypeListener {

    private val binding by lazy { FragmentConverterBinding.inflate(layoutInflater) }
    private val fromAdapter by lazy { ItemPickerAdapter(0, this) }
    private val toAdapter by lazy { ItemPickerAdapter(1, this) }
    private val decimalFormat = DecimalFormat("#.#####")
    private var isCrypto : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomBar.setPadding(0,0,0, getStatusBarHeight())
        listener.menuVisible(View.GONE)

        binding.fromRecyclerview.adapter = fromAdapter
        binding.toRecyclerview.adapter = toAdapter

        binding.fromRecyclerview.adapter = fromAdapter
        binding.toRecyclerview.adapter = toAdapter

        fromAdapter.addNewItem(exchangeRateList)
        toAdapter.addNewItem(exchangeRateList)
        updateChangeText()

        selectBottomItem(binding.dollarContainer, true)

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(MarketFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
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
            requireActivity().supportFragmentManager.popBackStack(MarketFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.fromRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
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
                                }else{
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
            val fromPosition = if(fromAdapter.getSelectedItem().code != "TRY")fromAdapter.getSelectedPosition() + 1 else 0
            val toPosition = if(toAdapter.getSelectedItem().code != "TRY") toAdapter.getSelectedPosition() - 1 else toAdapter.getShowListSize()-2
            fromAdapter.setPositionToItem(toPosition)
            toAdapter.setPositionToItem(fromPosition)
            context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let {
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
                    context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                        val offset = (binding.toRecyclerview.height / itemHeight) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            val lastPosition = it.findLastCompletelyVisibleItemPosition()
                            if (position+1 != toAdapter.getSelectedPosition()){
                                if (lastPosition < toAdapter.getShowListSize()){
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
        val fromTermUSDPrice = if (isCrypto && toAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toDouble() else 1.0
        val toTermUSDPrice = if (isCrypto && fromAdapter.getSelectedItem().code == "TRY") exchangeRateDTOListMap["USDTRY"]!!.sellingRate.toDouble() else 1.0
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
                    context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let {
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

    override fun changeTypeListener(isCrypto: Boolean) {
        this.isCrypto = isCrypto
    }

}