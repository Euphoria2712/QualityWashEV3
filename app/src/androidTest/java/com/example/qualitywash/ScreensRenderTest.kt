package com.example.qualitywash

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.qualitywash.ui.Screen.LoginScreen
import com.example.qualitywash.ui.Screen.PerfilScreen
import com.example.qualitywash.ui.Screen.TiendaScreen
import org.junit.Rule
import org.junit.Test

class ScreensRenderTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loginScreen_renders_fields_and_actions() {
        composeRule.setContent {
            LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeRule.onNodeWithText("¿Olvidó su contraseña?").assertIsDisplayed()
        composeRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
    }

    @Test
    fun tiendaScreen_renders_title() {
        composeRule.setContent { TiendaScreen(onNavigateBack = {}) }
        composeRule.onNodeWithText("Tienda").assertIsDisplayed()
    }

    @Test
    fun perfilScreen_renders_and_shows_edit_button() {
        composeRule.setContent { PerfilScreen(onNavigateBack = {}, onLogoutComplete = {}, onNavigateToHistory = {}) }
        composeRule.onNodeWithText("Mi Perfil").assertIsDisplayed()
        composeRule.onNodeWithText("Editar Perfil").assertIsDisplayed()
    }
}
