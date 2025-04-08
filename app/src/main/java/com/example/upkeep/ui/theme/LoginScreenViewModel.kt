package com.example.upkeep.ui.theme
import androidx.lifecycle.ViewModel
import com.example.upkeep.data.UpKeepUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UpKeepUiState())
    val uiState: StateFlow<UpKeepUiState> = _uiState

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun updateFirstName(firstName: String) {
        _uiState.update { it.copy(firstName = firstName) }
    }

    fun updateLastName(lastName: String) {
        _uiState.update { it.copy(lastName = lastName) }
    }

    fun updateFavoriteColor(favoriteColor: String) {
        _uiState.update { it.copy(favoriteColor = favoriteColor) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun checkCredentials(username: String, password: String) {

        val correct = (_uiState.value.username == username && _uiState.value.password == password)
        _uiState.update { it.copy(correctCredentials = correct) }
    }

    fun LoginUiState () {
        _uiState.update { it.copy(username = "") }
        _uiState.update { it.copy(password = "") }
        _uiState.update { it.copy(correctCredentials = false) }
    }
}