package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TiendaViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TiendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TiendaViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
