package com.example.qualitywash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Navigation.AppNavigation
import com.example.qualitywash.ui.Navigation.Routes
import com.example.qualitywash.ui.Screen.AppSplash
import com.example.qualitywash.ui.theme.QualityWashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QualityWashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }
                    LaunchedEffect(Unit) {
                        delay(1200)
                        showSplash = false
                    }

                    if (showSplash) {
                        AppSplash()
                    } else {
                        val startDestination = if (UserRepository.isUserLoggedIn()) {
                            Routes.HOME
                        } else {
                            Routes.LOGIN
                        }
                        AppNavigation(startDestination = startDestination)
                    }
                }
            }
        }
    }
}
