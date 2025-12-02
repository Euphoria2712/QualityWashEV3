package com.example.qualitywash.ui.Data

import android.util.Log

// Enumeración de roles simplificada
enum class UserRole {
    Cliente,   // Usuario que compra productos
    ADMIN      // Administrador del sistema
}

// Modelo de Usuario
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.Cliente,
    val photoUri: String? = null,
    val phoneNumber: String? = null,
    val addresss: String? = null
)

// Repositorio de usuarios
object UserRepository {
    private val users = mutableListOf<User>()
    private var currentUser: User? = null
    var authToken: String? = null

    private const val TAG = "UserRepository"
    private fun debug(msg: String) {
        try {
            Log.d(TAG, msg)
        } catch (_: Exception) {}
    }

    init {
        val admin = User(
            id = "2",
            name = "Administrador",
            email = "admin@mail.com",
            password = "Admin123@",
            role = UserRole.ADMIN
        )
        users.add(admin)
        debug("Admin agregado: ${admin.email}")
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        run: String,
        apellido: String,
        telefono: String,
        direccion: String,
        fechaNacimiento: String
    ): Pair<Boolean, String> {
        debug("Intentando registrar: $email")
        return try {
            val dto = com.example.qualitywash.network.ApiClient.userService.register(
                com.example.qualitywash.network.dto.UserBackend(
                    id = null,
                    tipoUsuario = "Cliente",
                    run = run,
                    nombre = name,
                    apellido = apellido,
                    email = email,
                    telefono = telefono,
                    direccion = direccion,
                    fechaNacimiento = fechaNacimiento,
                    password = password
                )
            )
            val role = if ((dto.tipoUsuario ?: "Cliente").equals("ADMIN", true)) UserRole.ADMIN else UserRole.Cliente
            val newUser = User(
                id = (dto.id ?: (users.size + 1)).toString(),
                name = dto.nombre ?: name,
                email = dto.email,
                password = password,
                role = role
            )
            users.add(newUser)
            currentUser = newUser
            Pair(true, "Registro exitoso")
        } catch (e: Exception) {
            Pair(false, "No se pudo registrar en el servidor")
        }
    }

    suspend fun adminCreateUser(
        name: String,
        email: String,
        password: String,
        run: String,
        apellido: String,
        telefono: String,
        direccion: String,
        fechaNacimiento: String,
        tipoUsuario: String
    ): Pair<Boolean, String> {
        if (!isAdmin()) return Pair(false, "No tienes permisos")
        return try {
            val dto = com.example.qualitywash.network.ApiClient.userServiceAuthed(authToken ?: "").register(
                com.example.qualitywash.network.dto.UserBackend(
                    id = null,
                    tipoUsuario = tipoUsuario,
                    run = run,
                    nombre = name,
                    apellido = apellido,
                    email = email,
                    telefono = telefono,
                    direccion = direccion,
                    fechaNacimiento = fechaNacimiento,
                    password = password
                )
            )
            refreshUsers()
            Pair(true, "Usuario creado")
        } catch (e: Exception) {
            val msg = if (e is retrofit2.HttpException) {
                "HTTP ${e.code()}"
            } else {
                "Error al crear usuario"
            }
            Pair(false, msg)
        }
    }

    suspend fun loginUser(email: String, password: String): Pair<Boolean, String> {
        debug("=== INTENTO DE LOGIN ===")
        debug("Email: $email")
        return try {
            val res = com.example.qualitywash.network.ApiClient.userService.login(
                com.example.qualitywash.network.dto.LoginRequest(email, password)
            )
            authToken = res.token
            val dto = com.example.qualitywash.network.ApiClient.userServiceAuthed(authToken!!).getUserByEmail(email)
            val role = if ((dto.tipoUsuario ?: "Cliente").equals("ADMIN", true)) UserRole.ADMIN else UserRole.Cliente
            val user = User(
                id = (dto.id ?: 0L).toString(),
                name = (dto.nombre ?: "") + (if (!dto.apellido.isNullOrBlank()) " " + dto.apellido else ""),
                email = dto.email,
                password = password,
                role = role,
                phoneNumber = dto.telefono,
                addresss = dto.direccion
            )
            currentUser = user
            Pair(true, "Inicio de sesión exitoso")
        } catch (e: Exception) {
            Pair(false, "No se pudo iniciar sesión contra el servidor")
        }
    }

    suspend fun resetPassword(email: String, run: String, newPassword: String): Pair<Boolean, String> {
        return try {
            val service = com.example.qualitywash.network.ApiClient.userService
            service.resetPassword(com.example.qualitywash.network.dto.ResetPasswordRequest(email, run, newPassword))
            if (currentUser?.email == email) {
                currentUser = currentUser?.copy(password = newPassword)
            }
            Pair(true, "Contraseña actualizada")
        } catch (e: Exception) {
            Pair(false, "No se pudo actualizar la contraseña")
        }
    }

    fun logoutUser() {
        debug("Logout: ${currentUser?.email}")
        currentUser = null
    }

    fun getCurrentUser(): User? = currentUser

    fun isUserLoggedIn(): Boolean = currentUser != null

    fun getAllUsers(): List<User> = users.toList()

    fun isAdmin(): Boolean = currentUser?.role == UserRole.ADMIN

    fun updateProfilePhoto(photoUri: String): Boolean {
        val user = currentUser ?: return false
        val updatedUser = user.copy(photoUri = photoUri)
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = updatedUser
            currentUser = updatedUser
            debug("Foto actualizada: ${user.email}")
            return true
        }
        return false
    }

    suspend fun updateCurrentUserRemote(
        name: String,
        email: String,
        password: String?,
        address: String?,
        phoneNumber: String?
    ): Pair<Boolean, String> {
        val token = authToken ?: return Pair(false, "Sesión requerida")
        val user = currentUser ?: return Pair(false, "Sesión requerida")
        return try {
            val service = com.example.qualitywash.network.ApiClient.userServiceAuthed(token)
            val backend = service.getUserByEmail(user.email)
            val updated = backend.copy(
                nombre = name,
                email = email,
                password = password ?: backend.password,
                direccion = address ?: backend.direccion,
                telefono = phoneNumber ?: backend.telefono
            )
            val id = (backend.id ?: 0L).toString()
            service.updateUser(id, updated)
            val newRole = user.role
            currentUser = user.copy(
                name = name,
                email = email,
                password = password ?: user.password,
                addresss = address,
                phoneNumber = phoneNumber,
                role = newRole
            )
            Pair(true, "Perfil actualizado")
        } catch (e: Exception) {
            Pair(false, "No se pudo actualizar el perfil")
        }
    }

    // FUNCIONES ADMIN PARA GESTIONAR USUARIOS
    fun createUser(name: String, email: String, password: String, role: UserRole): Pair<Boolean, String> {
        if (!isAdmin()) {
            return Pair(false, "No tienes permisos para crear usuarios")
        }

        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return Pair(false, "Este email ya está registrado")
        }

        val newUser = User(
            id = (users.size + 1).toString(),
            name = name,
            email = email,
            password = password,
            role = role
        )

        users.add(newUser)
        debug("Admin creó usuario: $email con rol $role")
        return Pair(true, "Usuario creado exitosamente")
    }

    fun updateUserRole(userId: String, newRole: UserRole): Boolean {
        if (!isAdmin()) return false

        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = users[index]
            users[index] = user.copy(role = newRole)
            debug("Admin actualizó rol de ${user.email} a $newRole")
            return true
        }
        return false
    }

    suspend fun deleteUserRemote(userId: String): Pair<Boolean, String> {
        if (!isAdmin()) return Pair(false, "No tienes permisos")
        if (currentUser?.id == userId) return Pair(false, "No puedes eliminarte a ti mismo")

        val token = authToken
        if (token.isNullOrBlank()) {
            return Pair(false, "Sesión requerida para eliminar en el servidor")
        }

        return try {
            val res = com.example.qualitywash.network.ApiClient.userServiceAuthed(token).deleteUser(userId)
            if (res.isSuccessful) {
                users.removeIf { it.id == userId }
                debug("Admin eliminó usuario remoto ID: $userId")
                Pair(true, "Usuario eliminado")
            } else {
                val code = res.code()
                val msg = when (code) {
                    401 -> "Sesión requerida"
                    403 -> "No autorizado (requiere rol ADMIN)"
                    404 -> "Usuario no encontrado"
                    405 -> "Servidor no soporta eliminar (DELETE)"
                    else -> "Servidor respondió $code"
                }
                Pair(false, msg)
            }
        } catch (e: Exception) {
            val msg = if (e is retrofit2.HttpException) {
                val code = e.code()
                if (code == 404 || code == 405) {
                    "Servidor no soporta eliminar (DELETE)"
                } else {
                    "HTTP $code"
                }
            } else {
                "Error de red al eliminar"
            }
            Pair(false, msg)
        }
    }

    suspend fun refreshUsers() {
        try {
            val list = com.example.qualitywash.network.ApiClient.userService.getUsers()
            users.clear()
            users.addAll(
                list.map {
                    val role = if ((it.tipoUsuario ?: "Cliente").equals("ADMIN", true)) UserRole.ADMIN else UserRole.Cliente
                    User(
                        id = (it.id ?: (users.size + 1)).toString(),
                        name = ((it.nombre ?: "") + (if (!it.apellido.isNullOrBlank()) " " + it.apellido else "")),
                        email = it.email,
                        password = "",
                        role = role,
                        phoneNumber = it.telefono,
                        addresss = it.direccion
                    )
                }
            )
        } catch (_: Exception) {}
    }

    fun clearUsers() {
        users.clear()
        currentUser = null
    }
    fun setCurrentUserForTest(u: User?) {
        currentUser = u
    }
}
