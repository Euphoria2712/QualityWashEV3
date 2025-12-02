package com.example.qualitywash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.qualitywash.ui.Data.UserRole

// Colores personalizados - Tema Lavandería
val Primary = Color(0xFF00A896)      // Verde turquesa (agua fresca)
val PrimaryDark = Color(0xFF05668D)  // Azul profundo (agua limpia)
val Accent = Color(0xFF02C39A)       // Verde menta (frescura)
val Background = Color(0xFFEDF6F9)   // Azul muy claro (espuma)
val Surface = Color(0xFFFFFFFF)      // Blanco (limpieza)
val Error = Color(0xFFFF5A5F)        // Rojo suave

// Colores específicos para roles de usuario
val RoleClienteColor = Color(0xFF2196F3)     // Azul para Cliente
val RoleAdminColor = Color(0xFFFF5722)       // Naranja/Rojo para Administrador

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Accent,
    tertiary = PrimaryDark,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Accent,
    tertiary = PrimaryDark,
    background = Background,
    surface = Surface,
    error = Error
)

@Composable
fun QualityWashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



fun getRoleColor(role: UserRole): Color {
    return when (role) {
        UserRole.Cliente -> RoleClienteColor     // Azul
        UserRole.ADMIN -> RoleAdminColor    // Naranja/Rojo

    }
}

//muestra el rol del usuario
fun getRoleText(role: UserRole): String {
    return when (role) {
        UserRole.Cliente -> "👤 Usuario"
        UserRole.ADMIN -> "👑 Administrador"

    }
}

// obtiene la descricion
fun getRoleDescription(role: UserRole): String {
    return when (role) {
        UserRole.Cliente -> "Acceso a servicios básicos"
        UserRole.ADMIN -> "Control total del sistema"

    }
}