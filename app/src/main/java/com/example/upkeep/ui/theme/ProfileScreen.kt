package com.example.upkeep.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.upkeep.data.UserHistoryDatabase

@Composable
fun ProfileScreen(
    viewModel: LoginScreenViewModel,
    navController: NavController
) {
    val dbHelper = UserHistoryDatabase(context = LocalContext.current)
    val uiState by viewModel.uiState.collectAsState()

    val refreshTrigger = remember { mutableStateOf(0) }

    val userCredentials = remember(refreshTrigger.value) {
        dbHelper.getUserCredentials(uiState.username)
    }

    val username = userCredentials.firstOrNull()?.username
    val firstName = userCredentials.firstOrNull()?.firstName
    val lastName = userCredentials.firstOrNull()?.lastName
    val favoriteColor = userCredentials.firstOrNull()?.favoriteColor

    var isEditingFirstName by remember { mutableStateOf(false) }
    var isEditingLastName by remember { mutableStateOf(false) }
    var isEditingFavoriteColor by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (firstName != null && !isEditingFirstName) {
            Column {
                Text(
                    text = firstName,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    color = Color.Blue,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 8.dp)
                        .clickable { isEditingFirstName = true },
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
        } else {
            Column {
                Text(text = "Enter first name:")
                if (username != null) {
                    UpdateUserInformationFieldandButton(
                        initialValue = uiState.firstName,
                        dbHelper = dbHelper,
                        type = "firstName",
                        username = username,
                        onUpdated = {
                            refreshTrigger.value++
                            isEditingFirstName = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (lastName != null && !isEditingLastName) {
            Column {
                Text(text = lastName,
                    fontSize = 48.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    color = Color.Blue,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 8.dp)
                        .clickable { isEditingLastName = true },
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
        } else {
            Column {
                Text(text = "Enter last name:")
                if (username != null) {
                    UpdateUserInformationFieldandButton(
                        initialValue = uiState.lastName,
                        dbHelper = dbHelper,
                        type = "lastName",
                        username = username,
                        onUpdated = {
                            refreshTrigger.value++
                            isEditingLastName = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (favoriteColor != null && !isEditingFavoriteColor) {
            Column {
                Text(text = favoriteColor,
                    fontSize = 48.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    color = Color.Blue,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 8.dp)
                        .clickable { isEditingFavoriteColor = true },
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
        } else {
            Column {
                Text(text = "Enter favorite color name:")
                if (username != null) {
                    UpdateUserInformationFieldandButton(
                        initialValue = uiState.favoriteColor,
                        dbHelper = dbHelper,
                        type = "favoriteColor",
                        username = username,
                        onUpdated = {
                            refreshTrigger.value++
                            isEditingFavoriteColor = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Sign Out",
            color = Color.Blue,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 20.dp, start = 8.dp)
                .clickable {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }
}

@Composable
fun UpdateUserInformationFieldandButton (
    initialValue: String,
    dbHelper: UserHistoryDatabase,
    type: String,
    username: String,
    onUpdated: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("value") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done )
        )
        Button(
            onClick = {
                when (type) {
                    "firstName" -> dbHelper.updateFirstName(username, text)
                    "lastName" -> dbHelper.updateLastName(username, text)
                    else -> dbHelper.updateFavoriteColor(username, text)
                }
                onUpdated()
            }
        ) {
            Text("Update")
        }
    }
}