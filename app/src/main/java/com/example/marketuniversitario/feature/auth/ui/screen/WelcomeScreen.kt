package com.example.marketuniversitario.feature.auth.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marketuniversitario.R

@Composable
fun WelcomeScreen(
    onNavigateToLoginScreen: () -> Unit
) {
    // Definición de los colores de tu Figma
    val guindaUNMSM = Color(0xFF8A002B)
    val textoOscuro = Color(0xFF333333)
    val tituloGuinda = Color(0xFF3D000E)

    // Surface asegura que el fondo sea 100% blanco puro
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Botón de retroceso y Logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Lógica para volver */ }) {
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

            Spacer(modifier = Modifier.height(100.dp))

            // Título Principal
            Text(
                text = "Comenzamos",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = tituloGuinda,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Botón de Google (Outlined)
            OutlinedButton(
                onClick = { /* Lógica futura de Google */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = textoOscuro
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified // Importante para que no se pinte de negro
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(35.dp))

            // Texto "or"
            Text(
                text = "or",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Botón de Inicio de Sesión
            Button(
                onClick = onNavigateToLoginScreen, // Función exacta de tus compañeros
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = guindaUNMSM,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Loguearse con una cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(35.dp))

            // Enlace al Registro
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "¿No tienes una cuenta?", color = Color.Gray, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Regístrate", color = guindaUNMSM, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}