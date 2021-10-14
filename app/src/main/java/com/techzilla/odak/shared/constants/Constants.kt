package com.techzilla.odak.shared.constants

import com.techzilla.odak.shared.model.ConverterCurrency
import com.techzilla.odak.shared.model.CurrencyModel
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO


const val BASEURL:String = "http://odakapidev.techzillalabs.com/AppBackend/V1/"
const val apiKey:String = "X-API-KEY: eR8E88d2zRLTSHH6fyjjF8H66b4MRCVO"
const val sessionKey:String = "X-SESSION-KEY: 4lUVgGTdD2dgM42O5yiBwNvK9kMqqZB6Y4hhjUDAhGap63BIonIH2Y9QFm68DFpE"
const val application:String = "accept: application/json"

var rememberMemberDTO : MemberDTO? = null

const val odakTimePattern:String = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
const val timePatternYearMountDayHourMinuteSecond:String = "yyyy-MM-dd'T'HH:mm:ss"

var exchangeRateList = ArrayList<ExchangeRateDTO>()

val list : MutableList<CurrencyModel> = mutableListOf(
    CurrencyModel("AMERİKAN DOLARI", "USD", 8.4436, 8.4431, "% -0,08", 0),
    CurrencyModel("EURO", "EUR/TRY", 8.4436, 8.4431, "% 0,08", 0),
    CurrencyModel("İNGİLİZ STERLİNİ", "GBP/TRY", 8.4436, 8.4431, "% -0,08", 0),
    CurrencyModel("İSVEÇ FRANKI", "CHF/TRY", 8.4436, 8.4431, "% 0,08", 0),
    CurrencyModel("", "GR ALTIN", 8.4436, 8.4431, "% -0,08", 1),
    CurrencyModel("", "HAS ALTIN", 8.4436, 8.4431, "% 0,08", 1),
    CurrencyModel("", "22 AYAR", 8.4436, 8.4431, "% -0,08", 1),
    CurrencyModel("BITCOIN", "BTC/TRY", 8.4436, 8.4431, "% -0,08", 2),
    CurrencyModel("CARDANO", "ADA/TRY", 8.4436, 8.4431, "% 0,08", 2),
    CurrencyModel("COSMOS", "ATOM/TRY", 8.4436, 8.4431, "% -0,08", 2),
    CurrencyModel("ETHEREUM", "ETH/TRY", 8.4436, 8.4431, "% 0,08", 2)
)

val converterList : MutableList<ConverterCurrency> = mutableListOf(
    ConverterCurrency("TÜRK LİRASI", "TRY", 1.00, 0),
    ConverterCurrency("AMERİKAN DOLARI", "USD", 8.8906, 0),
    ConverterCurrency("EURO", "EUR", 10.4436, 0),
    ConverterCurrency("İNGİLİZ STERLİNİ", "GBP", 12.4436, 0),
    ConverterCurrency("İSVEÇ FRANKI", "CHF", 9.4436, 0),
    ConverterCurrency("TÜRK LİRASI", "TRY", 1.00, 1),
    ConverterCurrency("", "GR ALTIN", 514.00, 1),
    ConverterCurrency("", "HAS ALTIN", 945.99, 1),
    ConverterCurrency("", "22 AYAR", 1453.00, 1),
    ConverterCurrency("TÜRK LİRASI", "TRY", 1.00, 2),
    ConverterCurrency("BITCOIN", "BTC", 245546.00, 2),
    ConverterCurrency("CARDANO", "ADA", 121326.56, 2),
    ConverterCurrency("COSMOS", "ATOM", 32356.00, 2),
    ConverterCurrency("ETHEREUM", "ETH", 23488.59, 2)
)