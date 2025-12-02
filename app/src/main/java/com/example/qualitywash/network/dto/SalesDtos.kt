package com.example.qualitywash.network.dto

data class VentaRequest(
    val productoId: Long,
    val clienteId: Long,
    val cantidad: Int,
    val precioTotal: Double,
    val fechaVenta: String
)

data class VentaResponse(
    val id: Long,
    val productoId: Long,
    val clienteId: Long,
    val cantidad: Int,
    val precioTotal: Double,
    val fechaVenta: String
)
