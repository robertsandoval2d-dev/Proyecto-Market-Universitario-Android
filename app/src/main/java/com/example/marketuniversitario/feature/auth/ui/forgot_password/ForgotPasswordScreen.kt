package com.example.marketuniversitario.feature.auth.ui.forgot_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marketuniversitario.R
import com.example.marketuniversitario.feature.auth.ui.sign_up.ResultDialog

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_univpe),
                    contentDescription = "Logo UNIVPE",
                    modifier = Modifier.height(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Título y Descripción
            Text(
                text = "Recuperar\ncontraseña",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 40.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa tu correo institucional y te enviaremos un enlace para restablecer tu contraseña.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de Correo
            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                label = { Text("Correo institucional") },
                leadingIcon = { Icon(Icons.Filled.Email, "Correo institucional") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Botón Principal
            Button(
                onClick = { onEvent(ForgotPasswordEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Enviar enlace", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ForgotPasswordScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )

    when (val status = state.status) {
        is ForgotPasswordStatus.Error -> {
            ResultDialog(
                success = false,
                message = status.message,
                onDismiss = { viewModel.onEvent(ForgotPasswordEvent.DismissDialog) }
            )
        }
        is ForgotPasswordStatus.Success -> {
            ResultDialog(
                success = true,
                message = "Se ha enviado un enlace de recuperación a tu correo.",
                onDismiss = {
                    viewModel.onEvent(ForgotPasswordEvent.DismissDialog)
                    onNavigateBack() // Regresa al login tras el éxito
                }
            )
        }
        ForgotPasswordStatus.Loading -> { }
        ForgotPasswordStatus.Idle -> { }
    }
}