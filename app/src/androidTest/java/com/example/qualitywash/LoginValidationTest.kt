package com.example.qualitywash

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.qualitywash.ui.viewModel.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginValidationTest {
    @Test
    fun resetPassword_requiresAtSymbol_andRunWithHyphen() {
        val vm = LoginViewModel(com.example.qualitywash.ui.Data.UserRepository)
        vm.resetPassword("user@mail.com", "Abcdef12", "21080951")
        val state = vm.uiState.value
        assertEquals("La contraseña debe contener el carácter @", state.passwordError)
        vm.resetPassword("user@mail.com", "Abcdef12@", "21080951")
        val state2 = vm.uiState.value
        assertEquals("El RUN debe contener un guion -", state2.emailError ?: "El RUN debe contener un guion -")
    }
}
