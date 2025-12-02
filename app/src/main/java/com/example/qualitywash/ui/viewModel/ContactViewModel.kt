package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val message: String = "",
    val isSending: Boolean = false,
    val sentSuccessfully: Boolean = false,
    val errorMessage: String? = null
)

class ContactViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState

    fun updateName(v: String) { _uiState.update { it.copy(name = v, errorMessage = null) } }
    fun updateEmail(v: String) { _uiState.update { it.copy(email = v, errorMessage = null) } }
    fun updateMessage(v: String) { _uiState.update { it.copy(message = v, errorMessage = null) } }
    fun updatePhone(v: String) { _uiState.update { it.copy(phone = v, errorMessage = null) } }

    fun sendContact() {
        val s = _uiState.value
        if (s.name.isBlank() || s.email.isBlank() || s.phone.isBlank() || s.message.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            try {
                val body = com.example.qualitywash.network.dto.ContactRequest(s.name, s.email, s.phone, s.message)
                com.example.qualitywash.network.ApiClient.contactService.sendMessage(body)
                _uiState.update { it.copy(isSending = false, sentSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSending = false, errorMessage = "No se pudo enviar") }
            }
        }
    }

    fun resetSentFlag() { _uiState.update { it.copy(sentSuccessfully = false) } }
}
