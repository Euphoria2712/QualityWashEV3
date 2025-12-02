package com.example.qualitywash

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.qualitywash.ui.viewModel.RegisterViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterValidationTest {
    @Test
    fun register_validations_run_phone_password() {
        val vm = RegisterViewModel(com.example.qualitywash.ui.Data.UserRepository)
        vm.updateRun("12345678")
        vm.updateTelefono("56912345678")
        vm.updatePassword("Abcdef12")
        val s = vm.uiState.value
        assertEquals("Formato RUN inválido (ej: 12345678-9)", s.runError)
        assertEquals("Incluye + y sólo dígitos (ej: +56912345678)", s.telefonoError)
        assertEquals("Debe incluir el carácter @", s.passwordError)
    }
}
