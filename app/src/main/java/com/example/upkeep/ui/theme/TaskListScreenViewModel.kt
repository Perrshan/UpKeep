package com.example.upkeep.ui.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class TaskListScreenViewModel : ViewModel() {
    private val _selectedOption = mutableStateOf("All")
    val selectedOption: State<String> get() = _selectedOption

    fun updateSelectedOption(option: String) {
        _selectedOption.value = option
    }
}
