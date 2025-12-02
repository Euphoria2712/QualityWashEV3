package com.example.qualitywash.network.dto

data class UserBackend(
    val id: Long?,
    val tipoUsuario: String?,
    val run: String?,
    val nombre: String?,
    val apellido: String?,
    val email: String,
    val telefono: String?,
    val direccion: String?,
    val fechaNacimiento: String?,
    val password: String?
)
