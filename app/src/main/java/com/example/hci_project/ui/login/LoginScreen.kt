package com.example.hci_project.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

@Composable
fun LoginScreen(
    authUiState: AuthUiState,
    onLoginButtonClicked: (String, String) -> Unit,
    onLoginSuccess: (String, String) -> Unit,
    onRegisterClicked: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            text = "Login",
            fontSize = 40.sp,
            modifier = Modifier
        )
        TextField(
            value = username, { text -> username = text },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(start = 64.dp, end = 64.dp, top = 8.dp, bottom = 8.dp)
                .border(1.dp, Color("#7d32a8".toColorInt()), shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = Color("#7d32a8".toColorInt()),
                fontSize = 14.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color("#7d32a8".toColorInt()),
                unfocusedTextColor = Color("#7d32a8".toColorInt()),
                cursorColor = Color("#7d32a8".toColorInt())
            )
        )
        TextField(
            value = pass, { text -> pass = text },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(start = 64.dp, end = 64.dp, top = 8.dp, bottom = 8.dp)
                .border(1.dp, Color("#7d32a8".toColorInt()), shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = Color("#7d32a8".toColorInt()),
                fontSize = 14.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color("#7d32a8".toColorInt()),
                unfocusedTextColor = Color("#7d32a8".toColorInt()),
                cursorColor = Color("#7d32a8".toColorInt())
            ),
        )
        Text(
            text = "Register",
            color = Color.Blue,
            modifier = Modifier
                .clickable(
                    onClick = onRegisterClicked
                ),
        )

        when (authUiState) {
            is AuthUiState.Loading -> Text("Logging in...", color = Color.Gray)
            is AuthUiState.Error -> Text("Error: ${authUiState.message}", color = Color.Red)
            is AuthUiState.Success -> {
                Text("Log in Success!", color = Color.Green)
                LaunchedEffect(Unit) {
                    onLoginSuccess(authUiState.token, authUiState.userId)
                }
            }

            else -> {}
        }

        Button(
            onClick = {
                onLoginButtonClicked(username, pass)
//                authViewModel.login(username, pass)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(start = 64.dp, end = 64.dp, top = 8.dp, bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color("#7d32a8".toColorInt())),
            shape = RoundedCornerShape(50)
        ) {
            Text("Login", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}