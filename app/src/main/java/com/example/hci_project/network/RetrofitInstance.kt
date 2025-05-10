package com.example.hci_project.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BACKEND_URL = "http://192.168.100.96:8000/"

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

}