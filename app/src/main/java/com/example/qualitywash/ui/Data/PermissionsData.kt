package com.example.qualitywash.ui.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Modelo de permiso individual
data class Permission(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

// Obtiene los permisos según el rol del usuario
object PermissionsRepository {
    fun getPermissionsByRole(role: UserRole): List<Permission> {
        return when (role) {
            UserRole.Cliente -> getClientePermissions()
            UserRole.ADMIN -> getAdminPermissions()
            else -> emptyList() // O maneja otros roles como necesites
        }
    }



    private fun getClientePermissions(): List<Permission> {
        return listOf(
            Permission(
                id = "cliente_shop",
                name = "Comprar Productos",
                description = "Navegar y comprar productos de limpieza",
                icon = Icons.Filled.ShoppingCart
            ),
            Permission(
                id = "cliente_cart",
                name = "Carrito de Compras",
                description = "Agregar productos al carrito y finalizar compra",
                icon = Icons.Filled.ShoppingBag
            ),
            Permission(
                id = "cliente_orders",
                name = "Ver Mis Pedidos",
                description = "Consultar historial y estado de pedidos",
                icon = Icons.Filled.Receipt
            ),
            Permission(
                id = "cliente_profile",
                name = "Gestionar Perfil",
                description = "Editar información personal y dirección de envío",
                icon = Icons.Filled.Person
            )
        )
    }

    private fun getAdminPermissions(): List<Permission> {
        return listOf(
            // Permisos de compras (igual que cliente)
            Permission(
                id = "admin_shop",
                name = "Comprar Productos",
                description = "Acceso a la tienda",
                icon = Icons.Filled.ShoppingCart
            ),

            // Permisos exclusivos de Admin
            Permission(
                id = "admin_products",
                name = "Gestionar Productos",
                description = "Crear, editar y eliminar productos del catálogo",
                icon = Icons.Filled.Inventory
            ),
            Permission(
                id = "admin_stock",
                name = "Control de Inventario",
                description = "Administrar stock y alertas de bajo inventario",
                icon = Icons.Filled.Warehouse
            ),
            Permission(
                id = "admin_orders",
                name = "Gestionar Pedidos",
                description = "Ver y procesar todos los pedidos de clientes",
                icon = Icons.Filled.LocalShipping
            ),
            Permission(
                id = "admin_users",
                name = "Gestionar Usuarios",
                description = "Crear, editar y eliminar usuarios del sistema",
                icon = Icons.Filled.People
            ),
            Permission(
                id = "admin_sales",
                name = "Reportes de Ventas",
                description = "Ver estadísticas y análisis de ventas",
                icon = Icons.Filled.BarChart
            ),
            Permission(
                id = "admin_config",
                name = "Configuración",
                description = "Ajustes generales del sistema",
                icon = Icons.Filled.Settings
            )
        )
    }

    // Verifica si un usuario tiene un permiso específico
    fun hasPermission(role: UserRole, permissionId: String): Boolean {
        return getPermissionsByRole(role).any { it.id == permissionId }
    }
}