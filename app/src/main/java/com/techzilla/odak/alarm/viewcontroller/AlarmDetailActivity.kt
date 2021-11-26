package com.techzilla.odak.alarm.viewcontroller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.alarmDTO
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.databinding.ActivityAlarmDetailBinding
import com.techzilla.odak.shared.model.AlarmDTO
import com.techzilla.odak.shared.model.AlarmTypeEnum
import com.techzilla.odak.shared.model.CurrencyTypeEnum
import com.techzilla.odak.shared.service.repository.AlarmRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController
import java.text.DecimalFormat

class AlarmDetailActivity : AppCompatActivity() {

    private val binding : ActivityAlarmDetailBinding by lazy { ActivityAlarmDetailBinding.inflate(layoutInflater) }

    private lateinit var alarmRepository: AlarmRepository

    private var isEdit:Boolean = false

    private var alarmType : AlarmTypeEnum = AlarmTypeEnum.PriceOver
    private var alarmName : String = ""

    private val decimalFormat = DecimalFormat("#.####")
    private val cryptoDecimalFormat = DecimalFormat("#.##")

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
        isEdit = intent.getBooleanExtra("edit", false)


        exchangeRateDTOForDetail?.let {
            binding.currencyCode.text = it.code
            binding.currencyName.text = it.name
            if (it.currencyType == CurrencyTypeEnum.Crypto){
                binding.lastPriceLabel.text = "${resources.getString(R.string.alarm_lastPrice_label)} ${cryptoDecimalFormat.format(it.sellingRate)}"
                binding.aimPrice.setText(cryptoDecimalFormat.format(it.sellingRate))
            }
            else{
                binding.lastPriceLabel.text = "${resources.getString(R.string.alarm_lastPrice_label)} ${decimalFormat.format(it.sellingRate)}"
                binding.aimPrice.setText(decimalFormat.format(it.sellingRate))
            }
            binding.priceButton.isSelected = true
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.priceButton.text.toString())
            binding.ifUpButtonText.isSelected = true
            binding.ifDownButtonText.isSelected = false
            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
        }

        if (isEdit){
            alarmDTO?.let {
                alarmType = it.alarmType
                setSelectButton(it.alarmType)
                if (it.alarmType == AlarmTypeEnum.PriceUnder || it.alarmType == AlarmTypeEnum.PriceOver){
                    binding.aimPrice.setText(decimalFormat.format(it.targetValue))
                }
                else if(it.alarmType == AlarmTypeEnum.PercentOver){
                    binding.aimPrice.setText("% ${decimalFormat.format(it.targetValue)}")
                }
                else if (it.alarmType == AlarmTypeEnum.PercentUnder){
                    binding.aimPrice.setText("% -${decimalFormat.format(it.targetValue)}")
                }

                object : CountDownTimer(2000, 1000){
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        setSliderXForEdit(it)
                    }
                }.start()
            }
        }

        binding.ifDownButton.setOnClickListener {
            if (!binding.ifDownButtonText.isSelected){
                binding.ifUpButtonText.isSelected = false
                binding.ifDownButtonText.isSelected = true
                alarmType = setAlarmType()
                binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                getResultSlider(binding.sliderCircle.x)
            }
        }

        binding.ifUpButton.setOnClickListener {
            if (!binding.ifUpButtonText.isSelected){
                binding.ifDownButtonText.isSelected = false
                binding.ifUpButtonText.isSelected = true
                alarmType = setAlarmType()
                binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                getResultSlider(binding.sliderCircle.x)
            }
        }

        binding.percentButton.setOnClickListener {
            binding.percentButton.isSelected = true
            binding.priceButton.isSelected = false
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.percentButton.text.toString())
            alarmType = setAlarmType()
            getResultSlider(binding.sliderCircle.x)
        }

        binding.priceButton.setOnClickListener {
            binding.percentButton.isSelected = false
            binding.priceButton.isSelected = true
            binding.aimLabelTitle.text =
                resources.getString(R.string.alarm_aim_title_label).replace("%@", binding.priceButton.text.toString())
            alarmType = setAlarmType()
            getResultSlider(binding.sliderCircle.x)
        }

        binding.backBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        alarmRepository.addNewAlarmLiveData.observe(this, {
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            alarmDTO = it
            setResult(RESULT_OK)
            finish()
        })

        alarmRepository.errorLiveData.observe(this, {
            binding.componentProgressBar.progressbarContainer.visibility = View.GONE
            AlertDialogViewController.buildAlertDialog(this, "", it, "","",resources.getString(R.string.shared_Ok))
        })

        binding.createAlarmButton.setOnClickListener {
            binding.componentProgressBar.progressbarContainer.visibility = View.VISIBLE
            if (isEdit){
                val alarmMap = HashMap<String, Any>()
                alarmDTO?.let {
                    alarmName = when (alarmType) {
                        AlarmTypeEnum.PriceOver -> {
                            "${binding.currencyName.text} ${binding.aimPrice.text}"
                        }
                        AlarmTypeEnum.PriceUnder -> {
                            "${binding.currencyName.text} ${binding.aimPrice.text}"
                        }
                        AlarmTypeEnum.PercentOver -> {
                            "${binding.currencyName.text} +${binding.aimPrice.text}"
                        }
                        AlarmTypeEnum.PercentUnder -> {
                            "${binding.currencyName.text} -${
                                binding.aimPrice.text.toString().replace("-", "")
                            }"
                        }
                    }
                    alarmMap["Name"] = alarmName
                    alarmMap["CurrencyCode"] = it.currencyCode
                    alarmMap["AlarmType"] = alarmType.value
                    if (alarmType == AlarmTypeEnum.PercentOver || alarmType == AlarmTypeEnum.PercentUnder) {
                        alarmMap["ReferenceValue"] = exchangeRateDTOForDetail!!.sellingRate
                    }
                    alarmMap["TargetValue"] = cleanInputAndToFloat(binding.aimPrice.text.toString())
                    alarmRepository.updateAlarm(it.rID, alarmMap)
                }
            }
            else {
                alarmName = when (alarmType) {
                    AlarmTypeEnum.PriceOver -> {
                        "${binding.currencyName.text} ${binding.aimPrice.text}"
                    }
                    AlarmTypeEnum.PriceUnder -> {
                        "${binding.currencyName.text} ${binding.aimPrice.text}"
                    }
                    AlarmTypeEnum.PercentOver -> {
                        "${binding.currencyName.text} +${binding.aimPrice.text}"
                    }
                    AlarmTypeEnum.PercentUnder -> {
                        "${binding.currencyName.text} -${
                            binding.aimPrice.text.toString().replace("-", "")
                        }"
                    }
                }

                val alarmMap = HashMap<String, Any>()
                alarmMap["Name"] = alarmName
                alarmMap["CurrencyCode"] = exchangeRateDTOForDetail!!.code
                alarmMap["AlarmType"] = alarmType.value
                if (alarmType == AlarmTypeEnum.PercentOver || alarmType == AlarmTypeEnum.PercentUnder) {
                    alarmMap["ReferenceValue"] = exchangeRateDTOForDetail!!.sellingRate
                }
                alarmMap["TargetValue"] = cleanInputAndToFloat(binding.aimPrice.text.toString())
                alarmRepository.addAlarm(alarmMap)
            }
        }


        binding.aimPriceIcon.setOnClickListener {
            if (!binding.aimPrice.isFocusable) {
                binding.aimPrice.requestFocus()
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.aimPrice, InputMethodManager.SHOW_IMPLICIT)
            }
            else{
                binding.aimPrice.clearFocus()
            }
        }

        binding.aimPrice.addTextChangedListener {
            binding.aimPrice.minWidth = 40
            it?.let {
                if (it.isNotEmpty() && it.toString().last() != ',') {
                    setSliderXToChangeEditText(binding.aimPrice.text.toString())
                }
            }
        }

        binding.aimPrice.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                val aimPriceValue = cleanInputAndToFloat(binding.aimPrice.text.toString())//binding.aimPrice.text.toString().replace("%", "").replace(" ","").toDouble()
                if (alarmType == AlarmTypeEnum.PriceOver || alarmType == AlarmTypeEnum.PriceUnder){
                    when {
                         aimPriceValue < (exchangeRateDTOForDetail!!.sellingRate * 0.8) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        aimPriceValue > (exchangeRateDTOForDetail!!.sellingRate * 1.2) -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                            binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        else -> {
                            setSliderXToChangeEditText(binding.aimPrice.text.toString())
                        }
                    }
                }
                else if (alarmType == AlarmTypeEnum.PercentOver){
                    when {
                        aimPriceValue < 0 -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        aimPriceValue > 20 -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                            binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        else -> {
                            setSliderXToChangeEditText(binding.aimPrice.text.toString())
                        }
                    }
                }
                else if (alarmType == AlarmTypeEnum.PercentUnder){
                    when {
                        aimPriceValue < -20 -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 0.8).toString())
                            binding.sliderCircle.x = ((binding.sliderCircleWay.width - binding.sliderWay.width - binding.sliderCircle.width) / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        aimPriceValue > 0 -> {
                            binding.aimPrice.setText((exchangeRateDTOForDetail!!.sellingRate * 1.2).toString())
                            binding.sliderCircle.x = (binding.sliderWay.width + binding.sliderCircle.width / 2).toFloat()
                            getResultSlider(binding.sliderCircle.x)
                        }
                        else -> {
                            setSliderXToChangeEditText(binding.aimPrice.text.toString())
                        }
                    }
                }
            }

        }

        binding.sliderCircleWay.setOnTouchListener {view, motionEvent ->
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

        binding.aimPrice.clearFocus()
    }

    private fun checkOnSliderWay(x:Float):Boolean{
        return x > ((binding.sliderCircleWay.width - binding.sliderWay.width) / 2) &&
                x < (binding.sliderWay.width + binding.sliderCircle.width)
    }

    @SuppressLint("SetTextI18n")
    private fun getResultSlider(x:Float){
        val result = (x / binding.sliderWay.width) - (binding.sliderCircleWay.width / (2 * binding.sliderWay.width) - 1/2)
        val decimalFactor = cleanInputAndToFloat(DecimalFormat("#.#").format(result * 20 - 0.4)).toDouble()
        exchangeRateDTOForDetail?.let {
            when (alarmType) {
                AlarmTypeEnum.PriceOver -> {
                    binding.aimPrice.setText(decimalFormat.format(it.sellingRate * (decimalFactor / 100 + 1)))
                }
                AlarmTypeEnum.PriceUnder -> {
                    binding.aimPrice.setText(decimalFormat.format((it.sellingRate ) * (0.8 + decimalFactor / 100)))
                }
                AlarmTypeEnum.PercentOver -> {
                    binding.aimPrice.setText("% ${DecimalFormat("#.#").format(decimalFactor)}")
                }
                AlarmTypeEnum.PercentUnder -> {
                    binding.aimPrice.setText("% ${DecimalFormat("#.#").format(decimalFactor - 20)}")
                }
            }
        }
    }

    private fun setSliderXToChangeEditText(targetValueText:String){
        val targetValue = cleanInputAndToFloat(targetValueText) //targetValueText.replace("%", "").replace(" ","").toDouble()
        exchangeRateDTOForDetail?.let {
            when (alarmType) {
                AlarmTypeEnum.PriceOver -> {
                    val decimalFactor = cleanInputAndToFloat(DecimalFormat("#.#").format(100 * ((targetValue / it.sellingRate) - 1)))
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PriceUnder -> {
                    val decimalFactor = cleanInputAndToFloat(DecimalFormat("#.#").format((100 *((targetValue/it.sellingRate) - 0.8))))
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PercentOver -> {
                    val result = (targetValue + 0.4) / 20
                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PercentUnder -> {
                    val decimalFactor = targetValue + 20
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
            }
        }
    }

    private fun setSliderXForEdit(alarmDTO: AlarmDTO){
        exchangeRateDTOForDetail?.let {
            val targetValue = alarmDTO.targetValue
            when (alarmDTO.alarmType) {
                AlarmTypeEnum.PriceOver -> {
                    val decimalFactor = cleanInputAndToFloat(DecimalFormat("#.#").format(100 * ((targetValue / it.sellingRate) - 1)))
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PriceUnder -> {
                    val decimalFactor = cleanInputAndToFloat(DecimalFormat("#.#").format((100 *((targetValue/it.sellingRate) - 0.8))))
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PercentOver -> {
                    val result = (targetValue + 0.4) / 20
                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
                AlarmTypeEnum.PercentUnder -> {
                    val decimalFactor = (targetValue * (-1)) + 20
                    val result = (decimalFactor + 0.4) / 20

                    binding.sliderCircle.x = (binding.sliderWay.width * result).toFloat()
                }
            }
        }
    }

    private fun setAlarmType(): AlarmTypeEnum{
        var result : AlarmTypeEnum = AlarmTypeEnum.PriceOver
        if (binding.ifUpButtonText.isSelected && binding.priceButton.isSelected){
            result = AlarmTypeEnum.PriceOver
        }
        else if (binding.ifDownButtonText.isSelected && binding.priceButton.isSelected){
            result = AlarmTypeEnum.PriceUnder
        }
        else if (binding.ifUpButtonText.isSelected && binding.percentButton.isSelected){
            result = AlarmTypeEnum.PercentOver
        }
        else if (binding.ifDownButtonText.isSelected && binding.percentButton.isSelected){
            result = AlarmTypeEnum.PercentUnder
        }
        return result
    }

    private fun setSelectButton(alarmTypeEnum: AlarmTypeEnum){
        when(alarmTypeEnum){
            AlarmTypeEnum.PriceOver ->{
                binding.priceButton.isSelected = true
                binding.percentButton.isSelected = false
                binding.ifUpButtonText.isSelected = true
                binding.ifDownButtonText.isSelected = false
            }
            AlarmTypeEnum.PriceUnder ->{
                binding.priceButton.isSelected = true
                binding.percentButton.isSelected = false
                binding.ifUpButtonText.isSelected = false
                binding.ifDownButtonText.isSelected = true
            }
            AlarmTypeEnum.PercentOver ->{
                binding.priceButton.isSelected = false
                binding.percentButton.isSelected = true
                binding.ifUpButtonText.isSelected = true
                binding.ifDownButtonText.isSelected = false
            }
            AlarmTypeEnum.PercentUnder ->{
                binding.priceButton.isSelected = false
                binding.percentButton.isSelected = true
                binding.ifUpButtonText.isSelected = false
                binding.ifDownButtonText.isSelected = true
            }
        }
    }


    private fun cleanInputAndToFloat(inputString : String):Float{
        var result = 0.0f
        try {
            var resultString = inputString.replace("%", "")
            resultString = resultString.replace(" ", "")
            resultString = resultString.replace(",", ".")
            result = resultString.toFloat()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return result
    }
}