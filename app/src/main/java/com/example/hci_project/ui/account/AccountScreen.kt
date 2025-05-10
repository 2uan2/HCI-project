package com.example.hci_project.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hci_project.network.AuthPreference
import com.example.hci_project.ui.login.AuthState
import com.example.hci_project.ui.login.LoginScreen

@Composable
fun AccountScreen(
    authState: AuthState,
    userId: String?,
    onNavigateToLoginScreen: () -> Unit,
    onLogoutButtonClicked: () -> Unit,
) {
    when (authState) {
        AuthState.Unauthenticated -> {
            onNavigateToLoginScreen()
        }
        AuthState.Authenticated -> {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Hello ${userId ?: "anonymous user"}",
                    modifier = Modifier,
                    fontSize = 24.sp,
                )
                Button(
                    onClick = onLogoutButtonClicked
                ) {
                    Text(
                        text = "Log out",
                    )
                }
            }

        }
    }
}