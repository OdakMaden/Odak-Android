package com.techzilla.odak.shared.model

data class ConverterCurrency(
    val currencyName: String,
    val currencyCode: String,
    val salePrice : Double,
    var currencyType : Int = 0
)
