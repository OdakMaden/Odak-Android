package com.techzilla.odak.main.viewcontrollers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.alarm.viewcontroller.AlarmDetailActivity
import com.techzilla.odak.converter.viewcontrollers.ConverterActivity
import com.techzilla.odak.currencydetail.viewcontroller.CurrencyDetailActivity
import com.techzilla.odak.databinding.FragmentMarketBinding
import com.techzilla.odak.main.adapters.ExchangeRateRecyclerViewAdapter
import com.techzilla.odak.main.constant.isAddFavorite
import com.techzilla.odak.main.constant.isChangeInnerViewCurrencyModel
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.helper_interface.MenuButtonListener
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.service.repository.MainRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController
import org.json.JSONObject
import org.json.JSONTokener


class MarketFragment (private val listener:MenuButtonListener) : Fragment(), ExchangeRateRecyclerViewAdapter.ExchangeRateRecyclerViewListener
{
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    private val mainRepository by lazy { MainRepository() }

    private var isFirstOpen : Boolean = true

    companion object {
        const val TAG= "MarketFragment"
    }

    private val adapter by lazy { ExchangeRateRecyclerViewAdapter(this) }

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
        listener.menuVisible(View.VISIBLE)
        binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE

        mainRepository.getExchangeRateList(rememberMemberDTO!!.memberID, "")

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

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let {
                    adapter.searchItems(p0.lowercase())
                }
                return true
            }
        })

        mainRepository.exchangeRateListLiveData.observe(viewLifecycleOwner, {
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            if (isFirstOpen) {
                rememberMemberDTO?.let { memberDTO ->
                    var favoriteIdList = ""
                    if (memberDTO.memberData != null) {
                        val memberDataJSON =
                            JSONTokener(memberDTO.memberData).nextValue() as JSONObject
                        favoriteIdList = memberDataJSON.getString("favoriteIdList")
                    }
                    adapter.insertNewExchangeRateRecyclerView(it, favoriteIdList)
                    isFirstOpen = false
                }
            }else{
                adapter.changeItems(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        isChangeInnerViewCurrencyModel?.let {
            if (isAddFavorite){
                adapter.addFavorite(it)
            }
            else{
                adapter.deleteFavorite(it)
            }
            isChangeInnerViewCurrencyModel = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isChangeInnerViewCurrencyModel = null
        mainRepository.periodicRequestClose()
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

                adapter.changeType(-1)
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

        binding.searchView.setQuery("", true)
    }

    private fun getStatusBarHeight(): Int{
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId>0){
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun addFavoriteList(favoriteCodeId:String){
        rememberMemberDTO?.let {
            if (it.memberData != null){
                val memberDataJSON = JSONTokener(it.memberData).nextValue() as JSONObject
                var favoriteIdList = memberDataJSON.getString("favoriteIdList")
                favoriteIdList += ",$favoriteCodeId"
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteIdList"}""")
                mainRepository.updateMemberDTO(updateMemberDTO)
            }
            else{
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteCodeId"}""")
                mainRepository.updateMemberDTO(updateMemberDTO)
            }
        }
    }

    private fun deleteFavoriteList(favoriteCodeId:String){
        rememberMemberDTO?.let {
            if (it.memberData != null){
                val memberDataJSON = JSONTokener(it.memberData).nextValue() as JSONObject
                var favoriteIdList = memberDataJSON.getString("favoriteIdList")
                if (favoriteIdList.contains(favoriteCodeId)){
                    favoriteIdList = favoriteIdList.replace(",$favoriteCodeId", "")
                    val updateMemberDTO = JsonObject()
                    updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteIdList"}""")
                    mainRepository.updateMemberDTO(updateMemberDTO)
                }
            }
        }
    }

    override fun exchangeRateRecyclerViewItemOnClickListener(position: Int) {
        adapter.selectItem(position)
    }

    override fun exchangeRateRecyclerViewItemForDetailOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        Intent(requireActivity(), CurrencyDetailActivity::class.java).apply {
            exchangeRateDTOForDetail = exchangeRateDTO
            startActivity(this)
        }
    }

    override fun exchangeRateRecyclerViewItemAddFavoriteOnClickListener(exchangeRateDTO: ExchangeRateDTO, isFavorite: Boolean) {
        if (isFavorite){
            deleteFavoriteList(exchangeRateDTO.code)
            adapter.deleteFavorite(exchangeRateDTO)
        }
        else{
            addFavoriteList(exchangeRateDTO.code)
            adapter.addFavorite(exchangeRateDTO)
        }
    }

    override fun exchangeRateRecyclerViewItemAlarmOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        //startResult.launch(Intent(requireActivity(), AlarmDetailActivity::class.java).also {
        exchangeRateDTOForDetail = exchangeRateDTO
        Intent(requireActivity(), AlarmDetailActivity::class.java).apply {
            startActivity(this)
        }
        //})
    }

    override fun exchangeRateRecyclerViewItemConverterOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        exchangeRateDTOForDetail = exchangeRateDTO
        Intent(requireActivity(), ConverterActivity::class.java).apply {
            startActivity(this)
        }
       /* startResult.launch(Intent(requireActivity(), ConverterActivity::class.java).also {
            exchangeRateDTOForDetail = exchangeRateDTO
        })

        */
    }


/*
    override fun innerViewOnClickListener(position: Int) {
        adapter.selectItem(position)
    }

    override fun innerViewForDetailOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        Intent(requireActivity(), CurrencyDetailActivity::class.java).apply {
            exchangeRateDTOForDetail = exchangeRateDTO
            startActivity(this)
        }
    }

    override fun innerViewAddFavoriteOnClickListener(exchangeRateDTO: ExchangeRateDTO, isFavorite:Boolean) {
        if (isFavorite){
            deleteFavoriteList(exchangeRateDTO.code)
            adapter.deleteFavorite(exchangeRateDTO)
        }
        else{
            addFavoriteList(exchangeRateDTO.code)
            adapter.addFavorite(exchangeRateDTO)
        }
    }

    override fun innerViewAlarmOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        startResult.launch(Intent(requireActivity(), AlarmDetailActivity::class.java).also {
            exchangeRateDTOForDetail = exchangeRateDTO
        })
    }

    override fun innerViewConverterOnClickListener(exchangeRateDTO: ExchangeRateDTO) {
        startResult.launch(Intent(requireActivity(), ConverterActivity::class.java).also {
            exchangeRateDTOForDetail = exchangeRateDTO
        })
    }

 */


}