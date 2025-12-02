package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualitywash.ui.Data.Purchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val userId: String) : ViewModel() {

    private val _historyState = MutableStateFlow<List<Purchase>>(emptyList())
    val historyState: StateFlow<List<Purchase>> = _historyState

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val ventas = com.example.qualitywash.network.ApiClient.salesService.getVentasPorUsuario(userId.toLong())
                _historyState.value = ventas.map { v ->
                    val epoch = v.fechaVenta.toLongOrNull() ?: System.currentTimeMillis()
                    Purchase(
                        id = v.id.toString(),
                        userId = v.clienteId.toString(),
                        productId = v.productoId.toString(),
                        productName = "Producto ${v.productoId}",
                        quantity = v.cantidad,
                        totalPrice = v.precioTotal,
                        date = epoch
                    )
                }
            } catch (_: Exception) {
                _historyState.value = emptyList()
            }
        }
    }
}
