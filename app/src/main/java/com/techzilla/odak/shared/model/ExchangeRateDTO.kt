package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class ExchangeRateDTO(
    @SerializedName("Code") val code:String,
    @SerializedName("Name") val name:String,
    @SerializedName("Description") val description:String,
    @SerializedName("CurrencyType") val currencyType:CurrencyTypeEnum,
    @SerializedName("LastChangeTimeStamp") val lastChangeTimeStamp:TimeStamp,
    @SerializedName("SellingRate") val sellingRate:Float,
    @SerializedName("BuyingRate") val buyingRate:Float
)
