package com.example.qualitywash.network

import com.example.qualitywash.network.dto.ProductDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductService {
    @GET("api/productos")
    suspend fun getProducts(): List<ProductDto>

    @POST("api/productos")
    suspend fun addProduct(@Body product: ProductDto): ProductDto

    @PUT("api/productos/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: ProductDto): ProductDto

    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: String)
}
