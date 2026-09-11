package com.example.marketuniversitario.feature.auth.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marketuniversitario.R
import com.example.marketuniversitario.feature.auth.ui.viewmodel.LoginState
import com.example.marketuniversitario.feature.auth.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSubmit: (String, String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    // Variables de Firebase
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    // Colores Institucionales
    val guindaUNMSM = Color(0xFF8A002B)
    val textoOscuro = Color(0xFF333333)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.Start // Alineado a la izquierda según tu Figma
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = textoOscuro
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_univpe),
                    contentDescription = "Logo UNIVPE",
                    modifier = Modifier.height(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título
            Text(
                text = "Inicia sesión en\ntu cuenta",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = textoOscuro,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de Correo
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Correo institucional") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = guindaUNMSM,
                    focusedLabelColor = guindaUNMSM
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = guindaUNMSM,
                    focusedLabelColor = guindaUNMSM
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recordarme y Olvidé mi contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = guindaUNMSM)
                    )
                    Text("Recordarme", color = Color.Gray, fontSize = 14.sp)
                }
                TextButton(onClick = { /* TODO: Recuperar contraseña */ }) {
                    Text("¿Olvidaste tu contraseña?", color = guindaUNMSM, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Principal
            Button(
                onClick = { onLoginSubmit(address, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = guindaUNMSM,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Lógica de navegación y Firebase (No modificar)
@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        onLoginSubmit = { address, password ->
            viewModel.login(address, password)
        }
    )
}