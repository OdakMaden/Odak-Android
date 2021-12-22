package com.techzilla.odak.shared.constants

import android.annotation.SuppressLint
import com.techzilla.odak.shared.model.ExchangeRateDTO
import com.techzilla.odak.shared.model.MemberDTO
import com.techzilla.odak.shared.model.TimeStamp
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


const val BASEURL:String = "http://odakapidev.techzillalabs.com/AppBackend/V1/"
const val apiKey:String = "X-API-KEY: eR8E88d2zRLTSHH6fyjjF8H66b4MRCVO"
const val sessionKey:String = "X-SESSION-KEY: 4lUVgGTdD2dgM42O5yiBwNvK9kMqqZB6Y4hhjUDAhGap63BIonIH2Y9QFm68DFpE"
const val application:String = "accept: application/json"

var rememberMemberDTO : MemberDTO? = null

const val odakTimePattern:String = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
const val odakTimePattern2:String = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZZZZZ"
const val timePatternYearMountDayHourMinuteSecond:String = "yyyy-MM-dd'T'HH:mm:ss"

val exchangeRateListForIndexMap = HashMap<String, Int>()
val exchangeRateList = ArrayList<ExchangeRateDTO>()
val exchangeRateDTOListMap = HashMap<String, ExchangeRateDTO>()

var phoneNumber1 : String? = null
var refreshInterval : Int? = null

@SuppressLint("SimpleDateFormat")
fun Calendar.dateFormatISO8601ToTimeStamp(): TimeStamp {
    return try {
        SimpleDateFormat(odakTimePattern2).format(this.time)
    } catch (e : Exception){
        e.printStackTrace()
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun String.toDateFormatISO8601(): Calendar?{
    return try {
        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat(odakTimePattern2).parse(this)
        date?.let {
            calendar.time = it
        }
        calendar
    } catch (e : Exception){
        e.printStackTrace()
        null
    }
}