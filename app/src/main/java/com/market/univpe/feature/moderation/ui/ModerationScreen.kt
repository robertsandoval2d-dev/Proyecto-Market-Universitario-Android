package com.market.univpe.feature.moderation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Search
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
import com.market.univpe.core.theme.MarketUniversitarioTheme
import com.market.univpe.feature.user.domain.model.AccountStatus
import com.market.univpe.feature.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ModerationRoute(
    viewModel: ModerationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ModerationScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ModerationScreen(
    state: ModerationState,
    onEvent: (ModerationEvent) -> Unit
) {
    LaunchedEffect(state.errorMessage, state.successMessage) {
        if (state.errorMessage != null || state.successMessage != null) {
            kotlinx.coroutines.delay(3500)
            onEvent(ModerationEvent.ClearMessages)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Banner de Notificación Error / Éxito
        state.errorMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onEvent(ModerationEvent.ClearMessages) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        state.successMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onEvent(ModerationEvent.ClearMessages) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

            // Buscador de Usuarios
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(ModerationEvent.SearchQueryChanged(it)) },
                placeholder = { Text("Buscar por nombre o correo") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pestañas Desplazables (ScrollableTabRow para evitar saltos de línea)
            ScrollableTabRow(
                selectedTabIndex = state.selectedFilter.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (state.selectedFilter.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedFilter.ordinal]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                ModerationFilterTab.entries.forEach { filter ->
                    Tab(
                        selected = state.selectedFilter == filter,
                        onClick = { onEvent(ModerationEvent.FilterTabChanged(filter)) },
                        text = {
                            Text(
                                text = filter.displayName,
                                fontWeight = if (state.selectedFilter == filter) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No se encontraron usuarios en este filtro.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.filteredUsers) { user ->
                        UserModerationCard(
                            user = user,
                            onSanctionClick = { onEvent(ModerationEvent.OpenSanctionDialog(user)) },
                            onRestoreClick = { onEvent(ModerationEvent.RestoreAccount(user.id)) }
                        )
                    }
                }
            }

            // Modal de Aplicación de Sanción
            state.selectedUserForSanction?.let { user ->
                SanctionDialog(
                    user = user,
                    state = state,
                    onEvent = onEvent,
                    onDismiss = { onEvent(ModerationEvent.CloseSanctionDialog) }
                )
            }
        }
    }

@Composable
fun UserModerationCard(
    user: User,
    onSanctionClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    val (statusBg, statusText) = when (user.accountStatus) {
        AccountStatus.ACTIVE -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        AccountStatus.SUSPENDED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        AccountStatus.BANNED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name.ifBlank { "Sin Nombre" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = user.accountStatus.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (user.accountStatus != AccountStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                if (user.accountStatus == AccountStatus.SUSPENDED && user.suspendedUntil != null) {
                    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(user.suspendedUntil))
                    Text(
                        text = "• Suspendido hasta: $dateStr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                if (user.suspensionReason.isNotBlank()) {
                    Text(
                        text = "• Motivo: ${user.suspensionReason}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (user.accountStatus == AccountStatus.ACTIVE) {
                    Button(
                        onClick = onSanctionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sancionar Cuenta", fontSize = 13.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onRestoreClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restaurar Cuenta", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SanctionDialog(
    user: User,
    state: ModerationState,
    onEvent: (ModerationEvent) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aplicar Sanción a ${user.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Tipo de Sanción:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.sanctionStatusToApply == AccountStatus.SUSPENDED,
                        onClick = { onEvent(ModerationEvent.SanctionStatusChanged(AccountStatus.SUSPENDED)) }
                    )
                    Text("Suspensión Temporal", fontSize = 14.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.sanctionStatusToApply == AccountStatus.BANNED,
                        onClick = { onEvent(ModerationEvent.SanctionStatusChanged(AccountStatus.BANNED)) }
                    )
                    Text("Baneo Permanente", fontSize = 14.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }

                if (state.sanctionStatusToApply == AccountStatus.SUSPENDED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Duración de la Suspensión:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 3, 7, 30).forEach { days ->
                            FilterChip(
                                selected = state.suspensionDurationDays == days,
                                onClick = { onEvent(ModerationEvent.SuspensionDurationChanged(days)) },
                                label = { Text("${days}d", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.sanctionReason,
                    onValueChange = { onEvent(ModerationEvent.SanctionReasonChanged(it)) },
                    label = { Text("Motivo de la sanción *") },
                    placeholder = { Text("Escribe la justificación...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(ModerationEvent.ApplySanction) },
                enabled = state.sanctionReason.isNotBlank() && !state.isExecutingAction,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Confirmar Sanción")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ModerationScreenPreview() {
    val sampleUsers = listOf(
        User(
            id = "usr1",
            name = "Juan Pérez",
            email = "juan.perez@unmsm.edu.pe",
            phone = "987654321",
            accountStatus = AccountStatus.ACTIVE
        ),
        User(
            id = "usr2",
            name = "Maria López",
            email = "maria.lopez@unmsm.edu.pe",
            phone = "912345678",
            accountStatus = AccountStatus.SUSPENDED,
            suspendedUntil = System.currentTimeMillis() + 864000000,
            suspensionReason = "Múltiples reclamos por productos defectuosos"
        )
    )

    MarketUniversitarioTheme {
        ModerationScreen(
            state = ModerationState(
                users = sampleUsers,
                filteredUsers = sampleUsers,
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
