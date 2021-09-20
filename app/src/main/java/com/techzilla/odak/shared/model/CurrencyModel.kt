package com.techzilla.odak.shared.model

data class CurrencyModel(
    val currencyName: String,
    val currencyCode: String,
    val salePrice : Double,
    val buyPrice : Double,
    val percentage : String,
    var currencyType : Int = 0
)