package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class AlarmDTO(
    @SerializedName("FieldsToNull") val fieldsToNull:String,
    @SerializedName("RID") val rID:Int,
    @SerializedName("Name") val name:String,
    @SerializedName("CurrencyCode") val currencyCode:String,
    @SerializedName("AlarmType") val alarmType:AlarmTypeEnum,
    @SerializedName("ReferenceValue") val referenceValue:Double,
    @SerializedName("TargetValue") val targetValue:Double,
    @SerializedName("CreationTimeStamp") val creationTimeStamp:TimeStamp,
    @SerializedName("ModificationTimeStamp") val modificationTimeStamp:TimeStamp
)
