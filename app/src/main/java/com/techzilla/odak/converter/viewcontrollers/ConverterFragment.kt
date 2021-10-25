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
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.helper_interface.MenuButtonListener
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import java.text.DecimalFormat


class ConverterFragment constructor(private val listener: MenuButtonListener) : Fragment() {

    private val binding by lazy { FragmentConverterBinding.inflate(layoutInflater) }
    private val fromAdapter by lazy { ItemPickerAdapter(0) }
    private val toAdapter by lazy { ItemPickerAdapter(1) }
    private val decimalFormat = DecimalFormat("#.#####")

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
        binding.toPiecePrice.text = decimalFormat.format(fromAdapter.getSelectedItem().sellingRate / toAdapter.getSelectedItem().sellingRate)
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
                    context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let {
                        layoutManager.scrollToPositionWithOffset(position, (height / 2 - it / 2))
                    }
                }
            })
        }

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(MarketFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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