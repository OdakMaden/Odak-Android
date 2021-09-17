package com.techzilla.odak.shared.model

data class CurrencyModel(
    val currencyName: String,
    val currencyCode: String,
    val salePrice : String,
    val buyPrice : String,
    val percentage : String,
    var currencyType : Int = 0
)

data class InnerViewCurrencyModel(
    val currencyModel: CurrencyModel,
    var isSelected : Boolean = false
)