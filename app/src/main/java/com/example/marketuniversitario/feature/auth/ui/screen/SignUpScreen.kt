package com.example.marketuniversitario.feature.auth.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marketuniversitario.R
import com.example.marketuniversitario.feature.auth.ui.viewmodel.SignUpState
import com.example.marketuniversitario.feature.auth.ui.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    onSignUpSubmit: (String, String, String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SignUpTopBar(onNavigateBack = onNavigateBack)
            SignUpLayout (
                onSignUpSubmit = onSignUpSubmit,
                onGoogleSignInClick = onGoogleSignInClick
            )
        }
    }
}

@Composable
fun SignUpTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    // Header
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_univpe),
            contentDescription = "Logo UNIVPE",
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
fun SignUpLayout(
    onSignUpSubmit: (String, String, String) -> Unit,
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
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        SignUpForm(onSignUpSubmit = onSignUpSubmit)
        Spacer(modifier = Modifier.height(30.dp))
        SignUpSocialFooter(onGoogleSignInClick = onGoogleSignInClick)
    }
}

@Composable
fun SignUpForm(
    onSignUpSubmit: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
){
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = {Text("Correo institucional")},
            leadingIcon = {Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Correo institucional"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8A002B),
                focusedLabelColor = Color(0xFF8A002B),
                focusedLeadingIconColor = Color(0xFF8A002B)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {Text("Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Contraseña"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8A002B),
                focusedLabelColor = Color(0xFF8A002B),
                focusedLeadingIconColor = Color(0xFF8A002B)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {Text("Confirmar contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Confirmar contraseña"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8A002B),
                focusedLabelColor = Color(0xFF8A002B),
                focusedLeadingIconColor = Color(0xFF8A002B)
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Botón Principal
        Button(
            onClick = { onSignUpSubmit(address,password,confirmPassword) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8A002B),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Registrar Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SignUpSocialFooter(onGoogleSignInClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Separador "O continuar con"
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "O continuar con",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Google
        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp)
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
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        if (state is SignUpState.Success) {
            onRegisterSuccess()
        }
    }

    SignUpScreen(
        onSignUpSubmit = { email, password, confirmPassword ->
            viewModel.register(email, password, confirmPassword)
        },
        onNavigateBack = onNavigateBack,
        onGoogleSignInClick = { }
    )

}

@Preview
@Composable
fun SignUpPreview() {
    val string: String = ""
    val string2: String = ""
    val string3: String = ""
    SignUpScreen(
        onSignUpSubmit = {string, string2, string3 ->} ,
        onGoogleSignInClick = {},
        onNavigateBack = {}
    )
}