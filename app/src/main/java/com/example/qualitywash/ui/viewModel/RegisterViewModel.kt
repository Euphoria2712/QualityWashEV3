package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qualitywash.ui.Data.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Modelo de Estado para la UI (UiState)
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val run: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val fechaNacimiento: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val runError: String? = null,
    val telefonoError: String? = null,
    val fechaNacimientoError: String? = null,
    val isLoading: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val showToastMessage: String? = null
)

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado interno mutable
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // --- Funciones de Manejo de Input y Validación ---

    fun updateName(newName: String) {
        _uiState.update {
            it.copy(
                name = newName,
                nameError = validateName(newName), // Validación en tiempo real
                showToastMessage = null
            )
        }
    }

    fun updateEmail(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = validateEmail(newEmail),
                showToastMessage = null
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = validatePassword(newPassword),
                // Re-valida la confirmación si ya tiene contenido
                confirmPasswordError = if (it.confirmPassword.isNotEmpty()) {
                    validateConfirmPassword(newPassword, it.confirmPassword)
                } else null,
                showToastMessage = null
            )
        }
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = validateConfirmPassword(it.password, newConfirmPassword),
                showToastMessage = null
            )
        }
    }

    fun updateRun(v: String) { _uiState.update { it.copy(run = v, runError = validateRun(v), showToastMessage = null) } }
    fun updateApellido(v: String) { _uiState.update { it.copy(apellido = v, showToastMessage = null) } }
    fun updateTelefono(v: String) { _uiState.update { it.copy(telefono = v, telefonoError = validateTelefono(v), showToastMessage = null) } }
    fun updateDireccion(v: String) { _uiState.update { it.copy(direccion = v, showToastMessage = null) } }
    fun updateFechaNacimiento(v: String) { _uiState.update { it.copy(fechaNacimiento = v, fechaNacimientoError = validateFecha(v), showToastMessage = null) } }

    fun consumedToastMessage() { // 👈 Función para corregir error en RegisterScreen
        _uiState.update { it.copy(showToastMessage = null) }
    }


    // --- Lógica de Registro (Register) ---

    fun register() {
        // 1. Validaciones finales
        val finalNameError = validateName(_uiState.value.name)
        val finalEmailError = validateEmail(_uiState.value.email)
        val finalPasswordError = validatePassword(_uiState.value.password)
        val finalConfirmPasswordError = validateConfirmPassword(_uiState.value.password, _uiState.value.confirmPassword)
        val finalRunError = validateRun(_uiState.value.run)
        val finalTelefonoError = validateTelefono(_uiState.value.telefono)
        val finalFechaError = validateFecha(_uiState.value.fechaNacimiento)

        // 2. Actualizar el estado con los errores finales (para que la UI los muestre)
        _uiState.update {
            it.copy(
                nameError = finalNameError,
                emailError = finalEmailError,
                passwordError = finalPasswordError,
                confirmPasswordError = finalConfirmPasswordError,
                runError = finalRunError,
                telefonoError = finalTelefonoError,
                fechaNacimientoError = finalFechaError
            )
        }

        val s = _uiState.value
        val requiredMissing = s.run.isBlank() || s.apellido.isBlank() || s.telefono.isBlank() || s.direccion.isBlank() || s.fechaNacimiento.isBlank()
        if (finalNameError != null || finalEmailError != null || finalPasswordError != null || finalConfirmPasswordError != null || finalRunError != null || finalTelefonoError != null || finalFechaError != null || requiredMissing) {
            _uiState.update { it.copy(showToastMessage = "✗ Por favor, corrige los errores del formulario.") }
            return
        }

        // 3. Ejecutar la llamada asíncrona
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showToastMessage = null) }

            delay(2000)

            // 👈 CORRECCIÓN AQUÍ: Pasando el nombre, email y password
            val s2 = _uiState.value
            val (success, message) = userRepository.registerUser(
                name = s2.name,
                email = s2.email,
                password = s2.password,
                run = s2.run,
                apellido = s2.apellido,
                telefono = s2.telefono,
                direccion = s2.direccion,
                fechaNacimiento = s2.fechaNacimiento
            )

            if (success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true,
                        showToastMessage = "✓ $message"
                    )
                }
            } else {
                // Si el error es, por ejemplo, "email ya registrado"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        emailError = message,
                        showToastMessage = "✗ $message"
                    )
                }
            }
        }
    }

    // 👈 Función para corregir error en RegisterScreen
    fun resetRegistrationState() {
        _uiState.update { RegisterUiState() }
    }

    // --- Funciones de Validación (Portadas del Screen Original) ---

    private fun validateName(name: String): String? {
        return when {
            name.isEmpty() -> "El nombre es requerido"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) -> "Solo se permiten letras"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "La contraseña es requerida"
            password.length < 8 -> "Mínimo 8 caracteres"
            !password.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
            !password.any { it.isDigit() } -> "Debe contener al menos un número"
            !password.any { it.isLowerCase() } -> "Debe contener al menos una minúscula"
            !password.contains('@') -> "Debe incluir el carácter @"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Confirma tu contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    private fun validateRun(run: String): String? {
        return when {
            run.isEmpty() -> "RUN es requerido"
            !run.matches(Regex("^\\d+-[\\dkK]$")) -> "Formato RUN inválido (ej: 12345678-9)"
            else -> null
        }
    }

    private fun validateTelefono(telefono: String): String? {
        return when {
            telefono.isEmpty() -> "Teléfono es requerido"
            !telefono.matches(Regex("^\\+\\d{7,15}$")) -> "Incluye + y sólo dígitos (ej: +56912345678)"
            else -> null
        }
    }

    private fun validateFecha(fecha: String): String? {
        return when {
            fecha.isEmpty() -> "Fecha es requerida"
            !fecha.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> "Usa formato YYYY-MM-DD"
            else -> null
        }
    }
}
