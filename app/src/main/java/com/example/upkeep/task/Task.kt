package com.example.upkeep.task

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Task(
    @StringRes val name: Int,
    @StringRes val description: Int,
    @StringRes val frequency: Int,
    @DrawableRes val imageResourceId: Int
)
