package com.techzilla.odak.alarm.viewcontroller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.alarmDTO
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.databinding.ActivityAlarmDetailBinding
import com.techzilla.odak.shared.model.AlarmTypeEnum
import com.techzilla.odak.shared.service.repository.AlarmRepository
import java.text.DecimalFormat

class AlarmDetailActivity : AppCompatActivity() {

    private val binding : ActivityAlarmDetailBinding by lazy { ActivityAlarmDetailBinding.inflate(layoutInflater) }

    private lateinit var alarmRepository: AlarmRepository

    private var alarmType : AlarmTypeEnum = AlarmTypeEnum.PriceOver
    private var isPrice:Boolean = true
    private var isDistance:Boolean = false
    private var isIfUp:Boolean = true
    private var isIfDown:Boolean = false
    private val decimalFormat = DecimalFormat("#.####")

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (exchangeRateDTOForDetail == null){
            Toast.makeText(this, "Bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
            finish()
        }
        alarmRepository = AlarmRepository()

        exchangeRateDTOForDetail?.let {
            binding.currencyCode.text = it.code
            binding.currencyName.text = it.name
            binding.lastPriceLabel.text = "${resources.getString(R.string.alarm_lastPrice_label)} ${decimalFormat.format(it.sellingRate)}"
            binding.aimPrice.setText(decimalFormat.format(it.sellingRate))
            binding.priceButton.isSelected = true
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.priceButton.text.toString())
            binding.ifUpButtonText.isSelected = true
            binding.ifDownButtonText.isSelected = false
            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
        }


        binding.ifDownButton.setOnClickListener {
            if (!binding.ifDownButtonText.isSelected){
                binding.ifUpButtonText.isSelected = false
                binding.ifDownButtonText.isSelected = true
                isIfDown = true
                isIfUp = false
                binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                getResultSlider(binding.sliderCircle.x)
            }
        }

        binding.ifUpButton.setOnClickListener {
            if (!binding.ifUpButtonText.isSelected){
                binding.ifDownButtonText.isSelected = false
                binding.ifUpButtonText.isSelected = true
                isIfDown = false
                isIfUp = true
                binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                getResultSlider(binding.sliderCircle.x)
            }
        }

        binding.distanceButton.setOnClickListener {
            binding.distanceButton.isSelected = true
            binding.priceButton.isSelected = false
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.distanceButton.text.toString())
            isDistance = true
            isPrice = false
            getResultSlider(binding.sliderCircle.x)
        }

        binding.priceButton.setOnClickListener {
            binding.distanceButton.isSelected = false
            binding.priceButton.isSelected = true
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.priceButton.text.toString())
            isDistance = false
            isPrice = true
            getResultSlider(binding.sliderCircle.x)
        }

        binding.backBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        alarmRepository.addNewAlarmLiveData.observe(this, {
            alarmDTO = it
            setResult(RESULT_OK)
            finish()
        })

        binding.createAlarmButton.setOnClickListener {
            if (isPrice && !isDistance && isIfUp && !isIfDown){
                alarmType = AlarmTypeEnum.PriceOver
            }
            else if (isPrice && !isDistance && !isIfUp && isIfDown){
                alarmType = AlarmTypeEnum.PriceUnder
            }
            else if (!isPrice && isDistance && isIfUp && !isIfDown){
                alarmType = AlarmTypeEnum.PercentOver
            }
            else if(!isPrice && isDistance && !isIfUp && isIfDown){
                alarmType = AlarmTypeEnum.PercentUnder
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("Name",binding.currencyName.text.toString())
            jsonObject.addProperty("CurrencyCode", binding.currencyCode.text.toString())
            jsonObject.addProperty("AlarmType", alarmType.value)
            if (isDistance) {
                jsonObject.addProperty(
                    "ReferenceValue",
                    binding.lastPriceLabel.text.toString().replace("SON FİYAT: ", "").toDouble()
                )
            }
            jsonObject.addProperty("TargetValue", binding.aimPrice.text.toString().replace("% ","").toDouble())
            alarmRepository.addAlarm(jsonObject)
            //setResult(RESULT_OK)
            //finish()
        }


        binding.aimPriceIcon.setOnClickListener {
            binding.aimPrice.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.aimPrice, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.aimPrice.addTextChangedListener {
            binding.aimPrice.minWidth = 40
        }


        binding.aimPrice.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                if (isPrice && !isDistance){
                    when {
                        binding.aimPrice.text.toString().replace("% ", "").toDouble() < (exchangeRateDTOForDetail!!.sellingRate * 0.8) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        binding.aimPrice.text.toString().replace("% ", "").toDouble() > (exchangeRateDTOForDetail!!.sellingRate * 1.2) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                            binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        else -> {
                            changeSliderTitleWithEditText(binding.aimPrice.text.toString().replace("% ", ""))
                        }
                    }
                }
                else if(!isPrice && isDistance){
                    when {
                        binding.aimPrice.text.toString().replace("% ", "").toDouble() < (exchangeRateDTOForDetail!!.sellingRate * 0.8) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        binding.aimPrice.text.toString().replace("% ", "").toDouble() > (exchangeRateDTOForDetail!!.sellingRate * 1.2) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                            binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        else -> {
                            changeSliderTitleWithEditText(binding.aimPrice.text.toString().replace("% ", ""))
                        }
                    }
                }
                /*when {
                    binding.aimPrice.text.toString().replace("% ", "").toDouble() < (exchangeRateDTOForDetail!!.sellingRate * 0.8) -> {
                        binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                        binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                        getResultSlider(binding.sliderCircle.x)
                    }
                    binding.aimPrice.text.toString().replace("% ", "").toDouble() > (exchangeRateDTOForDetail!!.sellingRate * 1.2) -> {
                        binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                        binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                        getResultSlider(binding.sliderCircle.x)
                    }
                    else -> {
                        changeSliderTitleWithEditText(binding.aimPrice.text.toString().replace("% ", ""))
                    }
                }

                 */
            }

        }

        binding.sliderCircleWay.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE && checkOnSliderWay(motionEvent.x)){
                binding.sliderCircle.x = motionEvent.x - binding.sliderCircle.width / 2
                getResultSlider(binding.sliderCircle.x)
            }
            else if(motionEvent.action == MotionEvent.ACTION_UP && checkOnSliderWay(motionEvent.x)){
                binding.sliderCircle.x = motionEvent.x - binding.sliderCircle.width / 2
                getResultSlider(motionEvent.x - binding.sliderCircle.width / 2)
            }
            true
        }
    }

    private fun checkOnSliderWay(x:Float):Boolean{
        return x > ((binding.sliderCircleWay.width - binding.sliderWay.width) / 2) &&
                x < (binding.sliderWay.width + binding.sliderCircle.width)
    }

    @SuppressLint("SetTextI18n")
    private fun getResultSlider(x:Float){
        val result = (x + (binding.sliderCircle.width / 2) - ((binding.sliderCircleWay.width - binding.sliderWay.width ) / 2 )) /
                (binding.sliderWay.width)

        val decimalFactor = DecimalFormat("#.#").format(result * 20).toDouble()
        exchangeRateDTOForDetail?.let {
            if (isPrice && !isDistance){
                if (isIfUp && !isIfDown){
                    binding.aimPrice.setText(decimalFormat.format(it.sellingRate * (decimalFactor / 100 + 1)))
                }
                else if (!isIfUp && isIfDown){
                    binding.aimPrice.setText(decimalFormat.format((it.sellingRate ) * (0.8 + decimalFactor / 100)))
                }
            }
            else if(!isPrice && isDistance){
                if (isIfUp && !isIfDown){
                    binding.aimPrice.setText("% ${DecimalFormat("#.#").format(decimalFactor)}")
                }
                else if (!isIfUp && isIfDown){
                    binding.aimPrice.setText("% ${DecimalFormat("#.#").format(decimalFactor - 20)}")
                }
            }
        }
    }

    private fun changeSliderTitleWithEditText(text : String){
        exchangeRateDTOForDetail?.let {
            if (isPrice && !isDistance){
                val textFloat = text.toFloat()
                val sliderValue = (binding.sliderCircleWay.width - (binding.sliderCircleWay.width - binding.sliderWay.width) / 2  )
                if (isIfUp && !isIfDown){
                    binding.sliderCircle.x = (sliderValue * (((textFloat - (it.sellingRate)) / ((it.sellingRate*1.2) - (it.sellingRate)))).toFloat()) - binding.sliderCircle.width / 2
                }
                else if (!isIfUp && isIfDown){
                    binding.sliderCircle.x = (sliderValue * (((textFloat - (it.sellingRate * 0.8)) / ((it.sellingRate) - (it.sellingRate * 0.8)))).toFloat()) - binding.sliderCircle.width / 2
                }
            }
            else if(!isPrice && isDistance){
                if (isIfUp && !isIfDown){

                }
                else if (!isIfUp && isIfDown){

                }
            }
        }
    }

}