package com.example.qualitywash.network.dto

data class ContactRequest(
    val nombre: String,
    val email: String,
    val telefono: String,
    val mensaje: String
)

data class ContactResponse(
    val id: Long
)

