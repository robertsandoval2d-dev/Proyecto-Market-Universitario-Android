package com.example.marketuniversitario.feature.auth.ui.sign_up

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marketuniversitario.R
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.auth.ui.util.GoogleSignInLauncher
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    state: SignUpState,
    onEvent: (SignUpEvent) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Adaptable a Modo Oscuro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Evita que colisione con la barra superior
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SignUpTopBar(onNavigateBack = onNavigateBack)
            SignUpLayout (
                state = state,
                onEvent = onEvent,
                onGoogleSignInClick = { onEvent(SignUpEvent.GoogleSignIn("")) }
            )
        }
    }
}

@Composable
fun SignUpTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            modifier = Modifier.height(30.dp)
        )
    }
}

@Composable
fun SignUpLayout(
    state: SignUpState,
    onEvent: (SignUpEvent) -> Unit,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 40.sp
        )
        Spacer(modifier = Modifier.height(30.dp))

        SignUpForm(
            state = state,
            onEvent = onEvent
        )

        Spacer(modifier = Modifier.height(30.dp))
        SignUpSocialFooter(
            onGoogleSignInClick = onGoogleSignInClick,
            enabled = state.status !is SignUpStatus.Loading)
    }
}

@Composable
fun SignUpForm(
    state: SignUpState,
    onEvent: (SignUpEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(SignUpEvent.EmailChanged(it)) },
            label = {Text("Correo institucional")},
            leadingIcon = {Icon(Icons.Filled.Email, "Correo institucional")},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small, // Unificado con el Theme
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(SignUpEvent.PasswordChanged(it)) },
            label = {Text("Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Contraseña"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { onEvent(SignUpEvent.ConfirmPasswordChanged(it)) },
            label = {Text("Confirmar contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Confirmar contraseña"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
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

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Principal
        Button(
            onClick = { onEvent(SignUpEvent.SignUp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = "Registrar Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SignUpSocialFooter(
    onGoogleSignInClick: () -> Unit,
    enabled: Boolean
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Separador "O continuar con"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
            Text(
                text = "  O continuar con  ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de Google centrado
        OutlinedButton(
            onClick =  onGoogleSignInClick ,
            enabled = enabled,
            modifier = Modifier
                .size(52.dp),
            contentPadding = PaddingValues(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Sign In",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ResultDialog(success: Boolean, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (success) "Registro exitoso" else "Error") },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
fun LoadingDialog() {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { },
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
             modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.medium,
             color = MaterialTheme.colorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onRegisterSuccess: () -> Unit, //Para cuandro requiera verificación con correo
    onNavigateHome: () -> Unit ,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleSignInLauncher = remember {
        GoogleSignInLauncher()
    }

    SignUpScreen(
        state = state,
        onEvent = { event ->
            if (event is SignUpEvent.GoogleSignIn) {
                scope.launch {
                    googleSignInLauncher.getIdToken(context)
                        .onSuccess { token -> viewModel.onEvent(SignUpEvent.GoogleSignIn(token)) }
                        .onFailure { error -> viewModel.onGoogleSignInError(error) }
                    }
                } else {
                    viewModel.onEvent(event)
                }
            },
        onNavigateBack = onNavigateBack
    )

    when (val status = state.status) {
        is SignUpStatus.Error -> {
            ResultDialog(
                success = false,
                message = status.message,
                onDismiss = { viewModel.onEvent(SignUpEvent.DismissDialog) }
            )
        }
        is SignUpStatus.Success -> {
            ResultDialog(
                success = true,
                message = if (status.isEmailVerified) {
                    "Tu cuenta ha sido creada exitosamente."
                } else {
                    "Cuenta creada. Revisa tu correo institucional para verificar tu cuenta antes de iniciar sesión."
                },
                onDismiss = {
                    viewModel.onEvent(SignUpEvent.DismissDialog)
                    if (status.isEmailVerified) {
                        onNavigateHome()
                    } else {
                        onRegisterSuccess()
                    }
                }
            )
        }
        SignUpStatus.Loading -> { LoadingDialog() }
        SignUpStatus.Idle -> { }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun SignUpPreview() {
    val fakeState = SignUpState(
        email = "",
        password = "",
        confirmPassword = ""
    )
    MarketUniversitarioTheme() {
        SignUpScreen(
            state = fakeState,
            onEvent = {},
            onNavigateBack = {}
        )
    }

}