package com.techzilla.odak.converter.viewcontrollers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techzilla.odak.R
import com.techzilla.odak.converter.adapter.ItemPickerAdapter
import com.techzilla.odak.converter.model.ConverterCurrency
import com.techzilla.odak.databinding.FragmentConverterBinding
import java.text.DecimalFormat


class ConverterFragment : Fragment() {

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

        binding.currencyButtonText.isSelected = true
        binding.currencyBottomLine.isSelected = true

        binding.currencyButton.setOnClickListener {
            binding.currencyButtonText.isSelected = true
            binding.currencyBottomLine.isSelected = true
            binding.goldBarButtonText.isSelected = false
            binding.goldBarBottomLine.isSelected = false
        }

        binding.goldBarButton.setOnClickListener {
            binding.currencyButtonText.isSelected = false
            binding.currencyBottomLine.isSelected = false
            binding.goldBarButtonText.isSelected = true
            binding.goldBarBottomLine.isSelected = true
        }

        binding.fromRecyclerview.adapter = fromAdapter
        binding.toRecyclerview.adapter = toAdapter

        val arrayList = ArrayList<ConverterCurrency>()
        arrayList.add(ConverterCurrency(" ", " ", 0.0))
        arrayList.add(ConverterCurrency("TRY", "Lira", 1.0))
        arrayList.add(ConverterCurrency("USD", "Dolar", 8.8))
        arrayList.add(ConverterCurrency("EURO", "Euro", 10.34))
        arrayList.add(ConverterCurrency("SE", "Seeeas", 3.4))
        arrayList.add(ConverterCurrency("ADA", "Cardana", 24.3))
        arrayList.add(ConverterCurrency("AS", "asda", 344.333))
        arrayList.add(ConverterCurrency("TRY", "Lira",1.0))
        arrayList.add(ConverterCurrency("USD", "Dolar", 8.8))
        arrayList.add(ConverterCurrency("EURO", "Euro", 10.34))
        arrayList.add(ConverterCurrency("SE", "Seeeas", 3.4))
        arrayList.add(ConverterCurrency("ADA", "Cardana", 24.3))
        arrayList.add(ConverterCurrency("AS1", "asdsdaa", 3432.2))
        arrayList.add(ConverterCurrency(" ", " ", 0.0))
        fromAdapter.addNewItem(arrayList )
        toAdapter.addNewItem(arrayList)

        binding.fromRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    context?.resources?.getDimensionPixelSize(R.dimen.item_height)?.let { itemHeight ->
                        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                        val offset = (binding.fromRecyclerview.height / itemHeight - 1) / 2
                        layoutManager?.let {
                            var position = it.findFirstCompletelyVisibleItemPosition()
                            if (position+1 != fromAdapter.getSelectedPosition()){
                                position += offset
                                if (position in 0 until arrayList.size) {
                                    fromAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                            else{
                                if (position in 0 until arrayList.size) {
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
                                if (position in 0 until arrayList.size) {
                                    toAdapter.setPositionToItem(position)
                                    scrollToItem(itemHeight, it, position, recyclerView)
                                    updateChangeText()
                                }
                            }
                            else if (lastPosition == toAdapter.getSelectedPosition()){
                                if (position in 0 until arrayList.size) {
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

      //  binding.fromCode.text = fromAdapter.getSelectedItem().currencyCode
      //  binding.toPieceCode.text = toAdapter.getSelectedItem().currencyCode
        binding.fromPiece.addTextChangedListener { _it ->
            if (_it.toString() != ""){
                binding.resultPrice.text = decimalFormat.format(_it.toString().toDouble() * binding.toPiecePrice.text.toString().toDouble())
            }
            else{
                binding.resultPrice.text = decimalFormat.format(0 * binding.toPiecePrice.text.toString().toDouble())
            }
        }
        updateChangeText()
     //   binding.toPiecePrice.text = decimalFormat.format(toAdapter.getSelectedItem().currencyPrice / fromAdapter.getSelectedItem().currencyPrice)
     //   binding.resultPrice.text = decimalFormat.format(binding.fromPiece.text.toString().toDouble() / binding.toPiecePrice.text.toString().toDouble())
    }

    private fun updateChangeText(){
        binding.fromCode.text = fromAdapter.getSelectedItem().currencyCode
        binding.toPiecePrice.text = decimalFormat.format(toAdapter.getSelectedItem().currencyPrice / fromAdapter.getSelectedItem().currencyPrice)
        binding.toPieceCode.text = toAdapter.getSelectedItem().currencyCode
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
    }
}