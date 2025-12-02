package com.example.qualitywash.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qualitywash.network.dto.ProductDto
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.UserRole
import com.example.qualitywash.ui.viewModel.GestionProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductosScreen(
    onNavigateBack: () -> Unit
) {
    val vm: GestionProductosViewModel = viewModel()
    val productos by vm.productos.collectAsState()
    val form by vm.form.collectAsState()

    val isAdmin = UserRepository.getCurrentUser()?.role == UserRole.ADMIN
    if (!isAdmin) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestionar Productos") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, null) }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                Text("No autorizado")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Productos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { vm.startCreate() }) { Icon(Icons.Filled.Add, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00A896),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(Modifier.weight(1f)) {
                items(productos) { p ->
                    ProductoItem(
                        producto = p,
                        onEdit = { vm.startEdit(p) },
                        onDelete = { if (p.id != null) vm.deleteProduct(p.id) }
                    )
                }
            }

            if (form.showForm) {
                Divider()
                ProductoForm(
                    formState = form,
                    onNombreChange = vm::updateNombre,
                    onTipoChange = vm::updateTipo,
                    onStockChange = vm::updateStock,
                    onDescripcionChange = vm::updateDescripcion,
                    onPrecioChange = vm::updatePrecio,
                    onImagenUrlChange = vm::updateImagenUrl,
                    onSubmit = vm::submit
                )
            }
        }
    }
}

@Composable
private fun ProductoItem(
    producto: ProductDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(producto.nombre) },
        supportingContent = { Text("${producto.descripcion} • ${producto.tipo} • ${producto.stock} • $${producto.precio}") },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, null) }
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null) }
            }
        }
    )
    Divider()
}

@Composable
private fun ProductoForm(
    formState: com.example.qualitywash.ui.viewModel.ProductoFormState,
    onNombreChange: (String) -> Unit,
    onTipoChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onImagenUrlChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        if (formState.errorMessage != null) {
            Text(formState.errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(value = formState.nombre, onValueChange = onNombreChange, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = formState.tipo, onValueChange = onTipoChange, label = { Text("Tipo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = formState.stock, onValueChange = onStockChange, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = formState.descripcion, onValueChange = onDescripcionChange, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = formState.precio, onValueChange = onPrecioChange, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = formState.imagenUrl, onValueChange = onImagenUrlChange, label = { Text("Imagen URL") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = onSubmit, enabled = !formState.isLoading) {
            Text(if (formState.isEditing) "Actualizar" else "Agregar")
        }
    }
}
