package com.example.qualitywash.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import android.os.Environment
import java.io.File
import com.example.qualitywash.ui.viewModel.ProfileViewModel
import com.example.qualitywash.ui.viewModel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onLogoutComplete: () -> Unit,
    onNavigateToHistory: () -> Unit   
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory()
    )

    val user by viewModel.userState.collectAsState()
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()
    val photoUriState = remember { mutableStateOf<android.net.Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUriState.value?.let { viewModel.updateProfilePhoto(it.toString()) }
        }
    }
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.updateProfilePhoto(uri.toString())
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        val camGranted = perms[Manifest.permission.CAMERA] == true
        val readGranted = if (Build.VERSION.SDK_INT >= 33) {
            perms[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            perms[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        if (camGranted) {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "profile_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "com.example.qualitywash.fileprovider", file)
            photoUriState.value = uri
            takePictureLauncher.launch(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // Tarjeta de datos del usuario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    var isEditing by remember { mutableStateOf(false) }
                    var name by remember(user) { mutableStateOf(user.name) }
                    var email by remember(user) { mutableStateOf(user.email) }
                    var password by remember { mutableStateOf("") }
                    var address by remember(user) { mutableStateOf(user.addresss ?: "") }
                    var phone by remember(user) { mutableStateOf(user.phoneNumber ?: "") }

                    Text(
                        text = "Nombre:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isEditing) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Correo:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isEditing) {
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            singleLine = true,
                            label = { Text("Correo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Contraseña (opcional):",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            singleLine = true,
                            label = { Text("Nueva contraseña") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Dirección:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isEditing) {
                        Text(text = user.addresss ?: "", style = MaterialTheme.typography.titleMedium)
                    } else {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            singleLine = true,
                            label = { Text("Dirección") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Teléfono:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isEditing) {
                        Text(text = user.phoneNumber ?: "", style = MaterialTheme.typography.titleMedium)
                    } else {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            singleLine = true,
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedButton(onClick = {
                            val perms = mutableListOf(Manifest.permission.CAMERA)
                            if (Build.VERSION.SDK_INT >= 33) {
                                perms.add(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                perms.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                            permissionLauncher.launch(perms.toTypedArray())
                        }, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar foto")
                        }

                        Button(onClick = { isEditing = !isEditing }, shape = RoundedCornerShape(12.dp)) {
                            Text(if (isEditing) "Cancelar" else "Editar Perfil")
                        }

                        if (isEditing) {
                            Button(onClick = {
                                val pass = if (password.isBlank()) null else password
                                viewModel.updateProfile(name, email, pass, address, phone)
                                isEditing = false
                            }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A896))) {
                                Text("Confirmar cambios", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = {
                        val perms = if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.READ_MEDIA_IMAGES) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        permissionLauncher.launch(perms)
                        pickImageLauncher.launch("image/*")
                    }, shape = RoundedCornerShape(12.dp)) {
                        Text("Elegir de galería")
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -----------------------------------------------------
            // ✅ BOTÓN VER HISTORIAL
            // -----------------------------------------------------
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A896)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.History, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Ver Historial",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -----------------------------------------------------
            // CERRAR SESIÓN
            // -----------------------------------------------------
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogoutComplete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión")
            }
            if (message != null) {
                androidx.compose.runtime.LaunchedEffect(message) {
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                    viewModel.clearMessage()
                }
            }
        }
    }
}
