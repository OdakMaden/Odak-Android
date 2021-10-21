package com.techzilla.odak.main.viewcontrollers

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.techzilla.odak.main.adapters.InnerViewRecyclerViewAdapter
import com.techzilla.odak.main.constant.isAddFavorite
import com.techzilla.odak.main.constant.isChangeInnerViewCurrencyModel
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.constants.exchangeRateListMap
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.service.repository.MainRepository
import org.json.JSONObject
import org.json.JSONTokener


class MarketFragment : Fragment(), InnerViewRecyclerViewAdapter.InnerViewListener {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    private val mainRepository by lazy { MainRepository() }

    private var isFirstOpen : Boolean = true

    companion object {
        const val TAG= "MarketFragment"
    }

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            AlertDialog.Builder(requireContext()).setTitle("Alarm").setMessage("Alarm Başarılı Olarak Kaydedilmiştir.").setPositiveButton("Tamam"
            ) { dialog, p1 ->  dialog.dismiss()}.show()
        }
    }

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
            if (isFirstOpen) {
                rememberMemberDTO?.let { memberDTO ->
                    var favoriteIdList = ""
                    if (memberDTO.memberData != null) {
                        val memberDataJSON =
                            JSONTokener(memberDTO.memberData).nextValue() as JSONObject
                        favoriteIdList = memberDataJSON.getString("favoriteIdList")
                    }
                    adapter.insertNewParam(it, favoriteIdList)
                    isFirstOpen = false
                    println("listelendi")
                }
            }else{
                it.forEach { exDTO->
                    adapter.changeItems(exDTO)
                    println("değiştirildi")
                }
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

                println(updateMemberDTO)
                mainRepository.updateMemberDTO(updateMemberDTO)
            }
            else{
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteCodeId"}""")

                println(updateMemberDTO)
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


}