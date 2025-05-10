package com.example.hci_project.ui.call

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.actions.DefaultOnCallActionHandler
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.call.state.LeaveCall
import java.util.UUID

@Composable
fun CallScreen(
    client: StreamVideo,
    callId: String,
    onCallLeave: () -> Unit,
    onBackPressed: () -> Unit,
) {

    val context = LocalContext.current
    // Request permissions and join a call, which type is `default` and id is `123`.
    val call = client.call(type = "default", id = callId)//"64b4767c-d794-4f23-8359-68a998160ad9")
    LaunchCallPermissions(
        call = call,
        onAllPermissionsGranted = {
            // All permissions are granted so that we can join the call.
            val result = call.join(create = true)
            result.onError {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        }
    )

    // Apply VideoTheme
    VideoTheme {

        CallContent(
            modifier = Modifier.fillMaxSize(),
            call = call,
            onBackPressed = {
//                call.leave()
                onBackPressed()
            },
            onCallAction = { callAction ->
//                 LeaveCall
                when (callAction) {
                    is LeaveCall -> {
                        onCallLeave()
                        call.leave()
                    }
                    else -> DefaultOnCallActionHandler.onCallAction(call, callAction)
                }
            }
        )

    }

}