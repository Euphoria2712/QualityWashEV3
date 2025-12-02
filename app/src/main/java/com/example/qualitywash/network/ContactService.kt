package com.example.qualitywash.network

import com.example.qualitywash.network.dto.ContactRequest
import com.example.qualitywash.network.dto.ContactResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactService {
    @POST("contactos")
    suspend fun sendMessage(@Body body: ContactRequest): ContactResponse
}
