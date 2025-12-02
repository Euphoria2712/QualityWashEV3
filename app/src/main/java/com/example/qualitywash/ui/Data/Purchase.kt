package com.example.qualitywash.ui.Data

data class Purchase(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val totalPrice: Double,
    val date: Long
)