package com.techzilla.odak.shared.model

data class MemberCreateDTO(
    /* First name of the member */
    val firstName: String,
    /* Last name of the member */
    val lastName: String,
    /* GSM No of the member (CCCCCAAATTTTTTT) */
    val gSMNo: String,
    /* Email address of the member */
    val email: String,
    /* Password of the member */
    val password: String? = null,
    /* Free storage for member specific data (preferably in JSON) */
    val memberData: String? = null,
    /* FCM Registration Token */
    val fCMToken: String? = null
)
