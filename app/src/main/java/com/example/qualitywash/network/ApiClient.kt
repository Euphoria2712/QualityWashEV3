package com.example.qualitywash.network

import com.example.qualitywash.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private fun client(authToken: String? = null): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val headerInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder().apply {
                if (!authToken.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $authToken")
                }
            }.build()
            chain.proceed(req)
        }
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private fun retrofit(baseUrl: String, authToken: String? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client(authToken))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userService: UserService by lazy {
        retrofit(BuildConfig.USERS_BASE_URL).create(UserService::class.java)
    }

    fun userServiceAuthed(token: String): UserService {
        return retrofit(BuildConfig.USERS_BASE_URL, token).create(UserService::class.java)
    }

    val productService: ProductService by lazy {
        retrofit(BuildConfig.PRODUCTS_BASE_URL).create(ProductService::class.java)
    }

    fun productServiceAuthed(token: String): ProductService {
        return retrofit(BuildConfig.PRODUCTS_BASE_URL, token).create(ProductService::class.java)
    }

    val salesService: SalesService by lazy {
        retrofit(BuildConfig.SALES_BASE_URL).create(SalesService::class.java)
    }

    fun salesServiceAuthed(token: String): SalesService {
        return retrofit(BuildConfig.SALES_BASE_URL, token).create(SalesService::class.java)
    }

    val contactService: ContactService by lazy {
        retrofit(BuildConfig.CONTACT_BASE_URL).create(ContactService::class.java)
    }
}
