package com.example.hci_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("login/")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register/")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @GET("guest-token/")
    suspend fun getGuestToken(): Response<GuestAuthResponse>

    @GET("calls/")
    suspend fun getCalls(
        @Header("Authorization") authToken: String,
    ): Response<CallListResponse>
}