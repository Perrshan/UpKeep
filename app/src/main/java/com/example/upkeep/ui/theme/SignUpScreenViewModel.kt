package com.example.upkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class SignUpViewModel : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _isPasswordVisible = mutableStateOf(false)
    val isPasswordVisible: State<Boolean> = _isPasswordVisible

    private val _isConfirmPasswordVisible = mutableStateOf(false)
    val isConfirmPasswordVisible: State<Boolean> = _isConfirmPasswordVisible

    private val _showValidationMessage = mutableStateOf(false)
    val showValidationMessage: State<Boolean> = _showValidationMessage

    private val _passwordsMatch = mutableStateOf(true)
    val passwordsMatch: State<Boolean> = _passwordsMatch

    private val _accountCreated = mutableStateOf(false)
    val accountCreated: State<Boolean> = _accountCreated

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validatePasswords()
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        validatePasswords()
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
    }

    private fun validatePasswords() {
        _passwordsMatch.value = _password.value == _confirmPassword.value
        if (_showValidationMessage.value) {
            _showValidationMessage.value = !_passwordsMatch.value
        }
    }

    fun onSignUpClick() {
        validatePasswords()
        if (_passwordsMatch.value) {
            _accountCreated.value = true
        } else {
            _showValidationMessage.value = true
        }
    }
}
