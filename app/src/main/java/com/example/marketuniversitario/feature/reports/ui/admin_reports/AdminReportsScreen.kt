package com.example.marketuniversitario.feature.reports.ui.admin_reports

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminReportsRoute(
    viewModel: AdminReportsViewModel = hiltViewModel(),
    onNavigateToChat: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    AdminReportsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToChat = onNavigateToChat
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminReportsScreen(
    state: AdminReportsState,
    onEvent: (AdminReportsEvent) -> Unit,
    onNavigateToChat: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(AdminReportsEvent.ClearMessages)
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(AdminReportsEvent.ClearMessages)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            // 1. Filtros principales (TabRow)
            TabRow(
                selectedTabIndex = state.selectedFilter.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedFilter.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                ReportFilterTab.entries.forEach { filter ->
                    Tab(
                        selected = state.selectedFilter == filter,
                        onClick = { onEvent(AdminReportsEvent.FilterChanged(filter)) },
                        text = {
                            Text(
                                text = filter.displayName,
                                fontWeight = if (state.selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Contenido Lista de Reportes
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay reportes en esta categoría.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.filteredReports) { report ->
                        AdminReportCard(
                            report = report,
                            onClick = { onEvent(AdminReportsEvent.SelectReport(report)) }
                        )
                    }
                }
            }
        }
    }

    // 3. Modal / Sheet para Detalle y Resolución de Reporte
    state.selectedReport?.let { report ->
        AdminReportDetailModal(
            report = report,
            state = state,
            onEvent = onEvent,
            onNavigateToChat = onNavigateToChat,
            onDismiss = { onEvent(AdminReportsEvent.SelectReport(null)) }
        )
    }
}

@Composable
fun AdminReportCard(
    report: Report,
    onClick: () -> Unit
) {
    val (statusBg, statusText) = when (report.status) {
        ReportStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        ReportStatus.RESOLVED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        ReportStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(report.createdAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orden: #${report.orderId.take(8)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = report.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.reasonCategory,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateStr,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportDetailModal(
    report: Report,
    state: AdminReportsState,
    onEvent: (AdminReportsEvent) -> Unit,
    onNavigateToChat: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle del Reporte",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción del estudiante
            Text(
                text = "Motivo: ${report.reasonCategory}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Descripción del Estudiante:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = report.description,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contacto de partes e información de evidencia MVP
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aviso MVP: Contacto Directo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Para solicitar fotos de evidencia, contacta a las partes fuera de la app:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val buyer = state.selectedReportContacts.buyer
                    val seller = state.selectedReportContacts.seller

                    Text(
                        text = "• Comprador: ${buyer?.name ?: report.studentId} | Tel: ${buyer?.phone?.ifBlank { "No registrado" } ?: "Cargando..."}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "• Vendedor: ${seller?.name ?: "Negocio #${report.businessId.take(6)}"} | Tel: ${seller?.phone?.ifBlank { "No registrado" } ?: "Cargando..."}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Ver Chat
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onNavigateToChat(report.orderId)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver historial de Chat de la Orden")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Zona de Resolución
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zona de Resolución Admin",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.adminNotes,
                onValueChange = { onEvent(AdminReportsEvent.AdminNotesChanged(it)) },
                label = { Text("Notas de Resolución (Interno) *") },
                placeholder = { Text("Escribe la justificación de la decisión tomada...") },
                modifier = Modifier.fillMaxWidth()
                    .imePadding(),
                enabled = !state.isResolving && report.status == ReportStatus.PENDING,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (report.status == ReportStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEvent(AdminReportsEvent.RejectReport) },
                        enabled = state.adminNotes.isNotBlank() && !state.isResolving,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Rechazar Reporte", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    val resolveButtonText = if (report.orderStatusAtReport == "COMPLETED") {
                        "Aceptar Reporte"
                    } else {
                        "Resolver"
                    }

                    Button(
                        onClick = { onEvent(AdminReportsEvent.ResolveAndCancelOrder) },
                        enabled = state.adminNotes.isNotBlank() && !state.isResolving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(resolveButtonText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Este caso ya fue cerrado. Notas: ${report.adminResolutionNotes.orEmpty()}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, name = "1. Lista de Reportes Admin")
@Composable
fun AdminReportsScreenPreview() {
    MarketUniversitarioTheme {
        AdminReportsScreen(
            state = AdminReportsState(
                selectedFilter = ReportFilterTab.PENDING,
                reports = listOf(
                    Report(
                        id = "rep1",
                        orderId = "ord_8812345",
                        businessId = "bus_101",
                        studentId = "usr_55",
                        reasonCategory = "Producto defectuoso o dañado",
                        description = "La hamburguesa llegó helada y el empaque aplastado.",
                        status = ReportStatus.PENDING,
                        orderStatusAtReport = "PREPARING",
                        createdAt = System.currentTimeMillis() - 3600000
                    ),
                    Report(
                        id = "rep2",
                        orderId = "ord_998877",
                        businessId = "bus_202",
                        studentId = "usr_66",
                        reasonCategory = "Vendedor no contesta / No se presentó",
                        description = "Esperé 30 minutos en el pabellón A y nunca llegó.",
                        status = ReportStatus.PENDING,
                        orderStatusAtReport = "READY_FOR_PICKUP",
                        createdAt = System.currentTimeMillis() - 7200000
                    )
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Detalle de Reporte Admin")
@Composable
fun AdminReportDetailModalPreview() {
    MarketUniversitarioTheme {
        AdminReportDetailModal(
            report = Report(
                id = "rep1",
                orderId = "ord_8812345",
                businessId = "bus_101",
                studentId = "usr_55",
                reasonCategory = "Producto defectuoso o dañado",
                description = "El libro que pedí tiene las primeras 20 páginas rotas y manchadas de tinta.",
                status = ReportStatus.PENDING,
                orderStatusAtReport = "COMPLETED",
                createdAt = System.currentTimeMillis() - 3600000
            ),
            state = AdminReportsState(
                adminNotes = "Contacté al comprador por WhatsApp y verificó las fotos."
            ),
            onEvent = {},
            onNavigateToChat = {},
            onDismiss = {}
        )
    }
}
