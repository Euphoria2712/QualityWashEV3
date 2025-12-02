package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.qualitywash.ui.Data.ItemCarrito
import com.example.qualitywash.ui.Data.ProductosRepository
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.HistoryRepository
import com.example.qualitywash.ui.Data.Product
import com.example.qualitywash.ui.Data.ProductType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.qualitywash.R

class TiendaViewModel : ViewModel() {

    // Lista de productos disponibles
    private val _productos = MutableStateFlow<List<ProductosRepository>>(emptyList())
    val productos: StateFlow<List<ProductosRepository>> = _productos.asStateFlow()

    // Carrito de compras
    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()

    // Estado del panel del carrito
    private val _mostrarPanelCarrito = MutableStateFlow(false)
    val mostrarPanelCarrito: StateFlow<Boolean> = _mostrarPanelCarrito.asStateFlow()

    // Total del carrito
    private val _totalCarrito = MutableStateFlow(0.0)
    val totalCarrito: StateFlow<Double> = _totalCarrito.asStateFlow()

    // Cantidad total de items en el carrito
    private val _cantidadTotalItems = MutableStateFlow(0)
    val cantidadTotalItems: StateFlow<Int> = _cantidadTotalItems.asStateFlow()

    private val _mensajeCompra = MutableStateFlow<String?>(null)
    val mensajeCompra: StateFlow<String?> = _mensajeCompra.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            try {
                val remote = com.example.qualitywash.network.ApiClient.productService.getProducts()
                _productos.value = remote.mapIndexed { idx, p ->
                    ProductosRepository(
                        id = p.id?.toInt() ?: (idx + 1),
                        nombre = p.nombre,
                        descripcion = p.descripcion,
                        precio = p.precio,
                        imagen = R.drawable.detergente,
                        imagenUrl = p.imagenUrl
                    )
                }
            } catch (_: Exception) {
                _productos.value = emptyList()
            }
        }
    }

    fun agregarAlCarrito(producto: ProductosRepository) {
        _carrito.update { carritoActual ->
            val itemExistente = carritoActual.find { it.producto.id == producto.id }

            if (itemExistente != null) {
                carritoActual.map { item ->
                    if (item.producto.id == producto.id) {
                        item.copy(cantidad = item.cantidad + 1)
                    } else item
                }
            } else {
                carritoActual + ItemCarrito(producto = producto, cantidad = 1)
            }
        }
        actualizarTotales()
    }

    fun eliminarDelCarrito(productoId: Int) {
        _carrito.update { it.filter { item -> item.producto.id != productoId } }
        actualizarTotales()
    }

    fun actualizarCantidad(productoId: Int, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(productoId)
            return
        }

        _carrito.update { carritoActual ->
            carritoActual.map { item ->
                if (item.producto.id == productoId) {
                    item.copy(cantidad = nuevaCantidad)
                } else item
            }
        }
        actualizarTotales()
    }

    fun togglePanelCarrito() {
        _mostrarPanelCarrito.value = !_mostrarPanelCarrito.value
    }

    fun cerrarPanelCarrito() {
        _mostrarPanelCarrito.value = false
    }

    fun vaciarCarrito() {
        _carrito.value = emptyList()
        actualizarTotales()
    }

    fun procesarCompra() {
        val user = UserRepository.getCurrentUser() ?: return

        try {
            viewModelScope.launch {
                _carrito.value.forEach { item ->
                    val req = com.example.qualitywash.network.dto.VentaRequest(
                        productoId = item.producto.id.toLong(),
                        clienteId = user.id.toLongOrNull() ?: 0L,
                        cantidad = item.cantidad,
                        precioTotal = item.subtotal,
                        fechaVenta = System.currentTimeMillis().toString()
                    )
                    try {
                        com.example.qualitywash.network.ApiClient.salesService.createSale(req)
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {}

        // historial se obtiene desde el microservicio de ventas

        vaciarCarrito()
        cerrarPanelCarrito()
        _mensajeCompra.value = "Compra exitosa"
    }

    fun limpiarMensajeCompra() {
        _mensajeCompra.value = null
    }

    private fun actualizarTotales() {
        _totalCarrito.value = _carrito.value.sumOf { it.subtotal }
        _cantidadTotalItems.value = _carrito.value.sumOf { it.cantidad }
    }
}

// ✅✅✅ EXTENSIÓN PARA CONVERTIR TU PRODUCTO VIEJO AL MODELO NUEVO
fun ProductosRepository.toProduct(): Product {
    return Product(
        id = this.id.toString(),
        name = this.nombre,
        type = ProductType.PRODUCTO_FISICO,
        price = this.precio,
        stock = 999, // temporal hasta que hagas inventario real
        description = this.descripcion
    )
}
