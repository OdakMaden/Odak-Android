package com.techzilla.odak.main.viewcontrollers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.techzilla.odak.R
import com.techzilla.odak.databinding.FragmentMarketBinding
import com.techzilla.odak.main.adapters.InnerViewRecyclerViewAdapter
import com.techzilla.odak.shared.model.CurrencyModel
import com.techzilla.odak.shared.model.InnerViewCurrencyModel


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

        val list : MutableList<InnerViewCurrencyModel> = mutableListOf(InnerViewCurrencyModel(
            CurrencyModel("AMERİKAN DOLARI", "USD", "8,4436", "8,4431", "% -0,08", 0),
            false),
            InnerViewCurrencyModel(
                CurrencyModel("EURO", "EUR/TRY", "8,4436", "8,4431", "% 0,08", 0),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("İNGİLİZ STERLİNİ", "GBP/TRY", "8,4436", "8,4431", "% -0,08", 0),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("İSVEÇ FRANKI", "CHF/TRY", "8,4436", "8,4431", "% 0,08", 0),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("", "GR ALTIN", "8,4436", "8,4431", "% -0,08", 1),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("", "HAS ALTIN", "8,4436", "8,4431", "% 0,08", 1),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("", "22 AYAR", "8,4436", "8,4431", "% -0,08", 1),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("BITCOIN", "BTC/TRY", "8,4436", "8,4431", "% -0,08", 2),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("CARDANO", "ADA/TRY", "8,4436", "8,4431", "% 0,08", 2),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("COSMOS", "ATOM/TRY", "8,4436", "8,4431", "% -0,08", 2),
                false),
            InnerViewCurrencyModel(
                CurrencyModel("ETHEREUM", "ETH/TRY", "8,4436", "8,4431", "% 0,08", 2),
                false))

        adapter.insertNewParam(list)
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


}