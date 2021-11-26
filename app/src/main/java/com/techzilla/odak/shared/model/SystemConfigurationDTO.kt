package com.techzilla.odak.shared.model

import com.google.gson.annotations.SerializedName

data class SystemConfigurationDTO(
    /* Phone number (CCCCCAAATTTTTTT) */
    val phoneNo01: String? = null,
    /* Phone number (CCCCCAAATTTTTTT) */
    val phoneNo02: String? = null
)
