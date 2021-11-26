package com.techzilla.odak.currencydetail.viewcontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.JsonObject
import com.techzilla.odak.R
import com.techzilla.odak.alarm.constant.exchangeRateDTOForDetail
import com.techzilla.odak.alarm.viewcontroller.AlarmDetailActivity
import com.techzilla.odak.converter.viewcontrollers.ConverterActivity
import com.techzilla.odak.databinding.ActivityCurrencyDetailBinding
import com.techzilla.odak.main.constant.isAddFavorite
import com.techzilla.odak.main.constant.isChangeInnerViewCurrencyModel
import com.techzilla.odak.shared.constants.dateFormatISO8601ToTimeStamp
import com.techzilla.odak.shared.constants.exchangeRateDTOListMap
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.constants.toDateFormatISO8601
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.GraphPeriodEnum
import com.techzilla.odak.shared.model.TimeStamp
import com.techzilla.odak.shared.service.repository.GraphRepository
import com.techzilla.odak.shared.viewcontroller.AlertDialogViewController
import org.json.JSONObject
import org.json.JSONTokener
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class CurrencyDetailActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private var _binding : ActivityCurrencyDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var exchangeRateDTO: ExchangeRateDTO

    private val repository by lazy { GraphRepository() }

    private var graphPeriodEnum = GraphPeriodEnum.Hour
    private val values = ArrayList<Entry>()
    private var isFavorite : Boolean = false
    private var isRunGraphUpdateTimer : Boolean = true

    private var graphUpdateTimer = object : CountDownTimer(15000, 15000){
        override fun onTick(millisUntilFinished: Long) {
        }

        @SuppressLint("SimpleDateFormat")
        override fun onFinish() {
            repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                    Calendar.getInstance().dateFormatISO8601ToTimeStamp())
        }
    }

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == RESULT_OK){
            AlertDialogViewController.buildAlertDialog(this, resources.getString(R.string.alert_alarm_title),
                resources.getString(R.string.alert_alarm_message), "", "", resources.getString(R.string.shared_Ok))
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCurrencyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.ime())
        }
        else{
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        exchangeRateDTOForDetail?.let {
            exchangeRateDTO = it
            binding.clockText.isSelected = true
            binding.title.text = it.code
            binding.subTitle.text = it.name
            binding.buyText.text = it.buyingRate.toString()
            binding.sellText.text = it.sellingRate.toString()
            setChangePercentage(it.changePercentage)


            repository.getGraphData(it.code, graphPeriodEnum.value,
                    Calendar.getInstance().dateFormatISO8601ToTimeStamp()
            )

            updatePeriodic()
        }

        if (rememberMemberDTO!!.memberData != null) {
            val memberDataJSON =
                JSONTokener(rememberMemberDTO!!.memberData).nextValue() as JSONObject
            val favoriteIdList = memberDataJSON.getString("favoriteIdList")
            isFavorite = if (favoriteIdList.contains(exchangeRateDTO.code)){
                binding.favorite.setImageDrawable(resources.getDrawable(R.drawable.icon_selected_favorite, resources.newTheme()))
                true
            } else{
                binding.favorite.setImageDrawable(resources.getDrawable(R.drawable.icon_favorite, resources.newTheme()))
                false
            }
        }

        repository.exchangeRateGraphLiveData.observe(this, {
            //binding.sellText.text = it.meanValue.toString()
            binding.lowestSliderText.text = it.lowestValue.toString()
            binding.lowestText.text = it. lowestValue.toString()
            binding.highestText.text = it.highestValue.toString()
            binding.highestSliderText.text = it.highestValue.toString()
            changeSliderCirclePosition(it.meanValue, it.lowestValue, it.highestValue)
            values.clear()
            var termFloat = 0.0f
            it.data.forEach { data ->
                values.add(Entry(termFloat, data, resources.getDrawable(R.drawable.star, resources.newTheme())))
                termFloat++
            }
            setData(values, it.baseTimeStamp)
            binding.lineChart.invalidate()
            if (isRunGraphUpdateTimer){
                graphUpdateTimer.start()
            }
            else{
                graphUpdateTimer.cancel()
            }
        })

        if (exchangeRateDTO.changePercentage < 0) {
            binding.increase.setImageDrawable(
                resources.getDrawable(
                    R.drawable.icon_increase_down,
                    resources.newTheme()
                )
            )
            binding.increaseText.setTextColor(resources.getColor(R.color.odak_red, resources.newTheme()))
        }
        else{
            binding.increase.setImageDrawable(
                resources.getDrawable(
                    R.drawable.icon_increase_up,
                    resources.newTheme()
                )
            )
            binding.increaseText.setTextColor(resources.getColor(R.color.odak_green, resources.newTheme()))
        }
        binding.increaseText.text = exchangeRateDTO.changePercentage.toString()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //LineChartActivity1
        binding.lineChart.setBackgroundColor(resources.getColor(android.R.color.transparent, resources.newTheme()))
        binding.lineChart.description.isEnabled = false
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.setOnChartValueSelectedListener(this)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setPinchZoom(true)

        binding.lineChart.xAxis.apply {
            setDrawGridLines(false)
            textColor = Color.WHITE
            labelRotationAngle = -90f
            setLabelCount(4, true)
            position = XAxis.XAxisPosition.BOTTOM
        }


        binding.lineChart.axisRight.isEnabled = false

        binding.lineChart.axisLeft.apply {
            setDrawGridLines(false)
            setLabelCount(10, false)
            textColor = Color.WHITE
        }


        binding.lineChart.axisLeft.setDrawLimitLinesBehindData(true)
        binding.lineChart.xAxis.setDrawLimitLinesBehindData(true)
        binding.lineChart.animateX(1500)
        binding.lineChart.legend.form = Legend.LegendForm.NONE

        binding.clockText.setOnClickListener {
            if (!binding.clockText.isSelected) {
                graphUpdateTimer.cancel()
                isRunGraphUpdateTimer = true
                selectedDateButton(it as TextView)

                graphPeriodEnum = GraphPeriodEnum.Hour

                repository.getGraphData(
                        exchangeRateDTO.code, graphPeriodEnum.value,
                        Calendar.getInstance().dateFormatISO8601ToTimeStamp()
                )
            }
        }

        binding.dayText.setOnClickListener {
            if (!binding.dayText.isSelected) {
                graphUpdateTimer.cancel()
                isRunGraphUpdateTimer = true
                selectedDateButton(it as TextView)

                graphPeriodEnum = GraphPeriodEnum.Day

                repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                        Calendar.getInstance().dateFormatISO8601ToTimeStamp()
                )
            }
        }

        binding.weekText.setOnClickListener {
            if (!binding.weekText.isSelected) {
                graphUpdateTimer.cancel()
                isRunGraphUpdateTimer = false
                selectedDateButton(it as TextView)

                graphPeriodEnum = GraphPeriodEnum.Week

                repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                        Calendar.getInstance().dateFormatISO8601ToTimeStamp()
                )
            }
        }
        binding.monthText.setOnClickListener {
            if (!binding.monthText.isSelected) {
                graphUpdateTimer.cancel()
                isRunGraphUpdateTimer = false
                selectedDateButton(it as TextView)

                graphPeriodEnum = GraphPeriodEnum.Month
                repository.getGraphData(
                        exchangeRateDTO.code, graphPeriodEnum.value,
                        Calendar.getInstance().dateFormatISO8601ToTimeStamp()
                )
            }
        }

        binding.favorite.setOnClickListener {
            if (isFavorite) {
                binding.favorite.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.icon_favorite,
                        resources.newTheme()
                    )
                )
                deleteFavoriteList(exchangeRateDTO.code)
                isAddFavorite = false
            } else {
                binding.favorite.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.icon_selected_favorite,
                        resources.newTheme()
                    )
                )
                addFavoriteList(exchangeRateDTO.code)
                isAddFavorite = true
            }
            isChangeInnerViewCurrencyModel = exchangeRateDTO
        }

        binding.converter.setOnClickListener {
            startResult.launch(Intent(this, ConverterActivity::class.java).also {
                exchangeRateDTOForDetail = exchangeRateDTO
            })
        }

        binding.alarm.setOnClickListener {
            startResult.launch(Intent(this, AlarmDetailActivity::class.java).also {
                exchangeRateDTOForDetail = exchangeRateDTO
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        exchangeRateDTOForDetail = null
        graphUpdateTimer.cancel()
    }

    private fun selectedDateButton(textView: TextView){
        binding.clockText.isSelected = false
        binding.dayText.isSelected = false
        binding.weekText.isSelected = false
        binding.monthText.isSelected = false
        when(textView){
            binding.clockText ->{
                binding.clockText.isSelected = true
            }
            binding.dayText ->{
                binding.dayText.isSelected = true
            }
            binding.weekText ->{
                binding.weekText.isSelected = true
            }
            binding.monthText->{
                binding.monthText.isSelected = true
            }
        }
        binding.selectedDate.text = textView.text
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData(values: ArrayList<Entry>, timestamp: TimeStamp) {
        changeChartY(graphPeriodEnum, values.size, timestamp)

        val set1: LineDataSet
        if (binding.lineChart.data != null &&
            binding.lineChart.data.dataSetCount > 0
        ) {
            set1 =
                binding.lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.lineChart.data.notifyDataChanged()
            binding.lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 0f, 0f)
            // black lines and points
            set1.color = resources.getColor(R.color.odak_line_blue, resources.newTheme())
            //set1.setCircleColor(Color.WHITE)
            //set1.setDrawCircleHole(false)
            set1.setDrawCircles(false)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            // draw points as solid circles
            set1.setDrawCircleHole(true)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 0f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 11f
            set1.setDrawValues(false)

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> binding.lineChart.axisLeft.axisMinimum }

            val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
            set1.fillDrawable = drawable
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            binding.lineChart.data = data
        }
    }

    private fun changeSliderCirclePosition(salePrice: Float, minSalePrice: Float, maxSalePrice:Float){
        binding.sliderCircle.animate().translationX((binding.sliderWay.width * ((salePrice - minSalePrice) / (maxSalePrice - minSalePrice))))
            .setInterpolator(AccelerateInterpolator()).setDuration(500).start()
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeChartY(type:GraphPeriodEnum, size : Int, timestamp: TimeStamp){
        val value = ArrayList<String>()
        val calendar = timestamp.toDateFormatISO8601()
        calendar?.let { calendarTerm ->
            when (type) {
                GraphPeriodEnum.Hour -> {
                    calendarTerm.add(Calendar.MINUTE, (size * -1))
                    for (i in 0..size) {
                        calendarTerm.add(Calendar.MINUTE, 1)
                        val hour = if (calendarTerm[Calendar.HOUR_OF_DAY] < 10) {
                            "0${calendarTerm[Calendar.HOUR_OF_DAY]}"
                        } else {
                            calendarTerm[Calendar.HOUR_OF_DAY]
                        }
                        val minute = if (calendarTerm[Calendar.MINUTE] < 10) {
                            "0${calendarTerm[Calendar.MINUTE]}"
                        } else {
                            calendarTerm[Calendar.MINUTE]
                        }

                        value.add(i, "${hour}:${minute}")

                    }
                }
                GraphPeriodEnum.Day -> {
                    calendarTerm.add(Calendar.MINUTE, (size * -15))
                    for (i in 0..(size)) {//96
                        calendarTerm.add(Calendar.MINUTE, 15)
                        val hour = if (calendarTerm[Calendar.HOUR_OF_DAY] < 10) {
                            "0${calendarTerm[Calendar.HOUR_OF_DAY]}"
                        } else {
                            calendarTerm[Calendar.HOUR_OF_DAY]
                        }
                        val minute = if (calendarTerm[Calendar.MINUTE] < 10) {
                            "0${calendarTerm[Calendar.MINUTE]}"
                        } else {
                            calendarTerm[Calendar.MINUTE]
                        }
                        value.add(i, "${hour}:${minute}")
                    }
                }
                GraphPeriodEnum.Week -> {
                    calendarTerm.add(Calendar.HOUR_OF_DAY, (size * -1))
                    for (i in 0..size) {//168
                        calendarTerm.add(Calendar.HOUR_OF_DAY, 1)
                        val day = if (calendarTerm[Calendar.DAY_OF_MONTH] < 10) {
                            "0${calendarTerm[Calendar.DAY_OF_MONTH]}"
                        } else {
                            "${calendarTerm[Calendar.DAY_OF_MONTH]}"
                        }
                        val month = if (calendarTerm[Calendar.MONTH] < 10) {
                            "0${calendarTerm[Calendar.MONTH]}"
                        } else {
                            "${calendarTerm[Calendar.MONTH]}"
                        }
                        value.add(i, "${day}/${month}")
                    }
                }
                GraphPeriodEnum.Month -> {
                    calendarTerm.add(Calendar.HOUR_OF_DAY, (size * -3))
                    for (i in 0..size) {
                        calendarTerm.add(Calendar.HOUR_OF_DAY, 3)
                        val day = if (calendarTerm[Calendar.DAY_OF_MONTH] < 10) {
                            "0${calendarTerm[Calendar.DAY_OF_MONTH]}"
                        } else {
                            "${calendarTerm[Calendar.DAY_OF_MONTH]}"
                        }
                        val month = if (calendarTerm[Calendar.MONTH] < 10) {
                            "0${calendarTerm[Calendar.MONTH]}"
                        } else {
                            "${calendarTerm[Calendar.MONTH]}"
                        }
                        value.add(i, "${day}/${month}")
                    }
                }
            }
        }

        binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(value.toTypedArray())
    }

    private fun updatePeriodic(){
        object : CountDownTimer(3000, 3000){
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                exchangeRateDTOListMap[exchangeRateDTO.code]?.let{
                    exchangeRateDTO = it
                    setChangePercentage(it.changePercentage)
                    binding.buyText.text = it.buyingRate.toString()
                    binding.sellText.text = it.sellingRate.toString()

                    updatePeriodic()
                }
            }
        }.start()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun setChangePercentage(changePercentage:Float){
        if (changePercentage < 0) {
            binding.increase.setImageDrawable(
                resources.getDrawable(
                    R.drawable.icon_increase_down,
                    resources.newTheme()
                )
            )
            binding.increaseText.setTextColor(resources.getColor(R.color.odak_red, resources.newTheme()))
        }
        else{
            binding.increase.setImageDrawable(
                resources.getDrawable(
                    R.drawable.icon_increase_up,
                    resources.newTheme()
                )
            )
            binding.increaseText.setTextColor(resources.getColor(R.color.odak_green, resources.newTheme()))
        }
        binding.increaseText.text = "% ${DecimalFormat("#.##").format(changePercentage)}"
    }

    private fun addFavoriteList(favoriteCodeId:String){
        rememberMemberDTO?.let {
            if (it.memberData != null){
                val memberDataJSON = JSONTokener(it.memberData).nextValue() as JSONObject
                var favoriteIdList = memberDataJSON.getString("favoriteIdList")
                favoriteIdList += ",$favoriteCodeId"
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteIdList"}""")
                repository.updateMemberDTO(updateMemberDTO)
            }
            else{
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteCodeId"}""")
                repository.updateMemberDTO(updateMemberDTO)
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
                    repository.updateMemberDTO(updateMemberDTO)
                }
            }
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.i("Entry selected", e.toString())
        Log.i("LOW HIGH", "low: " + binding.lineChart.lowestVisibleX + ", high: " + binding.lineChart.highestVisibleX)
        Log.i("MIN MAX", "xMin: " + binding.lineChart.xChartMin + ", xMax: " + binding.lineChart.xChartMax + ", yMin: " + binding.lineChart.yChartMin + ", yMax: " + binding.lineChart.yChartMax)
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }
}