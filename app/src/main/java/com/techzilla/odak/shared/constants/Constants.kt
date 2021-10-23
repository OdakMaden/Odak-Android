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

val exchangeRateListForIndexMap = HashMap<String, Int>()
val exchangeRateList = ArrayList<ExchangeRateDTO>()
val exchangeRateDTOListMap = HashMap<String, ExchangeRateDTO>()
