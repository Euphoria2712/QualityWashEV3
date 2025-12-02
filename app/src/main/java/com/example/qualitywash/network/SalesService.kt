package com.example.qualitywash.network

import com.example.qualitywash.network.dto.VentaRequest
import com.example.qualitywash.network.dto.VentaResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesService {
    @POST("ventas")
    suspend fun createSale(@Body sale: VentaRequest): VentaResponse

    @retrofit2.http.GET("ventas/usuario/{usuarioId}")
    suspend fun getVentasPorUsuario(@retrofit2.http.Path("usuarioId") usuarioId: Long): List<VentaResponse>
}
