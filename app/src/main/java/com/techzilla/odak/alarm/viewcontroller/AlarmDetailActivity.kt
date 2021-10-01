package com.techzilla.odak.alarm.viewcontroller

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.currencyModelForDetail
import com.techzilla.odak.databinding.ActivityAlarmDetailBinding
import java.text.DecimalFormat

class AlarmDetailActivity : AppCompatActivity() {

    private val binding : ActivityAlarmDetailBinding by lazy { ActivityAlarmDetailBinding.inflate(layoutInflater) }

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

        if (currencyModelForDetail == null){
            Toast.makeText(this, "Bir hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
            finish()
        }

        currencyModelForDetail?.let {
            binding.currencyCode.text = it.currencyCode
            binding.currencyName.text = it.currencyName
            binding.lastPriceLabel.text = "${resources.getString(R.string.alarm_lastPrice_label)} ${decimalFormat.format(it.salePrice)}"
            binding.aimPrice.setText(decimalFormat.format(it.salePrice))
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

        binding.createAlarmButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }


        binding.aimPrice.addTextChangedListener {
            binding.aimPrice.minWidth = 40
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
        currencyModelForDetail?.let {
            if (isPrice && !isDistance){
                if (isIfUp && !isIfDown){
                    binding.aimPrice.setText(decimalFormat.format(it.salePrice * (decimalFactor / 100 + 1)))
                }
                else if (!isIfUp && isIfDown){
                    binding.aimPrice.setText(decimalFormat.format((it.salePrice ) * (0.8 + decimalFactor / 100)))
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
}