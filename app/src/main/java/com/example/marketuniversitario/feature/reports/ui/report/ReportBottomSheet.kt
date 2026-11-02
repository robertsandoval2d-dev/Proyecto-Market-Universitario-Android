package com.example.marketuniversitario.feature.reports.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.reports.domain.models.ReportConstants

@Composable
fun ReportBottomSheetRoute(
    orderId: String,
    businessId: String,
    studentId: String,
    orderStatusAtReport: String,
    viewModel: ReportViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId, studentId) {
        viewModel.onEvent(
            ReportEvent.InitData(
                orderId = orderId,
                businessId = businessId,
                studentId = studentId,
                orderStatusAtReport = orderStatusAtReport
            )
        )
    }

    LaunchedEffect(state.status) {
        if (state.status is ReportStatus.Success) {
            viewModel.onEvent(ReportEvent.ResetForm)
            onSuccess()
        }
    }

    ReportBottomSheet(
        state = state,
        onEvent = viewModel::onEvent,
        onDismiss = {
            viewModel.onEvent(ReportEvent.ResetForm)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    state: ReportState,
    onEvent: (ReportEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ReportBottomSheetContent(
            state = state,
            onEvent = onEvent,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheetContent(
    state: ReportState,
    onEvent: (ReportEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val maxChar = 300
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reportar Incidencia",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.existingReport != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ya existe un reporte para esta orden",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estado: ${state.existingReport.status.displayName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    if (!state.existingReport.adminResolutionNotes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nota del Admin: ${state.existingReport.adminResolutionNotes}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Text(
            text = "Selecciona el motivo principal del reporte:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.reasonCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ReportConstants.reportReasons.forEach { reason ->
                    DropdownMenuItem(
                        text = { Text(reason) },
                        onClick = {
                            onEvent(ReportEvent.ReasonChanged(reason))
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Describe los detalles de la incidencia:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(ReportEvent.DescriptionChanged(it)) },
            placeholder = {
                Text(
                    "Explica detalladamente qué sucedió con tu pedido...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = state.status !is ReportStatus.Loading && state.existingReport == null
        )

        Text(
            text = "${state.description.length}/$maxChar",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End
        )

        if (state.status is ReportStatus.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.status.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onEvent(ReportEvent.SubmitReport) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = state.description.isNotBlank() && state.status !is ReportStatus.Loading && state.existingReport == null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            if (state.status is ReportStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Enviar Reporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            enabled = state.status !is ReportStatus.Loading
        ) {
            Text("Cancelar", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportBottomSheetContentPreview() {
    MarketUniversitarioTheme(darkTheme = false) {
        ReportBottomSheetContent(
            state = ReportState(
                reasonCategory = "Producto defectuoso o dañado",
                description = "El producto llegó roto."
            ),
            onEvent = {},
            onDismiss = {}
        )
    }
}
