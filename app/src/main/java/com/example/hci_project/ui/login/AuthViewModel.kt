package com.example.hci_project.ui.login

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hci_project.network.AuthPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AuthViewModel(
    private val repository: AuthRepository,
    private val authPreference: AuthPreference,
) : ViewModel() {
    var authUiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    // application state (logged in or not)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _streamToken = MutableStateFlow<String?>(null)
    val streamToken: StateFlow<String?> = _streamToken

    private val _calls = MutableStateFlow(listOf<String>())
    val calls: StateFlow<List<String>> = _calls

    init {
        Log.i("AuthViewModel", _authState.value.toString())
        getCalls()
        _authState.value =
            if (authPreference.isLoggedIn()) AuthState.Authenticated
            else AuthState.Unauthenticated
        _userId.value = authPreference.getUserId()
        _streamToken.value = authPreference.getStreamToken()
        if (authState.value == AuthState.Unauthenticated) {
            getGuestToken()
        }
    }

    fun getCalls() {
        Log.i("AuthViewModel", "getting calls...")
        viewModelScope.launch {
            Log.i("AuthViewModel", "authToken is ${authPreference.getAuthToken()}")
            if (authPreference.getAuthToken() == null) {
                return@launch
            }
            val result = repository.getCalls(authPreference.getAuthToken()!!)
            result.fold(
                onSuccess = {
                    Log.i("AuthViewModel", "calls from viewmodel are: ${it.calls}")
                    _calls.value = it.calls
                },
                onFailure = {
                    Log.i("AuthViewModel", "error: ${it.message}")
                    AuthUiState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }

    fun getGuestToken() {
        viewModelScope.launch {
            Log.i("AuthViewModel", "getting guest token...")
            val result = repository.getGuestToken()
            result.fold(
                onSuccess = {
                    _userId.value = it.userId
                    _streamToken.value = it.streamToken
                    Log.i("AuthViewModel", "userId is ${ userId.value }")
                    Log.i("AuthViewModel", "streamToken is ${ streamToken.value }")
                },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authUiState = AuthUiState.Loading
            val result = repository.login(username, password)
            authUiState = result.fold(
                onSuccess = {
                    _authState.value = AuthState.Authenticated
                    authPreference.putToken(it.username, it.authToken, it.streamToken)
                    _userId.value = it.username
                    _streamToken.value = it.streamToken
                    AuthUiState.Success(it.authToken, it.username)
                },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            authUiState = AuthUiState.Loading
            val result = repository.register(username, password)
            authUiState = result.fold(
                onSuccess = {
                    _authState.value = AuthState.Authenticated
                    authPreference.putToken(it.username, it.authToken, it.streamToken)
                    _userId.value = it.username
                    _streamToken.value = it.streamToken
                    AuthUiState.Success(it.authToken, it.username)
                },
                onFailure = { AuthUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
        _streamToken.value = null
        _userId.value = null
        authPreference.clear()
        reset()
    }

    private  var  textToSpeech:TextToSpeech? = null

    fun textToSpeech(text: String, context: Context) {
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(1.0f)
                    txtToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }
        }
    }

    fun reset() {
        authUiState = AuthUiState.Idle
    }
}

sealed interface AuthState {
    object Authenticated : AuthState
    object Unauthenticated : AuthState
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String, val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
