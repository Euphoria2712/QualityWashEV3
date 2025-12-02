package com.example.qualitywash.ui.Data

data class Product(
    val id: String,
    val name: String,
    val type: ProductType,
    val price: Double,
    val stock: Int,
    val description: String
)