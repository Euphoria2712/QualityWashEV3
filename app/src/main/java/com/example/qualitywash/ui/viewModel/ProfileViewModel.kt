package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualitywash.ui.Data.User
import com.example.qualitywash.ui.Data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para gestionar el estado y las operaciones del perfil del usuario.
// Utiliza UserRepository para la lógica de datos.
class ProfileViewModel(
    private val userRepository: UserRepository // Inyección de dependencia del repositorio.
) : ViewModel() {

    // Estado interno mutable que contiene la información del usuario actual o un objeto 'Invitado'.
    private val _userState = MutableStateFlow(
        userRepository.getCurrentUser() ?: User(
            id = "0",
            name = "Invitado",
            email = "No logueado",
            password = ""
        )
    )

    // Estado inmutable expuesto a la UI para su observación.
    val userState: StateFlow<User> = _userState
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        // Carga el usuario al inicializar el ViewModel.
        loadCurrentUser()
    }

    //Carga el usuario actual desde el repositorio y actualiza el StateFlow.

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            if (user != null) {
                _userState.value = user
            }
        }
    }

    //Cierra la sesión del usuario y restablece el estado a 'Invitado'.

    fun logout() {
        userRepository.logoutUser()
        _userState.value = User(
            id = "0",
            name = "Invitado",
            email = "No logueado",
            password = ""
        )
    }

    //Actualiza la URI de la foto de perfil en el repositorio y refresca el estado.

    fun updateProfilePhoto(photoUri: String) {
        viewModelScope.launch {
            if (userRepository.updateProfilePhoto(photoUri)) {
                loadCurrentUser()
            }
        }
    }


    //Actualiza el nombre, número de teléfono y dirección del perfil en el repositorio y refresca el estado.

    fun updateProfile(name: String, email: String, password: String?, address: String?, phoneNumber: String?) {
        viewModelScope.launch {
            val (success, _) = userRepository.updateCurrentUserRemote(name, email, password, address, phoneNumber)
            if (success) {
                loadCurrentUser()
                _message.value = "Datos actualizados exitosamente"
            }
        }
    }
    fun clearMessage() { _message.value = null }
    //Fuerza una recarga de los datos del usuario actual (alias de loadCurrentUser).
    fun refreshUser() {
        loadCurrentUser()
    }
}
