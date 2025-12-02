// Archivo: RegisterViewModelFactory.kt
package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.viewModel.LoginViewModel


class RegisterViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que la clase solicitada sea RegisterViewModel
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Retorna la instancia de RegisterViewModel con el UserRepository (Singleton) inyectado
            return RegisterViewModel(UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}