package com.example.qualitywash.ui.Screen

import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.qualitywash.ui.Data.User
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.UserRole
import com.example.qualitywash.ui.theme.getRoleColor
import com.example.qualitywash.ui.theme.getRoleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var usuarios by remember { mutableStateOf(UserRepository.getAllUsers()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Función para refrescar la lista
    val scope = rememberCoroutineScope()
    fun refreshUsers() {
        scope.launch {
            UserRepository.refreshUsers()
            usuarios = UserRepository.getAllUsers()
        }
    }

    LaunchedEffect(Unit) {
        try {
            UserRepository.refreshUsers()
            refreshUsers()
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00A896),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFF00A896)
            ) {
                Icon(Icons.Default.PersonAdd, "Agregar usuario", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (UserRepository.authToken.isNullOrBlank()) {
                Text("Sesión requerida (inicia sesión contra el servidor)", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Total Usuarios",
                    value = usuarios.size.toString(),
                    icon = Icons.Default.People,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Admins",
                    value = usuarios.count { it.role == UserRole.ADMIN }.toString(),
                    icon = Icons.Default.Shield,
                    color = Color(0xFFFF5722),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de usuarios
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarios) { user ->
                    UserCard(
                        user = user,
                        onEdit = {
                            selectedUser = user
                            showEditDialog = true
                        },
                        onDelete = {
                            selectedUser = user
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Diálogos
    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { run, apellido, telefono, direccion, fecha, name, email, password, role ->
                scope.launch {
                    val tipo = if (role == UserRole.ADMIN) "ADMIN" else "Cliente"
                    val (success, message) = UserRepository.adminCreateUser(
                        name = name,
                        email = email,
                        password = password,
                        run = run,
                        apellido = apellido,
                        telefono = telefono,
                        direccion = direccion,
                        fechaNacimiento = fecha,
                        tipoUsuario = tipo
                    )
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        refreshUsers()
                        showCreateDialog = false
                    }
                }
            }
        )
    }

    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = { showEditDialog = false },
            onSave = { newRole ->
                UserRepository.updateUserRole(selectedUser!!.id, newRole)
                Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                refreshUsers()
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && selectedUser != null) {
        DeleteUserDialog(
            user = selectedUser!!,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                scope.launch {
                    val (success, message) = UserRepository.deleteUserRemote(selectedUser!!.id)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        refreshUsers()
                    }
                    showDeleteDialog = false
                }
            }
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Column {
                Text(title, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(getRoleColor(user.role).copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = getRoleColor(user.role)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(user.email, fontSize = 14.sp, color = Color.Gray)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getRoleColor(user.role).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = getRoleText(user.role),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = getRoleColor(user.role),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF2196F3))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFF44336))
                }
            }
        }
    }
}

@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreate: (
        run: String,
        apellido: String,
        telefono: String,
        direccion: String,
        fechaNacimiento: String,
        name: String,
        email: String,
        password: String,
        role: UserRole
    ) -> Unit
) {
    var run by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.Cliente) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = run,
                    onValueChange = { run = it },
                    label = { Text("RUN (ej: 12345678-9)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono (ej: +56912345678)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Rol:", fontWeight = FontWeight.Bold)
                Row {
                    FilterChip(
                        selected = selectedRole == UserRole.Cliente,
                        onClick = { selectedRole = UserRole.Cliente },
                        label = { Text("Cliente") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedRole == UserRole.ADMIN,
                        onClick = { selectedRole = UserRole.ADMIN },
                        label = { Text("Admin") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(run, apellido, telefono, direccion, fechaNacimiento, name, email, password, selectedRole) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column {
                Text("Usuario: ${user.name}", fontWeight = FontWeight.Bold)
                Text("Email: ${user.email}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cambiar rol:", fontWeight = FontWeight.Bold)
                Row {
                    FilterChip(
                        selected = selectedRole == UserRole.Cliente,
                        onClick = { selectedRole = UserRole.Cliente },
                        label = { Text("Cliente") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedRole == UserRole.ADMIN,
                        onClick = { selectedRole = UserRole.ADMIN },
                        label = { Text("Admin") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(selectedRole) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFF44336)) },
        title = { Text("Eliminar Usuario") },
        text = {
            Text("¿Estás seguro de eliminar a ${user.name}? Esta acción no se puede deshacer.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
