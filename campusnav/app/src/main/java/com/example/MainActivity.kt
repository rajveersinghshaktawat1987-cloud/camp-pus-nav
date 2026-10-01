package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.NavDestination
import com.example.ui.screens.components.CampusBottomBar
import com.example.ui.screens.components.CampusTopBar
import com.example.ui.screens.map.CampusMapScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.routing.ActiveNavigationScreen
import com.example.ui.screens.saved.SavedLocationsScreen
import com.example.ui.screens.search.CampusSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.CampusViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CampusNavApp()
            }
        }
    }
}

@Composable
fun CampusNavApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    val viewModel: CampusViewModel = viewModel(
        factory = CampusViewModelFactory(context.applicationContext)
    )

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()
    val activeRoute by viewModel.activeRoute.collectAsStateWithLifecycle()
    val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Map.route

    val snackbarHostState = remember { SnackbarHostState() }

    // Display Snackbars reactively
    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackBar()
        }
    }

    // Permission request launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.showMessage("GPS Location enabled")
        }
    }

    // Request permissions on first launch
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val isOnboarding = currentRoute == NavDestination.Onboarding.route
    val isNavigatingActive = activeRoute != null

    val startDestination = if (settings.hasCompletedOnboarding) {
        NavDestination.Map.route
    } else {
        NavDestination.Onboarding.route
    }

    Scaffold(
        topBar = {
            if (!isOnboarding && currentRoute != NavDestination.NavigationActive.route) {
                CampusTopBar(
                    currentRoute = currentRoute,
                    profile = settings.profile,
                    isNavigatingActive = isNavigatingActive,
                    onNavigateToSearch = {
                        if (currentRoute != NavDestination.Search.route) {
                            navController.navigate(NavDestination.Search.route)
                        }
                    },
                    onNavigateToSettings = {
                        if (currentRoute != NavDestination.Settings.route) {
                            navController.navigate(NavDestination.Settings.route)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!isOnboarding && currentRoute != NavDestination.NavigationActive.route) {
                CampusBottomBar(
                    currentRoute = currentRoute,
                    savedLocationsCount = savedLocations.count { it.isFavorite },
                    isNavigatingActive = isNavigatingActive,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(NavDestination.Map.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (currentRoute == NavDestination.NavigationActive.route || isOnboarding) 0.dp else innerPadding.calculateTopPadding(),
                    bottom = if (currentRoute == NavDestination.NavigationActive.route || isOnboarding) 0.dp else innerPadding.calculateBottomPadding()
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(NavDestination.Onboarding.route) {
                    OnboardingScreen(
                        viewModel = viewModel,
                        onFinishOnboarding = {
                            navController.navigate(NavDestination.Map.route) {
                                popUpTo(NavDestination.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavDestination.Map.route) {
                    CampusMapScreen(
                        viewModel = viewModel,
                        onNavigateToRoute = {
                            navController.navigate(NavDestination.NavigationActive.route)
                        }
                    )
                }

                composable(NavDestination.Saved.route) {
                    SavedLocationsScreen(
                        viewModel = viewModel,
                        onNavigateToRoute = {
                            navController.navigate(NavDestination.NavigationActive.route)
                        }
                    )
                }

                composable(NavDestination.Search.route) {
                    CampusSearchScreen(
                        viewModel = viewModel,
                        onNavigateToMap = {
                            navController.navigate(NavDestination.Map.route) {
                                popUpTo(NavDestination.Map.route) { inclusive = false }
                            }
                        },
                        onNavigateToRoute = {
                            navController.navigate(NavDestination.NavigationActive.route)
                        }
                    )
                }

                composable(NavDestination.Settings.route) {
                    SettingsScreen(viewModel = viewModel)
                }

                composable(NavDestination.NavigationActive.route) {
                    ActiveNavigationScreen(
                        viewModel = viewModel,
                        onBackToMap = {
                            navController.navigate(NavDestination.Map.route) {
                                popUpTo(NavDestination.Map.route) { inclusive = false }
                            }
                        }
                    )
                }
            }
        }
    }
}
