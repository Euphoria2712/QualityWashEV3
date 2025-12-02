package com.example.qualitywash.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qualitywash.ui.viewModel.ContactViewModel
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    onNavigateBack: () -> Unit = {}
) {
    val vm: ContactViewModel = viewModel()
    val ui by vm.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(ui.sentSuccessfully) {
        if (ui.sentSuccessfully) {
            Toast.makeText(context, "Mensaje enviado exitosamente", Toast.LENGTH_SHORT).show()
            vm.resetSentFlag()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.name,
                onValueChange = vm::updateName,
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::updateEmail,
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.phone,
                onValueChange = vm::updatePhone,
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.message,
                onValueChange = vm::updateMessage,
                label = { Text("Mensaje") },
                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            if (ui.errorMessage != null) {
                Text(ui.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = vm::sendContact,
                enabled = !ui.isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.isSending) "Enviando..." else "Enviar")
            }
        }
    }
}
