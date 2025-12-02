package com.example.qualitywash.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qualitywash.network.ApiClient
import com.example.qualitywash.network.dto.ProductDto
import com.example.qualitywash.ui.Data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductoFormState(
    val nombre: String = "",
    val tipo: String = "",
    val stock: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val imagenUrl: String = "",
    val isEditing: Boolean = false,
    val editingId: Long? = null,
    val showForm: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class GestionProductosViewModel : ViewModel() {
    private val _productos = MutableStateFlow<List<ProductDto>>(emptyList())
    val productos: StateFlow<List<ProductDto>> = _productos

    private val _form = MutableStateFlow(ProductoFormState())
    val form: StateFlow<ProductoFormState> = _form

    init {
        loadProductos()
    }

    fun loadProductos() {
        viewModelScope.launch {
            try {
                val list = ApiClient.productService.getProducts()
                _productos.value = list
            } catch (_: Exception) {
                _productos.value = emptyList()
            }
        }
    }

    fun startCreate() {
        _form.value = ProductoFormState(showForm = true)
    }

    fun startEdit(p: ProductDto) {
        _form.value = ProductoFormState(
            nombre = p.nombre,
            tipo = p.tipo,
            stock = p.stock,
            descripcion = p.descripcion,
            precio = p.precio.toString(),
            imagenUrl = p.imagenUrl ?: "",
            isEditing = true,
            editingId = p.id
        ).copy(showForm = true)
    }

    fun updateNombre(v: String) { _form.value = _form.value.copy(nombre = v, errorMessage = null) }
    fun updateTipo(v: String) { _form.value = _form.value.copy(tipo = v, errorMessage = null) }
    fun updateStock(v: String) { _form.value = _form.value.copy(stock = v, errorMessage = null) }
    fun updateDescripcion(v: String) { _form.value = _form.value.copy(descripcion = v, errorMessage = null) }
    fun updatePrecio(v: String) { _form.value = _form.value.copy(precio = v, errorMessage = null) }
    fun updateImagenUrl(v: String) { _form.value = _form.value.copy(imagenUrl = v, errorMessage = null) }

    fun submit() {
        val s = _form.value
        if (s.nombre.isBlank() || s.tipo.isBlank() || s.stock.isBlank() || s.descripcion.isBlank() || s.precio.isBlank()) {
            _form.value = s.copy(errorMessage = "Completa todos los campos")
            return
        }
        val precioDouble = s.precio.toDoubleOrNull()
        if (precioDouble == null) {
            _form.value = s.copy(errorMessage = "Precio inválido")
            return
        }
        val token = UserRepository.authToken
        if (token.isNullOrBlank()) {
            _form.value = s.copy(errorMessage = "Sesión requerida")
            return
        }
        viewModelScope.launch {
            _form.value = s.copy(isLoading = true, errorMessage = null)
            try {
                val dto = ProductDto(
                    id = s.editingId,
                    nombre = s.nombre,
                    tipo = s.tipo,
                    stock = s.stock,
                    descripcion = s.descripcion,
                    precio = precioDouble,
                    imagenUrl = if (s.imagenUrl.isBlank()) null else s.imagenUrl
                )
                if (s.isEditing && s.editingId != null) {
                    ApiClient.productServiceAuthed(token).updateProduct(s.editingId.toString(), dto)
                } else {
                    ApiClient.productServiceAuthed(token).addProduct(dto)
                }
                loadProductos()
                _form.value = ProductoFormState(showForm = false)
            } catch (_: Exception) {
                _form.value = s.copy(isLoading = false, errorMessage = "No se pudo guardar")
            }
        }
    }

    fun deleteProduct(id: Long) {
        val token = UserRepository.authToken ?: return
        viewModelScope.launch {
            try {
                ApiClient.productServiceAuthed(token).deleteProduct(id.toString())
                loadProductos()
            } catch (_: Exception) {}
        }
    }
}
