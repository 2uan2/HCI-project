package com.example.hci_project.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.hci_project.network.AuthPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val authPreference: AuthPreference,
) : ViewModel() {
    private val _isLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        _isLoggedIn.value = authPreference.isLoggedIn()
    }

    fun logout() {
        _isLoggedIn.value = false
         authPreference.clear()
    }

}