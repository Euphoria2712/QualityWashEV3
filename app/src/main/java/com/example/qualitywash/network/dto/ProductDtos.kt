package com.example.qualitywash.network.dto

data class ProductDto(
    val id: Long?,
    val nombre: String,
    val tipo: String,
    val stock: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String?
)
