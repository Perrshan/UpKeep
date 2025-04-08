package com.example.upkeep.ui.theme

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.upkeep.viewmodel.SignUpViewModel
import com.example.upkeep.data.UserHistoryDatabase

@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel) {

    val dbHelper = UserHistoryDatabase(context = LocalContext.current)
    val usernames = dbHelper.getAllUserHistories()
    val usernamesMatch = remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.accountCreated.value) {
        if (viewModel.accountCreated.value && !usernamesMatch.value) {
            dbHelper.insertUserHistory(viewModel.username.value, viewModel.password.value)
            delay(500)
            navController.navigate("login") {
                popUpTo("signUp") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = viewModel.username.value,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))


        TextField(
            value = viewModel.password.value,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            visualTransformation = if (viewModel.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = viewModel::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (viewModel.isPasswordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(8.dp))


        TextField(
            value = viewModel.confirmPassword.value,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            visualTransformation = if (viewModel.isConfirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                    Icon(
                        imageVector = if (viewModel.isConfirmPasswordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Confirm Password Visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = viewModel::onSignUpClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        val TAG = "UsernameDebug"


        if (viewModel.showValidationMessage.value && !viewModel.passwordsMatch.value) {
            Text(
                text = "❌ Passwords do not match. Try again.",
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            if (usernames.isEmpty()) {
                usernamesMatch.value = false
            }
            else {
                val doesUserNameExist = usernames.any() { user ->
                    Log.d(TAG, "currentUsername: ${viewModel.username.value} | databaseUsername: ${user.username} | Match: ${usernamesMatch.value}")
                    user.username == viewModel.username.value
                }
                usernamesMatch.value = doesUserNameExist
            }

            if (usernamesMatch.value) {
                Text(
                    text = "❌ Username already exists. Try again.",
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }
        }


        if (viewModel.accountCreated.value && !usernamesMatch.value) {
            Text(
                text = "✅ Account Created! Redirecting...",
                color = androidx.compose.ui.graphics.Color.Green,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}