package com.techzilla.odak.main.viewcontrollers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.techzilla.odak.R
import com.techzilla.odak.currencydetail.viewcontroller.CurrencyDetailActivity
import com.techzilla.odak.databinding.FragmentMarketBinding
import com.techzilla.odak.main.adapters.InnerViewRecyclerViewAdapter
import com.techzilla.odak.main.constant.isChangeInnerViewCurrencyModel
import com.techzilla.odak.shared.constants.USER
import com.techzilla.odak.shared.constants.list
import com.techzilla.odak.shared.model.CurrencyModel


class MarketFragment : Fragment(), InnerViewRecyclerViewAdapter.InnerViewListener {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!


    private val adapter by lazy { InnerViewRecyclerViewAdapter(this,
        binding.defaultRecyclerview.layoutManager!!
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomBar.setPadding(0,0,0, getStatusBarHeight())
        selectBottomItem(binding.favoriteContainer)
        binding.defaultRecyclerview.adapter = adapter

        binding.searchView.setOnSearchClickListener {
            it.startAnimation(AnimationUtils.makeInAnimation(requireActivity(), false))
        }
        binding.searchView.setOnCloseListener {
            binding.searchView.startAnimation(AnimationUtils.makeInAnimation(requireActivity(), true))
            false
        }

        binding.favoriteContainer.setOnClickListener {
            selectBottomItem(binding.favoriteContainer)
        }
        binding.dollarContainer.setOnClickListener {
            selectBottomItem(binding.dollarContainer)
        }
        binding.goldBarsContainer.setOnClickListener {
            selectBottomItem(binding.goldBarsContainer)
        }
        binding.cryptoContainer.setOnClickListener {
            selectBottomItem(binding.cryptoContainer)
        }
        adapter.insertNewParam(list, USER.favoriteCodeList)
    }

    override fun onResume() {
        super.onResume()
        isChangeInnerViewCurrencyModel?.let {
            adapter.changeItems(USER.favoriteCodeList, it)
            isChangeInnerViewCurrencyModel = null
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectBottomItem(constraintLayout: ConstraintLayout){
        when(constraintLayout){
            binding.favoriteContainer ->{
                binding.favoriteIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_favori_selected, resources.newTheme()))
                binding.favoriteText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                adapter.changeType(4)
            }
            binding.dollarContainer->{
                binding.favoriteIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_favori, resources.newTheme()))
                binding.favoriteText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar_selected, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                adapter.changeType(0)
            }
            binding.goldBarsContainer->{
                binding.favoriteIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_favori, resources.newTheme()))
                binding.favoriteText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars_selected, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                adapter.changeType(1)
            }
            binding.cryptoContainer->{
                binding.favoriteIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_favori, resources.newTheme()))
                binding.favoriteText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin_selected, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))

                adapter.changeType(2)
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

    override fun innerViewOnClickListener(position: Int) {
        adapter.selectItem(position)
    }

    override fun innerViewForDetailOnClickListener(currencyModel: CurrencyModel) {
        Intent(requireActivity(), CurrencyDetailActivity::class.java).apply {
            putExtra("currencyCode", currencyModel.currencyCode)
            startActivity(this)
        }
    }

    override fun innerViewAddFavoriteOnClickListener(currencyModel: CurrencyModel, isFavorite:Boolean) {
        if (isFavorite){
            USER.favoriteCodeList.remove(currencyModel.currencyCode)
            adapter.deleteFavorite(currencyModel)
        }
        else{
            USER.favoriteCodeList.add(currencyModel.currencyCode)
            adapter.addFavorite(currencyModel)
        }
    }


}