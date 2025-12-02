package com.example.qualitywash.ui.Screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.qualitywash.ui.Data.User
import com.example.qualitywash.ui.Data.UserRepository
import com.example.qualitywash.ui.Data.UserRole
import com.example.qualitywash.ui.theme.getRoleColor
import com.example.qualitywash.ui.theme.getRoleText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    onNavigateToPerfil: () -> Unit,
    onNavigateToTienda: () -> Unit = {},
    onNavigateToGestionUsuarios: () -> Unit = {},
    onNavigateToGestionProductos: () -> Unit = {},
    onNavigateToContacto: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser = UserRepository.getCurrentUser()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUser = currentUser,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    UserRepository.logoutUser()
                    onLogout()
                },
                onNavigateToPerfil = {
                    scope.launch { drawerState.close() }
                    onNavigateToPerfil()
                },
                onNavigateToTienda = {
                    scope.launch { drawerState.close() }
                    onNavigateToTienda()
                },
                // 👈 2. PASANDO el nuevo parámetro a DrawerContent
                onNavigateToGestionUsuarios = {
                    scope.launch { drawerState.close() }
                    onNavigateToGestionUsuarios()
                },
                onNavigateToGestionProductos = {
                    scope.launch { drawerState.close() }
                    onNavigateToGestionProductos()
                },
                onNavigateToContacto = {
                    scope.launch { drawerState.close() }
                    onNavigateToContacto()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Quality Wash",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            // Mostrar foto de perfil si existe
                            if (currentUser?.photoUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        Uri.parse(currentUser.photoUri)
                                    ),
                                    contentDescription = "Perfil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Menú"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF00A896),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            MainContent(
                modifier = Modifier.padding(paddingValues),
                userName = currentUser?.name ?: "Usuario",
                userRole = currentUser?.role ?: UserRole.Cliente
            )
        }
    }
}

@Composable
fun DrawerContent(
    currentUser: User?,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToTienda: () -> Unit = {},
    // 👈 3. AÑADIDO: Nuevo parámetro para la navegación de gestión de usuarios
    onNavigateToGestionUsuarios: () -> Unit,
    onNavigateToGestionProductos: () -> Unit,
    onNavigateToContacto: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con foto de perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF05668D),
                                Color(0xFF02C39A)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    // Foto de perfil circular
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUser?.photoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    Uri.parse(currentUser.photoUri)
                                ),
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Usuario",
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser?.name ?: "Usuario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = currentUser?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Badge de rol
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = getRoleText(currentUser?.role ?: UserRole.Cliente),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DrawerMenuItem(
                icon = Icons.Filled.Person,
                title = "Mi Perfil",
                onClick = onNavigateToPerfil
            )

            DrawerMenuItem(
                icon = Icons.Filled.ShoppingCart,
                title = "Tienda",
                onClick = onNavigateToTienda
            )

            DrawerMenuItem(
                icon = Icons.Filled.Email,
                title = "Contacto",
                onClick = onNavigateToContacto
            )

            // Opciones especiales según el rol
            if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.Cliente) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "ADMINISTRACIÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (currentUser?.role == UserRole.ADMIN) {
                    DrawerMenuItem(
                        icon = Icons.Filled.People,
                        title = "Gestionar Usuarios",
                        // 👈 4. USANDO el nuevo parámetro para navegar
                        onClick = onNavigateToGestionUsuarios,
                        color = Color(0xFFFF5722)
                    )

                    DrawerMenuItem(
                        icon = Icons.Filled.Store,
                        title = "Gestionar Productos",
                        onClick = onNavigateToGestionProductos,
                        color = Color(0xFFFF5722)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerMenuItem(
                icon = Icons.Filled.ExitToApp,
                title = "Cerrar Sesión",
                onClick = onLogout,
                color = Color(0xFFF44336)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Quality Wash v1.0",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = Color(0xFF667eea)
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color
            )
        },
        label = {
            Text(
                text = title,
                color = color,
                fontWeight = FontWeight.Medium
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    userName: String,
    userRole: UserRole
) {
    val gradientColors = listOf(
        Color(0xFF00A896),
        Color(0xFF05668D),
        Color(0xFF02C39A)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Tarjeta de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Bienvenida",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFFFFD700)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Hola, $userName!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bienvenido a Quality Wash",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badge de rol
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = getRoleColor(userRole).copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            getRoleColor(userRole)
                        )
                    ) {
                        Text(
                            text = getRoleText(userRole),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = getRoleColor(userRole),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resto del contenido (mantiene el código original)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Información",
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "¿Quiénes Somos?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quality Wash es tu centro de lavado de confianza, comprometido con la excelencia y el cuidado de tus vehículos.",
                        fontSize = 15.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Con más de 10 años de experiencia, ofrecemos servicios profesionales de lavado, encerado, pulido y detallado automotriz utilizando productos de la más alta calidad.",
                        fontSize = 15.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    FeatureItem(
                        icon = Icons.Filled.CheckCircle,
                        text = "Personal capacitado y profesional"
                    )
                    FeatureItem(
                        icon = Icons.Filled.CheckCircle,
                        text = "Productos ecológicos de alta calidad"
                    )
                    FeatureItem(
                        icon = Icons.Filled.CheckCircle,
                        text = "Atención personalizada"
                    )
                    FeatureItem(
                        icon = Icons.Filled.CheckCircle,
                        text = "Precios competitivos"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Acceso Rápido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessCard(
                    icon = Icons.Filled.ShoppingCart,
                    title = "Tienda",
                    modifier = Modifier.weight(1f),
                )
                QuickAccessCard(
                    icon = Icons.Filled.Wash,
                    title = "Lavado",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF667eea)
            )
        }
    }
}
