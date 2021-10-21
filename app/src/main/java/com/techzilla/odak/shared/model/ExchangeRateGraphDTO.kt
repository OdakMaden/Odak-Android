package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class ExchangeRateGraphDTO(
    @SerializedName("CurrencyCode") val currencyCode:String,
    @SerializedName("BaseTimeStamp") val baseTimeStamp: TimeStamp,
    @SerializedName("TimePeriod") val timePeriod:GraphPeriodEnum,
    @SerializedName("LowestValue") val lowestValue: Float,
    @SerializedName("HighestValue") val highestValue:Float,
    @SerializedName("MeanValue") val meanValue:Float,
    @SerializedName("Data") val data : List<Float>
)
