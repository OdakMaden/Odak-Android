package com.techzilla.odak.currencydetail.viewcontroller

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.techzilla.odak.R
import com.techzilla.odak.databinding.ActivityCurrencyDetailBinding


class CurrencyDetailActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private var _binding : ActivityCurrencyDetailBinding? = null
    private val binding get() = _binding!!

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
            position = XAxis.XAxisPosition.BOTTOM
            enableGridDashedLine(10f, 0f, 0f)
            axisMaximum = 9f
            axisMinimum = 1f
            gridColor = resources.getColor(R.color.odak_chart_bakground_line_blue, resources.newTheme())
            textColor = Color.WHITE
        }
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.axisLeft.apply {
            enableGridDashedLine(10f,0f, 0f)
            gridColor = resources.getColor(R.color.odak_chart_bakground_line_blue, resources.newTheme())
            axisMaximum = 100f
            axisMinimum = 0f
            textColor = Color.WHITE
        }

        binding.lineChart.axisLeft.setDrawLimitLinesBehindData(true)
        binding.lineChart.xAxis.setDrawLimitLinesBehindData(true)

        setData(10, 90f)
        binding.lineChart.animateX(1500)
        binding.lineChart.legend.form = Legend.LegendForm.NONE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData(count: Int, range: Float) {
        val values: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() - 10
            values.add(Entry(i.toFloat(), `val`, resources.getDrawable(R.drawable.star, resources.newTheme())))
        }
        val set1: LineDataSet
        if (binding.lineChart.data != null &&
            binding.lineChart.data.dataSetCount > 0
        ) {
            set1 =
                binding.lineChart.data.getDataSetByIndex(0) as LineDataSet //.getDataSetByIndex(0)
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

    override fun onValueSelected(e: Entry?, h: Highlight?) {

        Log.i("Entry selected", e.toString())
        Log.i("LOW HIGH", "low: " + binding.lineChart.lowestVisibleX + ", high: " + binding.lineChart.highestVisibleX)
        Log.i("MIN MAX", "xMin: " + binding.lineChart.xChartMin + ", xMax: " + binding.lineChart.xChartMax + ", yMin: " + binding.lineChart.yChartMin + ", yMax: " + binding.lineChart.yChartMax)
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }
}