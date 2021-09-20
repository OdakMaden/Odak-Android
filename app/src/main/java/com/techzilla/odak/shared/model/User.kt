package com.techzilla.odak.shared.model

data class User(
    val userID : String,
    val isLoggedIn : Boolean,
    val favoriteCodeList : ArrayList<String>
)
