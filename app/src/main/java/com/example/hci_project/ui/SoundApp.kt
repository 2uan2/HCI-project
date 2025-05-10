import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CallScreen : Screen("call")
    object AI : Screen("ai")
    object Login : Screen("login")
    object Register : Screen("register")
    object Account : Screen("account")

}

@Composable
fun CallApp(
    app: VideoCallingApp
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

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                if (authState == AuthState.Authenticated) authViewModel.getCalls()
//                Log.i("CallApp", "calls are ${calls.toString()}")
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
                        navController.navigate(Screen.CallScreen.route)
                    }
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
                )
            }
            composable(route = Screen.Login.route) {
                LoginScreen(
                    authUiState = authViewModel.authUiState,
                    onLoginSuccess = { streamToken, userId,  ->
                        navController.navigate(Screen.Account.route)
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
                        navController.navigate(Screen.Account.route)
                    },
                    onLoginClicked = {
                        navController.navigate(Screen.Login.route)
                    },
                    onRegisterButtonClicked = { username, email, password ->
                        authViewModel.register(username, email, password)
                    }
                )
            }
            composable(route = Screen.Account.route) {
                AccountScreen(
                    authState = authState,
                    userId = authPreference.getUserId(),
                    onNavigateToLoginScreen = {
                        navController.navigate(Screen.Login.route)
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

@SuppressLint("RestrictedApi")
@Composable
fun BottomBar(
    navController: NavHostController
) {
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
            "Account",
            Screen.Account.route,
            Icons.Default.AccountCircle
        )
    )

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