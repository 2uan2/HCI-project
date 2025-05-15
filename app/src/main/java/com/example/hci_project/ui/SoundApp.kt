import android.Manifest
import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hci_project.NotificationHandler
import com.example.hci_project.VideoCallingApp
import com.example.hci_project.network.AuthPreference
import com.example.hci_project.network.RetrofitInstance.authApi
import com.example.hci_project.ui.AppViewModel
import com.example.hci_project.ui.account.AccountScreen
import com.example.hci_project.ui.ai_camera.CameraAIScreen
import com.example.hci_project.ui.call.CallScreen
import com.example.hci_project.ui.home.HomeScreen
import com.example.hci_project.ui.login.AuthRepository
import com.example.hci_project.ui.login.AuthState
import com.example.hci_project.ui.login.AuthViewModel
import com.example.hci_project.ui.login.LoginScreen
import com.example.hci_project.ui.register.RegisterScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object CallScreen : Screen("Call")
    object AI : Screen("AI Camera")
    object Login : Screen("Login")
    object Register : Screen("Register")
    object Setting : Screen("Setting")

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CallApp(
    app: VideoCallingApp,
    isDarkMode: Boolean,
    onDarkThemeClicked: () -> Unit,
) {
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()
    var client by remember { mutableStateOf( app.client ) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                IMAGE_CAPTURE or IMAGE_ANALYSIS
            )
        }
    }
    val authRepository: AuthRepository = AuthRepository(authApi)
    val authPreference: AuthPreference = AuthPreference(context)
    val viewModel = AppViewModel(authPreference)
    val authViewModel = AuthViewModel(authRepository, authPreference)
    val authState by authViewModel.authState.collectAsState()
    val userId by authViewModel.userId.collectAsState()
    val streamToken by authViewModel.streamToken.collectAsState()
    val calls by authViewModel.calls.collectAsState()
    var currentCallId by remember { mutableStateOf("") }

    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsInitialized by remember { mutableStateOf(false) }
    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val notificationHandler = NotificationHandler(context)

    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }


    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            Log.d("TTS", "Initialization Status: $status")
            Log.i("TTS", "language is ${Locale.getDefault()}")
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                    // Optionally, you can prompt the user to install the necessary TTS data
                    // Intent installIntent = new Intent();
                    // installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    // startActivity(installIntent);
                } else {
                    isTtsInitialized = true
                    Log.i("TTS", "TTS engine initialized successfully")
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    fun speakText(text: String) {
        if (isTtsInitialized && tts != null) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTS", "TTS engine not initialized or text is null.")
        }
    }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Setting.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                if (authState == AuthState.Authenticated) authViewModel.getCalls()
//                Log.i("CallApp", "calls are ${calls.toString()}")
                notificationHandler.showSimpleNotification()
                HomeScreen(
                    authState = authState,
                    calls = calls,
                    onJoinCallClicked = { callId ->
                        app.initVideoClient(
                            isAuthenticated = authState == AuthState.Authenticated,
                            userToken = streamToken!!,
                            userId = userId!!,
                            username = "test"
                        )
                        currentCallId = callId
                        client = app.client
                        speakText("Calling a Volunteer")
                        navController.navigate(Screen.CallScreen.route)
                    },
                )
            }
            composable(route = Screen.CallScreen.route) {
                CallScreen(
                    client = client!!,
                    callId = currentCallId,
                    onBackPressed = {
//                        navController.navigate(Screen.Home.route)
                    },
                    onCallLeave = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }
            composable(route = Screen.AI.route) {
                CameraAIScreen(
                    controller = controller,
                    onObjectReceived = { classifications ->
//                        Log.i("SoundApp", "some objects received: ${classifications}")
                        var sentence: String
                        if (classifications.size == 1) {
                            sentence = "There is a ${classifications.get(0).name}"
                        } else if (classifications.size > 1) {
                            sentence = "There are "
                            for (classification in classifications) {
                                if (classification.name != "Unknown") {
                                    sentence += "and a ${classification.name}, "
                                }
                            }
                        } else {
                            sentence = "Could not detect anything"
                        }
                        speakText(sentence)
                    },
                    onClick = {
//                        authViewModel.textToSpeech("Photo taken", context)
                        speakText("Photo taken")
                    }
                )
            }
            composable(route = Screen.Login.route) {
                LoginScreen(
                    authUiState = authViewModel.authUiState,
                    onLoginSuccess = { streamToken, userId,  ->
                        navController.navigate(Screen.Setting.route)
                    },
                    onRegisterClicked = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginButtonClicked = { username, password ->
                        authViewModel.login(username, password)
                    }
                )
            }
            composable(route = Screen.Register.route) {
                RegisterScreen(
                    authUiState = authViewModel.authUiState,
                    onSignupSuccess = { streamToken, userId ->
                        navController.navigate(Screen.Setting.route)
                    },
                    onLoginClicked = {
                        navController.navigate(Screen.Login.route)
                    },
                    onRegisterButtonClicked = { username, email, password ->
                        authViewModel.register(username, email, password)
                    }
                )
            }
            composable(route = Screen.Setting.route) {
                AccountScreen(
                    authState = authState,
                    userId = authPreference.getUserId(),
                    isDarkMode = isDarkMode,
                    onNavigateToLoginScreen = {
                        navController.navigate(Screen.Login.route)
                    },
                    onDarkThemeClicked = {
                        onDarkThemeClicked()
                        authViewModel.textToSpeech("changing theme", context)
                    },
                    onLogoutButtonClicked = {
                        authViewModel.logout()
                        Log.i("CallApp", authState.toString())
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
        }
    }

}


val topLevelRoutes = listOf(
    TopLevelRoute(
        "Home",
        Screen.Home.route,
        Icons.Default.Home
    ),
    TopLevelRoute(
        "AICamera",
        Screen.AI.route,
        Icons.Default.Camera,
    ),
    TopLevelRoute(
        "Settings",
        Screen.Setting.route,
        Icons.Default.AccountCircle
    )
)
@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = currentDestination?.route ?: "Home",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    )
}

@SuppressLint("RestrictedApi")
@Composable
fun BottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var currentIndex by remember { mutableStateOf(0) }

    NavigationBar {
        topLevelRoutes.forEachIndexed { index, topLevelRoute ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route, null) } == true,
                onClick = {
                    currentIndex = index
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name)},
                label = { Text(topLevelRoute.name) }
            )
        }
    }
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)