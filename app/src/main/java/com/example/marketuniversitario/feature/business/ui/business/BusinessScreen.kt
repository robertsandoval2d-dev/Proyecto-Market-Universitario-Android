package com.example.marketuniversitario.feature.business.ui.business

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.R
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme

@Composable
fun BusinessRoute(
    viewModel: BusinessViewModel = hiltViewModel(),
    onNavigateToAddProduct: () -> Unit = {},
    onHomeScreen: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    BusinessScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToAddProduct = onNavigateToAddProduct,
        onHomeScreen = onHomeScreen
    )
}

@Composable
fun BusinessScreen(
    state: BusinessState,
    onEvent: (BusinessEvent) -> Unit,
    onNavigateToAddProduct: () -> Unit = {},
    onHomeScreen: () -> Unit = {}
) {
    when (val status = state.status) {
        is BusinessStatus.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is BusinessStatus.NoBusiness -> {
            ActivateBusinessContent(
                onActivateClick = { onEvent(BusinessEvent.ActivateBusiness()) },
                onHomeScreen = onHomeScreen
            )
        }
        is BusinessStatus.HasBusiness -> {
            ManageBusinessContent(
                onAddProductClick = onNavigateToAddProduct
            )
        }
        is BusinessStatus.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = status.message)
            }
        }
        is BusinessStatus.Idle -> {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ActivateBusinessContent(
    onActivateClick: () -> Unit,
    onHomeScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.store),
            contentDescription = "tienda",
            modifier = Modifier.size(230.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "¡IMPULSA TU EMPRENDIMIENTO!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Activa tu perfil comercial para vender tus productos y servicios a la comunidad.\n!Es rápido y fácil¡",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onActivateClick,
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "CREAR MI PERFIL COMERCIAL",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        TextButton(onClick = onHomeScreen) {
            Text(text = "Quizás más tarde")
        }
    }
}

@Composable
private fun ManageBusinessContent(
    onAddProductClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Panel de Tu Negocio",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddProductClick) {
            Text(text = "Añadir nuevo producto")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivateBusinessContentPreview() {
    MarketUniversitarioTheme() {
        ActivateBusinessContent(
            onActivateClick = {},
            onHomeScreen = {}
        )
    }
}
