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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marketuniversitario.R

@Composable
fun WelcomeHeader() {
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
            contentDescription = "Logo"
        )
    }

}

@Composable
fun WelcomeContent(
    onNavigateToLoginScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopCenter)
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "Comenzamos",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer( modifier = Modifier.height(35.dp))

        Button(
            onClick = {},
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text(
                text = "Continue with Google",
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        Spacer( modifier = Modifier.height(35.dp))

        Text(
            text = "or",
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )

        Spacer( modifier = Modifier.height(35.dp))

        Button(
            onClick = { onNavigateToLoginScreen() },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text(
                text = "Loguearse con una cuenta",
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        Spacer( modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿No tienes una cuenta?",
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Registrarse",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun WelcomeScreen(
    onNavigateToLoginScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        WelcomeHeader()
        Spacer(modifier = Modifier.height(100.dp))
        WelcomeContent(onNavigateToLoginScreen)
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onNavigateToLoginScreen = {})
}