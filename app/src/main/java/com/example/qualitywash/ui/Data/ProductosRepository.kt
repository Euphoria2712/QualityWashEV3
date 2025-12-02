
package com.example.qualitywash.ui.Data

// Modelo de Productos
data class ProductosRepository(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagen: Int, // Recurso drawable de respaldo
    val imagenUrl: String? = null // URL remota opcional
)

// Modelo de Carrito
data class ItemCarrito(
    val producto: ProductosRepository,
    var cantidad: Int = 1
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}
