package com.example.hci_project.ui.register

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hci_project.ui.login.AuthUiState
import com.example.hci_project.ui.login.AuthViewModel

@Composable
fun RegisterScreen(
    authUiState: AuthUiState,
    onRegisterButtonClicked: (String, String, String) -> Unit,
    onSignupSuccess: (String, String) -> Unit,
    onLoginClicked: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
//    val authUiState = authViewModel.authUiState

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
//            .background(color = Color("#ffffff".toColorInt())),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        Image(
//            painterResource(id = R.drawable.wave),
//            contentDescription = null,
//            contentScale = ContentScale.FillBounds
//        )
//
//        Image(
//            painterResource(id = R.drawable.logo),
//            contentDescription = null,
//            modifier = Modifier.height(150.dp)
//        )

        Text(
            "Create an Account",
            fontSize = 40.sp,
//            fontStyle = FontStyle.Italic,
//            fontWeight = FontWeight.Bold,
//            color = Color("#7d32a8".toColorInt())
        )

        // Username
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 64.dp, vertical = 8.dp),
//                .border(1.dp, Color("#7d32a8".toColorInt()), shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
//                color = Color("#7d32a8".toColorInt()),
                fontSize = 14.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
//                focusedTextColor = Color("#7d32a8".toColorInt()),
//                unfocusedTextColor = Color("#7d32a8".toColorInt()),
//                cursorColor = Color("#7d32a8".toColorInt())
            )
        )

        // Email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 64.dp, vertical = 8.dp),
//                .border(1.dp, Color("#7d32a8".toColorInt()), shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
//                color = Color("#7d32a8".toColorInt()),
                fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
//                focusedTextColor = Color("#7d32a8".toColorInt()),
//                unfocusedTextColor = Color("#7d32a8".toColorInt()),
//                cursorColor = Color("#7d32a8".toColorInt())
            )
        )

        // Password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 64.dp, vertical = 8.dp),
//                .border(1.dp, Color("#7d32a8".toColorInt()), shape = RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
//                color = Color("#7d32a8".toColorInt()),
                fontSize = 14.sp
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
//                focusedTextColor = Color("#7d32a8".toColorInt()),
//                unfocusedTextColor = Color("#7d32a8".toColorInt()),
//                cursorColor = Color("#7d32a8".toColorInt())
            )
        )

        // Signup button
        Button(
            onClick = {
                onRegisterButtonClicked(username, email, password)
//                authViewModel.register(username, email, password)
            },
            Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 64.dp, vertical = 8.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color("#7d32a8".toColorInt())),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Sign Up",
//                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when (authUiState) {
            is AuthUiState.Loading -> Text("Registering...", color = Color.Gray)
            is AuthUiState.Error -> Text("Error: ${authUiState.message}", color = Color.Red)
            is AuthUiState.Success -> {
                Text("Register Success!", color = Color.Green)
                LaunchedEffect(Unit) {
                    onSignupSuccess(authUiState.token, authUiState.userId)
                }
            }

            else -> {}
        }

        Text(
            text = "Already have an account? Log in here",
            Modifier.padding(top = 16.dp)
                .clickable(
                    onClick = onLoginClicked
                ),
            fontSize = 14.sp,
//            color = Color("#7d32a8".toColorInt())
        )
    }
}