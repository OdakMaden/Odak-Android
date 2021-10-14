package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class ComplaintDTO(
    @SerializedName("ComplaintType") val complaintType: ComplaintTypeEnum,
    @SerializedName("ComplaintText") val complaintText: String
)
