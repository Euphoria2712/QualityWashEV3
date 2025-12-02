package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualitywash.ui.Data.UserRepository


class ProfileViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Verificamos si la clase solicitada es ProfileViewModel
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // 2. Creamos la instancia inyectando el UserRepository (el Singleton object)
            return ProfileViewModel(UserRepository) as T
        }
        // 3. Si no es la clase esperada, lanzamos una excepción
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}