package com.example.qualitywash.ui.Screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


enum class WashState {
    IDLE,           // Esperando para iniciar
    WASHING,        // Lavado en progreso
    FINISHED,       // Lavado terminado
    UNLOCKED        // Máquina desbloqueada
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WashScreen(
    onNavigateBack: () -> Unit = {}
) {
    var washState by remember { mutableStateOf(WashState.IDLE) }
    var timeRemaining by remember { mutableIntStateOf(0) }
    var securityCode by remember { mutableStateOf("") }
    var userInputCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableIntStateOf(5) } // Tiempo en minutos

    val scope = rememberCoroutineScope()

    // Cronómetro
    LaunchedEffect(washState, timeRemaining) {
        if (washState == WashState.WASHING && timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        } else if (washState == WashState.WASHING && timeRemaining == 0) {
            washState = WashState.FINISHED
        }
    }

    val gradientColors = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFFf093fb)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Servicio de Lavado",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667eea),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Estado de la máquina
                MachineStatusCard(washState)

                Spacer(modifier = Modifier.height(24.dp))

                // Contenido según el estado
                when (washState) {
                    WashState.IDLE -> {
                        IdleContent(
                            selectedTime = selectedTime,
                            onTimeSelected = { selectedTime = it },
                            onStartWash = {
                                securityCode = generateSecurityCode()
                                timeRemaining = selectedTime * 60 // Convertir a segundos
                                washState = WashState.WASHING
                                userInputCode = ""
                                showError = false
                            }
                        )
                    }
                    WashState.WASHING -> {
                        WashingContent(
                            timeRemaining = timeRemaining,
                            securityCode = securityCode
                        )
                    }
                    WashState.FINISHED -> {
                        FinishedContent(
                            securityCode = securityCode,
                            userInputCode = userInputCode,
                            showError = showError,
                            onCodeChange = {
                                userInputCode = it
                                showError = false
                            },
                            onUnlock = {
                                if (userInputCode == securityCode) {
                                    washState = WashState.UNLOCKED
                                } else {
                                    showError = true
                                }
                            }
                        )
                    }
                    WashState.UNLOCKED -> {
                        UnlockedContent(
                            onNewWash = {
                                washState = WashState.IDLE
                                userInputCode = ""
                                showError = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MachineStatusCard(washState: WashState) {
    val statusColor = when (washState) {
        WashState.IDLE -> Color(0xFF9E9E9E)
        WashState.WASHING -> Color(0xFF2196F3)
        WashState.FINISHED -> Color(0xFFFF9800)
        WashState.UNLOCKED -> Color(0xFF4CAF50)
    }

    val statusText = when (washState) {
        WashState.IDLE -> "Máquina Disponible"
        WashState.WASHING -> "Lavado en Progreso"
        WashState.FINISHED -> "Lavado Completado"
        WashState.UNLOCKED -> "Máquina Desbloqueada"
    }

    val statusIcon = when (washState) {
        WashState.IDLE -> Icons.Filled.CheckCircle
        WashState.WASHING -> Icons.Filled.Refresh
        WashState.FINISHED -> Icons.Filled.Done
        WashState.UNLOCKED -> Icons.Filled.Lock
    }

    // Animación de rotación para el ícono cuando está lavando
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (washState == WashState.WASHING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = statusText,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(if (washState == WashState.WASHING) rotation else 0f),
                tint = statusColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IdleContent(
    selectedTime: Int,
    onTimeSelected: (Int) -> Unit,
    onStartWash: () -> Unit
) {
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
            Text(
                text = "Selecciona el Tiempo de Lavado",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF667eea)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones de tiempo
            val timeOptions = listOf(5, 10, 15, 20, 30)

            timeOptions.forEach { time ->
                TimeOptionButton(
                    time = time,
                    isSelected = selectedTime == time,
                    onClick = { onTimeSelected(time) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartWash,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Servicio",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TimeOptionButton(
    time: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFF667eea).copy(alpha = 0.1f) else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF667eea) else Color.Gray
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (isSelected) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Wash,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Lavado de $time minutos",
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF667eea)
                )
            }
        }
    }
}

@Composable
fun WashingContent(
    timeRemaining: Int,
    securityCode: String
) {
    // Animación de pulso
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Cronómetro
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tiempo Restante",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = formatTime(timeRemaining),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF2196F3),
                trackColor = Color(0xFF2196F3).copy(alpha = 0.2f),
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Código de seguridad
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Código de Seguridad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667eea)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFF9800),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = securityCode,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800),
                    letterSpacing = 8.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Guarda este código para desbloquear la máquina",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FinishedContent(
    securityCode: String,
    userInputCode: String,
    showError: Boolean,
    onCodeChange: (String) -> Unit,
    onUnlock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFF9800)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa el Código de Seguridad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF667eea),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Para desbloquear la máquina",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userInputCode,
                onValueChange = { if (it.length <= 4) onCodeChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Código de 4 dígitos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text(
                            text = "Código incorrecto. Inténtalo de nuevo.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF667eea),
                    focusedLabelColor = Color(0xFF667eea)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onUnlock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = userInputCode.length == 4,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Desbloquear Máquina",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UnlockedContent(
    onNewWash: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale),
                tint = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Máquina Desbloqueada!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ya puedes retirar tu vehículo",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNewWash,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nuevo Servicio",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun generateSecurityCode(): String {
    return (1000..9999).random().toString()
}