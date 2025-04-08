package com.example.upkeep.data

data class UpKeepUiState(
    val username: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val favoriteColor: String = "",
    val passwordVisible: Boolean = false,
    val correctCredentials: Boolean = false
)