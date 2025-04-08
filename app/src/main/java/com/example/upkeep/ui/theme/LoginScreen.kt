package com.example.upkeep.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.upkeep.R
import com.example.upkeep.data.UserHistoryDatabase
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel,
    onLoginSuccess: () -> Unit,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.LoginUiState()
    }

    val dbHelper = UserHistoryDatabase(context = LocalContext.current)
    val uiState by viewModel.uiState.collectAsState()
    val buttonClicked = remember { mutableStateOf(false) }


    LaunchedEffect(uiState.correctCredentials) {
        if (uiState.correctCredentials) {
            delay(500)
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CredentialFields(
            username = uiState.username,
            onUsernameChange = viewModel::updateUsername,
            password = uiState.password,
            onPasswordChange = viewModel::updatePassword,
            passwordVisible = uiState.passwordVisible,
            onPasswordVisibilityToggle = viewModel::togglePasswordVisibility
        )
        val TAG = "PasswordDebug"
        CredentialSubmitButton(
            onClick = {
                buttonClicked.value = true
                val userCredentials = dbHelper.getUserCredentials(uiState.username)
                val password = userCredentials.firstOrNull()?.password
                Log.d(TAG, "password: ${password} | databaseUsername: ${uiState.username}")
                if (password != null) {
                    viewModel.checkCredentials(uiState.username, password)
                }
            }
        )
        if (uiState.correctCredentials) {
            Text(text = "Login Successful!", color = MaterialTheme.colorScheme.primary)
        } else if (buttonClicked.value) {
            Text(text = "Username or Password incorrect.", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.no_account)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ClickableText(
                text = AnnotatedString(stringResource(R.string.sign_up)),
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ),
                onClick = {

                    navController.navigate("signUp")
                },
            )
        }
    }
}

@Composable
fun CredentialFields(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )
    }
}

@Composable
fun CredentialSubmitButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(16.dp)
    ) {
        Text(stringResource(R.string.submit))
    }
}