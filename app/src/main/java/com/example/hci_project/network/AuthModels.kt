package com.example.hci_project.network

import com.google.gson.annotations.SerializedName

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(
    val username: String,
    @SerializedName("stream_token") val streamToken: String,
    @SerializedName("auth_token") val authToken: String
)

data class GuestAuthResponse(
    @SerializedName("guest_id") val userId: String,
    @SerializedName("stream_token") val streamToken: String
)

data class CallListResponse(
    val calls: List<String>
)