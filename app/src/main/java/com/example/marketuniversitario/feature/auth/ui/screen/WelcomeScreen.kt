package com.example.marketuniversitario.feature.auth.ui.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marketuniversitario.R
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.auth.ui.viewmodel.WelcomeState
import com.example.marketuniversitario.feature.auth.ui.viewmodel.WelcomeStatus
import com.example.marketuniversitario.feature.auth.ui.viewmodel.WelcomeViewModel

@Composable
fun WelcomeScreen(
    state: WelcomeState,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
    onGoogleSignInClick : () -> Unit,
    onDialogDismiss : () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Botón de retroceso y Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_univpe),
                    contentDescription = "Logo UNIVPE",
                    modifier = Modifier
                        .height(30.dp)
                        .align(alignment = Alignment.CenterStart)
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            // Título Principal
            Text(
                text = "Comenzamos",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón de Google (Outlined)
            OutlinedButton(
                onClick = onGoogleSignInClick,
                enabled = state.status !is WelcomeStatus.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = MaterialTheme.shapes.small
            ) {
                //Dialog
                if (state.status is WelcomeStatus.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continuar con Google",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            // Texto "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "  Or  ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón de Inicio de Sesión
            Button(
                onClick = onNavigateToLoginScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Loguearse con una cuenta",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Enlace al Registro
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(onClick = onNavigateToSignUpScreen) {
                    Text(
                        text = "Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    val status = state.status
    if (status is WelcomeStatus.Error) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            confirmButton = { TextButton(onClick = onDialogDismiss) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(status.message) }
        )
    }
}

@Composable
fun WelcomeRoute(
    viewModel: WelcomeViewModel,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
    onLoginSuccess: () -> Unit
){
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    WelcomeScreen(
        state = state,
        onNavigateToLoginScreen = onNavigateToLoginScreen,
        onNavigateToSignUpScreen = onNavigateToSignUpScreen,
        onGoogleSignInClick = { viewModel.onGoogleSignInClick(context) },
        onDialogDismiss = viewModel::onDialogDismiss
    )
    LaunchedEffect(state.status) {
        if (state.status is WelcomeStatus.Success) {
            onLoginSuccess()
        }
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun WelcomeScreenPreview() {
    MarketUniversitarioTheme() {
    WelcomeScreen(
        onNavigateToLoginScreen = {},
        onNavigateToSignUpScreen = {},
        onGoogleSignInClick = {},
        onDialogDismiss = {},
        state = WelcomeState()
    )
    }
}
