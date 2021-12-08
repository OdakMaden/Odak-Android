package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class SystemConfigurationDTO(
    /* Phone number (CCCCCAAATTTTTTT) */
    @SerializedName("PhoneNo01") val phoneNo01: String? = null,
    /* Phone number (CCCCCAAATTTTTTT) */
    @SerializedName("PhoneNo02") val phoneNo02: String? = null
)
