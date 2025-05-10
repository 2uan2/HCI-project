package com.example.hci_project.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hci_project.ui.login.AuthState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import java.util.UUID

@Composable
fun HomeScreen(
    authState: AuthState,
    calls: List<String>,
    modifier: Modifier = Modifier,
    onJoinCallClicked: (String) -> Unit = {},
) {
    when (authState) {
        AuthState.Authenticated -> {
            if (calls.count() == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = "No calls",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,

                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(calls) { call ->
                    Button(
                        onClick = { onJoinCallClicked(call) },
                        shape = RoundedCornerShape(10),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(128.dp)
                    ) {
                        Text(
                            text = "Join call ${call}",
                            style = TextStyle(
                                fontSize = 24.sp,
                                lineHeight = 24.sp,
                            )
                        )
                    }
                }
            }
        }
        AuthState.Unauthenticated -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val randomCallId = UUID.randomUUID().toString()
                Button(
                    onClick = { onJoinCallClicked(randomCallId) },
                    shape = RoundedCornerShape(10),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(512.dp)
                ) {
                    Text(
                        text = "Call a volunteer",
                        fontSize = 60.sp,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        authState = AuthState.Authenticated,
        calls = listOf()//listOf("1234032809480123090875435414324", "5012983090954832iiii543543095809", "390248324")
    )
}