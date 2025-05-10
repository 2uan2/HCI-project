package com.example.hci_project.network

import android.content.Context
import androidx.core.content.edit
import android.content.SharedPreferences

class AuthPreference(context: Context) {
    private val pref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun putToken(userId: String, authToken: String, streamToken: String) {
        pref.edit() {
            putString("userId", userId)
            putString("authToken", authToken)
            putString("streamToken", streamToken)
        }
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun getUserId(): String? {
        return pref.getString("userId", null)
    }

    fun getAuthToken(): String? {
        return pref.getString("authToken", null)
    }

    fun getStreamToken(): String? {
        return pref.getString("streamToken", null)
    }

    fun clear() {
        pref.edit() {
            remove("userId")
            remove("authToken")
            remove("streamToken")
        }
    }
}
