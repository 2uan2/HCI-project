package com.example.hci_project.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hci_project.ui.login.AuthState

@Composable
fun AccountScreen(
    authState: AuthState,
    userId: String?,
    isDarkMode: Boolean,
    onDarkThemeClicked: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onLogoutButtonClicked: () -> Unit,
) {
    var receivedCallChecked by remember { mutableStateOf(false) }
    when (authState) {
        AuthState.Unauthenticated -> {
            onNavigateToLoginScreen()
        }
        AuthState.Authenticated -> {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hello, ${userId ?: "anonymous user"}",
                        modifier = Modifier
                            .padding(bottom = 64.dp),
//                            .fillMaxWidth(),
                        fontSize = 48.sp,
                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { checked ->
                            onDarkThemeClicked()
                        }
                    )
//                    IconButton(
//                        onClick = onDarkThemeClicked,
//                        modifier = Modifier
//                            .background(color = Color.Red)
//                    ) {
//                        Icon(
//                            if (isDarkMode) Icons.Default.LightMode
//                            else Icons.Default.DarkMode,
//                            contentDescription = "set dark/light mode"
//                        )
//                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 8.dp),
                ) {
                    Text(
                        text = "Primary Language:",
                        fontSize = 24.sp,
                    )
                }
                DropDownDemo()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24))
                        .padding(top = 4.dp, bottom = 2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(start = 24.dp)
                        .clickable(
                            onClick = {
                                receivedCallChecked = !receivedCallChecked
                            }
                        ),
                ) {
                    Text(
                        text = "Receive calls",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Checkbox(
                        checked = receivedCallChecked,
                        onCheckedChange = { receivedCallChecked = !receivedCallChecked },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = MaterialTheme.colorScheme.onPrimary,
                            checkedColor = MaterialTheme.colorScheme.onPrimary,
                            checkmarkColor = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(24),
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Support",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Privacy and Terms",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = onLogoutButtonClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(24),
                    modifier = Modifier
                        .fillMaxWidth()
                    ) {
                    Text(
                        text = "Log out",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }

        }
    }
}
@Composable
fun DropDownDemo() {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableStateOf(0)
    }

    val usernames = listOf("English", "Vietnamese")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.primary)
//            .clip(RoundedCornerShape(12))
    ) {

        Box {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        isDropDownExpanded.value = true
                    }
            ) {
                Text(
                    text = usernames[itemPosition.value],
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .padding(start = 16.dp),
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(48.dp)
//                        .background(MaterialTheme.colorScheme.onPrimary),
                )
            }
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .size(512.dp),
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                usernames.forEachIndexed { index, username ->
                    DropdownMenuItem(
//                        modifier = Modifier.fillMaxWidth(),
                        text = {
                        Text(
                            text = username
                        )
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.value = index
                        })
                }
            }
        }
    }
}