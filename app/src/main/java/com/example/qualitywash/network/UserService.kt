package com.example.qualitywash.network

import com.example.qualitywash.network.dto.LoginRequest
import com.example.qualitywash.network.dto.LoginResponse
import com.example.qualitywash.network.dto.ResetPasswordRequest
import com.example.qualitywash.network.dto.UserBackend
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.Response

interface UserService {
    @POST("api/usuarios/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/usuarios")
    suspend fun register(@Body body: UserBackend): UserBackend

    @GET("api/usuarios/obtenerUsuarios")
    suspend fun getUsers(): List<UserBackend>

    @PUT("api/usuarios/actualizar/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserBackend): UserBackend

    @GET("api/usuarios/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserBackend

    @PUT("api/usuarios/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): UserBackend

    @DELETE("api/usuarios/eliminar/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Void>
}
