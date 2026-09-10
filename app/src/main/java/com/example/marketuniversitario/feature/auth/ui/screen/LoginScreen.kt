package com.example.marketuniversitario.feature.auth.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marketuniversitario.R
import com.example.marketuniversitario.feature.auth.ui.viewmodel.LoginState
import com.example.marketuniversitario.feature.auth.ui.viewmodel.LoginViewModel

@Composable
fun LoginContent(
    onLoginSubmit: (String, String) -> Unit
) {
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopCenter)
            .padding(horizontal = 30.dp)
            .safeDrawingPadding()
    ) {
        Text(
            text = "Login to your account",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            label = { Text(stringResource(R.string.login_address)) },
            value = address,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            onValueChange = { address = it },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            label = { Text(stringResource(R.string.login_pass)) },
            value = password,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onLoginSubmit(address, password) },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text("Sign In")
        }



    }
}

@Composable
fun LoginHeader () {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 25.dp)
    ) {
        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Spacer(
            modifier = Modifier
                .width(10.dp)
        )
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "waza"
        )
    }
}

//@Composable
//fun EditDataField(
//    @StringRes label: String,
//    keyboardOptions: KeyboardOptions,
//    value: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier
//){
//    TextField()
//}

@Composable
fun LoginScreen(
    onLoginSubmit: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LoginHeader()
        Spacer(modifier = Modifier.height(100.dp))
        LoginContent(
            onLoginSubmit
        )
    }
}

@Composable
fun LoginRoute(
    viewModel: LoginViewModel, //Métodos de firebase
    onLoginSuccess: () -> Unit  //Estado para el Wrapper
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect (state) {
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

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen( onLoginSubmit = { _, _ -> })
}
