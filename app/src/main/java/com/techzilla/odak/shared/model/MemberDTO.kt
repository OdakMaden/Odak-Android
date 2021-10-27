package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class MemberDTO(
    @SerializedName("FieldsToNull") val fieldsToNull:String,
    @SerializedName("FirstName") val firstName:String,
    @SerializedName("LastName") val lastName:String,
    @SerializedName("GSMNo") val gSMNo:String,
    @SerializedName("MemberID") val memberID:String,
    @SerializedName("IsActive") val isActive:Boolean,
    @SerializedName("IsPushMessageAllowed") val isPushMessageAllowed:Boolean,
    @SerializedName("IsVerified") val isVerified:Boolean,
    @SerializedName("IsTester") val isTester:Boolean,
    @SerializedName("Email") val email:String,
    @SerializedName("Password") val password:String,
    @SerializedName("MemberData") var memberData:String?,
    @SerializedName("FCMToken") val fCMToken:String,
    @SerializedName("CreationTimeStamp") val creationTimeStamp:TimeStamp,
    @SerializedName("ModificationTimeStamp") val modificationTimeStamp:TimeStamp,
    @SerializedName("OTPTimeStamp") val OTPTimeStamp:TimeStamp,
)
