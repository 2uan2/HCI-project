package com.example.hci_project.ui.login

import android.util.Log
import com.example.hci_project.network.AuthApiService
import com.example.hci_project.network.AuthRequest
import com.example.hci_project.network.AuthResponse
import com.example.hci_project.network.CallListResponse
import com.example.hci_project.network.GuestAuthResponse
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val api: AuthApiService) {

    suspend fun getCalls(authToken: String): Result<CallListResponse> {
        return try {
            val response = api.getCalls("Token ${authToken}")
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("AuthRepository", "calls from repo are: ${it.calls}")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Guest login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP ${e.code()}: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.localizedMessage}"))
        }
    }

    suspend fun getGuestToken(): Result<GuestAuthResponse> {
        return try {
            val response = api.getGuestToken()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Guest login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP ${e.code()}: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.localizedMessage}"))
        }
    }

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(AuthRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP ${e.code()}: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.localizedMessage}"))
        }
    }

    suspend fun register(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(AuthRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Register failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.localizedMessage}"))
        }
    }
}