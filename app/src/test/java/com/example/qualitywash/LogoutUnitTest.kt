package com.example.qualitywash

import com.example.qualitywash.ui.Data.User
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.UserRole
import org.junit.Assert.*
import org.junit.Test

class LogoutUnitTest {
    @Test
    fun logout_cierra_sesion_correctamente() {
        UserRepository.setCurrentUserForTest(
            User(id = "10", name = "Usuario", email = "u@u.com", password = "P@ssw0rd", role = UserRole.Cliente)
        )
        assertTrue(UserRepository.isUserLoggedIn())

        UserRepository.logoutUser()
        assertFalse(UserRepository.isUserLoggedIn())
        assertNull(UserRepository.getCurrentUser())
    }
}

