package com.example.marketuniversitario.feature.user.ui.terms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TermsRoute(onNavigateBack: () -> Unit) {
    TermsScreen(onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Reglas de la Comunidad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            RuleCard(
                ruleId = "RN-01",
                title = "Exclusividad Sanmarquina",
                description = "El uso de la aplicación y la publicación de productos está estrictamente limitada a estudiantes con correo institucional activo (@unmsm.edu.pe). Se prohíbe ceder la cuenta a terceros."
            )

            RuleCard(
                ruleId = "RN-02",
                title = "Garantía de Alquiler",
                description = "Para transacciones de alquiler de equipos o libros, ambas partes deberán acordar un método de garantía (documento de identidad físico temporal o depósito monetario)."
            )

            RuleCard(
                ruleId = "RN-03",
                title = "Zonas Seguras de Intercambio",
                description = "Se recomienda enfáticamente realizar todas las entregas físicas dentro del campus universitario, en zonas concurridas como facultades, biblioteca central o comedores."
            )

            RuleCard(
                ruleId = "RN-04",
                title = "Suspensión de Cuentas",
                description = "Cualquier reporte validado por conflictos, fraude, artículos prohibidos (sustancias ilícitas o armas) o la no devolución de artículos alquilados resultará en el baneo permanente de la plataforma."
            )
        }
    }
}

@Composable
fun RuleCard(
    ruleId: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = ruleId,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}