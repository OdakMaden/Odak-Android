package com.techzilla.odak.currencydetail.viewcontroller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Build
import android.os.Bundle
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
import com.techzilla.odak.shared.constants.odakTimePattern
import com.techzilla.odak.shared.constants.rememberMemberDTO
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.GraphPeriodEnum
import com.techzilla.odak.shared.service.repository.GraphRepository
import org.json.JSONObject
import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CurrencyDetailActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private var _binding : ActivityCurrencyDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var exchangeRateDTO: ExchangeRateDTO

    private val repository by lazy { GraphRepository() }

    private val percentage = "0"// silinecek
    private var graphPeriodEnum = GraphPeriodEnum.Hour
    private val values = ArrayList<Entry>()
    private var isFavorite : Boolean = false

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == RESULT_OK){
            AlertDialog.Builder(this).setTitle("Alarm").setMessage("Alarm Başarılı Olarak Kaydedilmiştir.").setPositiveButton("Tamam"
            ) { dialog, p1 ->  dialog.dismiss()}.show()
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
            val calendar = Calendar.getInstance()
            repository.getGraphData(it.code, graphPeriodEnum.value,
                SimpleDateFormat(odakTimePattern).format(calendar.time))
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
            binding.sellText.text = it.meanValue.toString()
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
            setData(values)
            binding.lineChart.invalidate()

        })

        if (percentage.contains("-")) {
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
        binding.increaseText.text = percentage

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
            setLabelCount(4, false)
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
            selectedDateButton(it as TextView)

            graphPeriodEnum = GraphPeriodEnum.Hour

            val calendar = Calendar.getInstance()
            repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                SimpleDateFormat(odakTimePattern).format(calendar.time))
        }
        binding.dayText.setOnClickListener {
            selectedDateButton(it as TextView)

            graphPeriodEnum = GraphPeriodEnum.Day

            val calendar = Calendar.getInstance()
            repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                SimpleDateFormat(odakTimePattern).format(calendar.time))
        }
        binding.weekText.setOnClickListener {
            selectedDateButton(it as TextView)

            graphPeriodEnum = GraphPeriodEnum.Week

            val calendar = Calendar.getInstance()
            repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                SimpleDateFormat(odakTimePattern).format(calendar.time))
        }
        binding.monthText.setOnClickListener {
            selectedDateButton(it as TextView)

            graphPeriodEnum = GraphPeriodEnum.Month
            val calendar = Calendar.getInstance()
            repository.getGraphData(exchangeRateDTO.code, graphPeriodEnum.value,
                SimpleDateFormat(odakTimePattern).format(calendar.time))
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
    private fun setData(values : ArrayList<Entry>) {
        changeChartY(graphPeriodEnum, values.size)

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
            set1.valueTextSize = 9f
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

    private fun changeChartY(type:GraphPeriodEnum, size : Int){
        val value = ArrayList<String>()
        println(size)
        when(type){
            GraphPeriodEnum.Hour -> {
                for (i in 0..size){
                    value.add(i, "$i.dakika")
                }
            }
            GraphPeriodEnum.Day ->{
                for (i in 0..size){
                    value.add(i, "$i.saat")
                }
            }
            GraphPeriodEnum.Week ->{
                for (i in 0..size){
                    value.add(i, "$i.gün")
                }
            }
            GraphPeriodEnum.Month ->{
                for (i in 0..size){
                    value.add(i, "$i.GÜN")
                }
            }
        }

        binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(value.toTypedArray())
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
                repository.updateMemberDTO(updateMemberDTO)
            }
            else{
                val updateMemberDTO = JsonObject()
                updateMemberDTO.addProperty("MemberData", """{"favoriteIdList": "$favoriteCodeId"}""")

                println(updateMemberDTO)
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