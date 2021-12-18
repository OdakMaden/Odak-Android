package com.techzilla.odak.alarm.viewcontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.SearchView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.techzilla.odak.R
import com.techzilla.odak.alarm.adapter.AddAlarmRecyclerviewAdapter
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.databinding.ActivityAddAlarmBinding
import com.techzilla.odak.shared.constants.exchangeRateList
import com.techzilla.odak.shared.model.ExchangeRateDTO

class AddAlarmActivity : AppCompatActivity(), AddAlarmRecyclerviewAdapter.AddAlarmListener {

    private val binding : ActivityAddAlarmBinding by lazy { ActivityAddAlarmBinding.inflate(layoutInflater) }

    private val adapter by lazy { AddAlarmRecyclerviewAdapter(this) }


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result:ActivityResult->
        if (result.resultCode == RESULT_OK){
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
       // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
           // window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.bottomBar.setPadding(0,0,0, getStatusBarHeight())
        binding.alarmRecyclerview.adapter = adapter
        selectBottomItem(binding.dollarContainer)
        adapter.insertParseList(exchangeRateList)

        binding.dollarContainer.setOnClickListener {
            selectBottomItem(binding.dollarContainer)
        }
        binding.goldBarsContainer.setOnClickListener {
            selectBottomItem(binding.goldBarsContainer)
        }
        binding.cryptoContainer.setOnClickListener {
            selectBottomItem(binding.cryptoContainer)
        }
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(text: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                text?.let {
                    adapter.searchItems(it)
                }
                return true
            }
        })
    }

    private fun getStatusBarHeight(): Int{
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId>0){
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun addAlarmForDetail(exchangeRateDTO: ExchangeRateDTO) {
        adapter.selectItem(exchangeRateDTO)
        startForResult.launch(
            Intent(this, AlarmDetailActivity::class.java).also {
                exchangeRateDTOForDetail = exchangeRateDTO
            }
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectBottomItem(constraintLayout: ConstraintLayout){
        when(constraintLayout){
            binding.dollarContainer->{
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar_selected, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                adapter.changeType(0)
            }
            binding.goldBarsContainer->{
                binding.dollarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_dollar, resources.newTheme()))
                binding.dollarText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                binding.goldBarIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_gold_bars_selected, resources.newTheme()))
                binding.goldBarText.setTextColor(resources.getColor(R.color.odak_light_blue, resources.newTheme()))
                binding.bitcoinIcon.setImageDrawable(resources.getDrawable(R.drawable.icon_bitcoin, resources.newTheme()))
                binding.bitcoinText.setTextColor(resources.getColor(R.color.white, resources.newTheme()))

                adapter.changeType(1)
            }
            binding.cryptoContainer->{
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
}