package com.example.marketuniversitario.feature.splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marketuniversitario.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Preview
@Composable
fun SplahScreenPreview(){
    SplashScreen ()
}
@Composable
fun SplashScreen() {
    LaunchedEffect(key1 = true) {
        delay(2500.milliseconds)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_univpe),
                contentDescription = stringResource(R.string.logo_univpe),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .size(65.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(25.dp)
            )
            Image(
                painter = painterResource(R.drawable.icon_box),
                contentDescription = stringResource(R.string.icono_caja),
                modifier = Modifier.size(100.dp)
            )
        }

        Image(
            painter = painterResource(R.drawable.gb_globe),
            contentDescription = stringResource(R.string.fondo_universidad),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onNavigate: (String) -> Unit
){
    val destination by viewModel.destination.collectAsState()
    LaunchedEffect(destination) {
        destination?.let { onNavigate(it) }
    }
    SplashScreen()

}