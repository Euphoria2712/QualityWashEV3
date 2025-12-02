package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qualitywash.ui.Data.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// 1. Modelo de Estado para la UI (UiState)
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val showToastMessage: String? = null
)

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado expuesto a la UI (inmutable)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // --- Funciones de Manejo de Input ---

    fun updateEmail(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                passwordError = null,
                showToastMessage = null
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                emailError = null,
                showToastMessage = null
            )
        }
    }

    // Limpia el mensaje de toast después de que la UI lo ha mostrado
    fun consumedToastMessage() {
        _uiState.update { it.copy(showToastMessage = null) }
    }


    // --- Lógica de Inicio de Sesión (Login) ---

    fun login() {
        // 1. Validaciones básicas
        val emailError = when {
            _uiState.value.email.isEmpty() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches() -> "Email inválido"
            else -> null
        }

        val passwordError = when {
            _uiState.value.password.isEmpty() -> "La contraseña es requerida"
            _uiState.value.password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !_uiState.value.password.any { it.isDigit() } -> "La contraseña debe contener al menos un número"
            !_uiState.value.password.any { it.isUpperCase() } -> "La contraseña debe contener al menos una mayúscula"
            !_uiState.value.password.any { it.isLowerCase() } -> "La contraseña debe contener al menos una minúscula"
            !_uiState.value.password.any { !it.isLetterOrDigit() } -> "La contraseña debe contener al menos un carácter especial"
            else -> null
        }

        // Si hay errores de validación, actualizar el estado y detenerse
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        // 2. Ejecutar la llamada asíncrona
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailError = null, passwordError = null) }

            // Simular proceso de autenticación (delay original de 2 segundos)
            delay(2000)

            val (success, message) = userRepository.loginUser(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            if (success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        showToastMessage = "✓ $message"
                    )
                }
            } else {
                // Si el error es de credenciales, lo ponemos en el campo de contraseña y mostramos Toast.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        passwordError = message,
                        showToastMessage = "✗ $message"
                    )
                }
            }
        }
    }

    fun resetLoginState() {
        _uiState.update { it.copy(isLoginSuccessful = false, isLoading = false) }
    }

    fun resetPassword(email: String, newPassword: String, run: String) {
        val emailError = when {
            email.isEmpty() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }

        val passwordError = when {
            newPassword.isEmpty() -> "La contraseña es requerida"
            newPassword.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !newPassword.any { it.isDigit() } -> "La contraseña debe contener al menos un número"
            !newPassword.any { it.isUpperCase() } -> "La contraseña debe contener al menos una mayúscula"
            !newPassword.any { it.isLowerCase() } -> "La contraseña debe contener al menos una minúscula"
            !newPassword.contains('@') -> "La contraseña debe contener el carácter @"
            else -> null
        }

        val runError = when {
            run.isEmpty() -> "El RUN es requerido"
            !run.contains('-') -> "El RUN debe contener un guion -"
            else -> null
        }

        if (emailError != null || passwordError != null || runError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showToastMessage = null) }
            val (success, message) = userRepository.resetPassword(email, run, newPassword)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showToastMessage = if (success) "✓ $message" else "✗ $message"
                )
            }
        }
    }
}
