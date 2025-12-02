package com.example.qualitywash

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.qualitywash.ui.Data.ProductosRepository
import com.example.qualitywash.ui.Data.User
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.UserRole
import com.example.qualitywash.ui.viewModel.TiendaViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarritoCompraInstrumentedTest {

    @Test
    fun agregar_actualizar_y_procesar_compra_funciona() {
        val vm = TiendaViewModel()

        val productosAPrueba = listOf(
            ProductosRepository(
                id = 1,
                nombre = "Detergente",
                descripcion = "Limpia",
                precio = 1500.0,
                imagen = 0
            ),
            ProductosRepository(
                id = 2,
                nombre = "Suavizante",
                descripcion = "Suaviza",
                precio = 2500.0,
                imagen = 0
            ),
            ProductosRepository(
                id = 3,
                nombre = "Jabón",
                descripcion = "Jabón en polvo",
                precio = 800.0,
                imagen = 0
            )
        )


        productosAPrueba.forEach { producto ->
            vm.agregarAlCarrito(producto)
        }

        val cantidadEsperada = productosAPrueba.size
        assertEquals(cantidadEsperada, vm.carrito.value.size)

        val totalEsperado = productosAPrueba.sumOf { it.precio }
        assertEquals(totalEsperado, vm.totalCarrito.value, 0.01)

    }
}

