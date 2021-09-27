package com.techzilla.odak.currencydetail.viewcontroller

import android.annotation.SuppressLint
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
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityCurrencyDetailBinding
import com.techzilla.odak.main.constant.isChangeInnerViewCurrencyModel
import com.techzilla.odak.shared.constants.USER
import com.techzilla.odak.shared.constants.list
import com.techzilla.odak.shared.model.CurrencyModel
import java.text.DecimalFormat


class CurrencyDetailActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private var _binding : ActivityCurrencyDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var currencyModel: CurrencyModel

    @SuppressLint("UseCompatLoadingForDrawables")
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

        val currencyCode = intent.getStringExtra("currencyCode")
        list.forEach {
            if (it.currencyCode == currencyCode){
                currencyModel = it
            }
        }

        binding.clockText.isSelected = true
        binding.title.text = currencyModel.currencyCode
        binding.subTitle.text = currencyModel.currencyName
        binding.buyText.text = currencyModel.buyPrice.toString()
        binding.sellText.text = currencyModel.salePrice.toString()
        if (USER.favoriteCodeList.contains(currencyModel.currencyCode)){
            binding.favorite.setImageDrawable(resources.getDrawable(R.drawable.icon_selected_favorite, resources.newTheme()))
        }
        else{
            binding.favorite.setImageDrawable(resources.getDrawable(R.drawable.icon_favorite, resources.newTheme()))
        }

        if (currencyModel.percentage.contains("-")) {
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
        binding.increaseText.text = currencyModel.percentage

        val decimalFormat = DecimalFormat("#.#####")

        binding.lowestSliderText.text = decimalFormat.format(currencyModel.salePrice / 1.01)
        binding.highestSliderText.text = decimalFormat.format(currencyModel.salePrice * 1.01)
        binding.highestText.text = decimalFormat.format(currencyModel.salePrice * 1.01)
        binding.lowestText.text = decimalFormat.format(currencyModel.salePrice / 1.01)

        object : CountDownTimer(100,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(currencyModel.salePrice, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

        object : CountDownTimer(2000,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(currencyModel.salePrice * 1.005, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

        object : CountDownTimer(5000,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(currencyModel.salePrice / 1.005, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

        object : CountDownTimer(8000,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(8.36, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

        object : CountDownTimer(11000,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(8.52804, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

        object : CountDownTimer(14000,100){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                changeSliderCirclePosition(currencyModel.salePrice, currencyModel.salePrice / 1.01, currencyModel.salePrice * 1.01)
            }
        }.start()

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



        val values = ArrayList<String>()
        for (i in 0..7){
            values.add(i, "$i.gün")
        }
        binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(values.toTypedArray())
        setData(values.size, 100f)

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

       // setData(30, 90f)
        binding.lineChart.animateX(1500)
        binding.lineChart.legend.form = Legend.LegendForm.NONE

        binding.clockText.setOnClickListener {
            selectedDateButton(it as TextView)

            val values = ArrayList<String>()
            for (i in 0..60){
                values.add(i, "$i.dakika")
            }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(values.toTypedArray())
            setData(values.size, 100f)

            binding.lineChart.invalidate()
        }
        binding.dayText.setOnClickListener {
            selectedDateButton(it as TextView)

            val values = ArrayList<String>()
            for (i in 0..24){
                values.add(i, "$i.saat")
            }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(values.toTypedArray())
            setData(values.size, 100f)

            binding.lineChart.invalidate()
        }
        binding.weekText.setOnClickListener {
            selectedDateButton(it as TextView)

            val values = ArrayList<String>()
            for (i in 0..7){
                values.add(i, "$i.gün")
            }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(values.toTypedArray())
            setData(values.size, 100f)

            binding.lineChart.invalidate()
        }
        binding.monthText.setOnClickListener {
            selectedDateButton(it as TextView)

            val values = ArrayList<String>()
            for (i in 0..30){
                values.add(i, "$i.GÜN")
            }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(values.toTypedArray())
            setData(values.size, 100f)

            binding.lineChart.invalidate()
        }

        binding.favorite.setOnClickListener {
            if (USER.favoriteCodeList.contains(currencyModel.currencyCode)) {
                USER.favoriteCodeList.remove(currencyModel.currencyCode)
                binding.favorite.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.icon_favorite,
                        resources.newTheme()
                    )
                )
            } else {
                USER.favoriteCodeList.add(currencyModel.currencyCode)
                binding.favorite.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.icon_selected_favorite,
                        resources.newTheme()
                    )
                )
            }
            isChangeInnerViewCurrencyModel = currencyModel
        }
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
    private fun setData(count: Int, range: Float) {
        val values: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat()
            values.add(Entry(i.toFloat(), `val`, resources.getDrawable(R.drawable.star, resources.newTheme())))
        }
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

    private fun changeSliderCirclePosition(salePrice: Double, minSalePrice: Double, maxSalePrice:Double){
        binding.sliderCircle.animate().translationX((binding.sliderWay.width * ((salePrice - minSalePrice) / (maxSalePrice - minSalePrice))).toFloat())
            .setInterpolator(AccelerateInterpolator()).setDuration(500).start()
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