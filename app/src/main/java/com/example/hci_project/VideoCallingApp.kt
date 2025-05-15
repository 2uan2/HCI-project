package com.example.hci_project

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.hci_project.network.AuthPreference
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class VideoCallingApp : Application() {

    lateinit var authPreference: AuthPreference
    var client: StreamVideo? = null

    val apiKey = "ekfxty25g8es"
//    val userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NDY4Njk2MDEsInVzZXJfaWQiOiJ0ZXN0MSJ9.oBY91VvzSUatztc8T7UiSUZI9g_6ZKYT1TBS8f_GQsE"
//    val userId = "test1"
//    val callId = "tp7j7wLhBfAj"

    // Create a user
//    val user = User(
//        id = userId, // any string
//        name = "Tutorial", // name and image are used in the UI
//        image = "https://bit.ly/2TIt8NR",
//    )

    override fun onCreate() {
        authPreference = AuthPreference(applicationContext)
        super.onCreate()

        val notificationChannel = NotificationChannel(
            "notification_channel_id",
            "Notification name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Setting up the channel
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun initVideoClient(userToken: String, userId: String, isAuthenticated: Boolean, username: String) {
        if (client == null) {
            if (isAuthenticated) {
                client = StreamVideoBuilder(
                    context = applicationContext,
                    apiKey = apiKey,
                    geo = GEO.GlobalEdgeNetwork,
                    user = User(
                        id = userId,
                        name = username,
                        image = "https://bit.ly/2TIt8NR",
                    ),
                    token = userToken
                ).build()

            } else {
                client = StreamVideoBuilder(
                    context = applicationContext,
                    apiKey = apiKey,
                    geo = GEO.GlobalEdgeNetwork,
                    user = User(
                        id = userId,
//                        role = "guest",
                        name = username,
                        image = "https://bit.ly/2TIt8NR",
//                        type = UserType.Guest,
                    ),
                    token = userToken
                ).build()
            }
        }
    }
}