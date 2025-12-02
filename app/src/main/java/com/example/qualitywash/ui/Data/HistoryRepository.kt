package com.example.qualitywash.ui.Data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID

object HistoryRepository {

    private val _history: SnapshotStateList<Purchase> = mutableStateListOf()
    val history: SnapshotStateList<Purchase> = _history

    fun getUserHistory(userId: String): List<Purchase> {
        return _history.filter { it.userId == userId }
    }

    fun addPurchase(userId: String, product: Product, quantity: Int) {
        val purchase = Purchase(
            id = UUID.randomUUID().toString(),
            userId = userId,
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            totalPrice = product.price * quantity,
            date = System.currentTimeMillis()
        )
        _history.add(purchase)
    }
}