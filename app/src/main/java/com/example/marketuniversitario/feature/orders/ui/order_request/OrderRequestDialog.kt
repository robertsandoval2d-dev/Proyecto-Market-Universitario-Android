package com.example.marketuniversitario.feature.orders.ui.order_request

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.marketuniversitario.feature.orders.domain.models.ProductRequest

@Composable
fun OrderRequestDialogRoute(
    viewModel: OrderRequestViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OrderRequestDialog(
        state = state,
        onEvent = viewModel::onEvent,
        onDismiss = onDismiss
    )
}

@Composable
fun OrderRequestDialog(
    state: OrderRequestState,
    onEvent: (OrderRequestEvent) -> Unit,
    onDismiss: () -> Unit
) {
    when (val status = state.status) {
        is OrderRequestStatus.Loading -> {
            AlertDialog(
                onDismissRequest = if (state.productRequest == null) onDismiss else ({ }),
                confirmButton = { },
                text = {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Procesando solicitud...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }

        is OrderRequestStatus.Error -> {
            OrderRequestErrorDialog(
                message = status.message,
                onDismiss = {
                    onEvent(OrderRequestEvent.Dismiss)
                    onDismiss()
                }
            )
        }

        is OrderRequestStatus.Success -> {
            OrderRequestSuccessDialog(
                onDismiss = {
                    onEvent(OrderRequestEvent.Dismiss)
                    onDismiss()
                }
            )
        }

        else -> {
            val product = state.productRequest
            if (product != null) {
                OrderRequestAlertDialog(
                    product = product,
                    quantity = state.quantity,
                    note = state.note,
                    onDismiss = {
                        onEvent(OrderRequestEvent.Dismiss)
                        onDismiss()
                    },
                    onQuantityChanged = {
                        quantity -> onEvent(OrderRequestEvent.QuantityChanged(quantity)) },
                    onNoteChanged = {
                        note -> onEvent(OrderRequestEvent.NoteChanged(note)) },
                    onConfirmOrder = {
                        val finalRequest = product.copy(
                            quantity = state.quantity,
                            note = state.note
                        )
                        onEvent(OrderRequestEvent.RequestOrder(finalRequest))
                    }
                )
            }
        }
    }
}

@Composable
fun OrderRequestAlertDialog(
    product: ProductRequest,
    quantity: Int,
    note: String,
    onDismiss: () -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onConfirmOrder: () -> Unit
) {
    val totalPrice = product.price * quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Solicitar Pedido",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (product.imageUrl.isNotBlank()) {
                                SubcomposeAsyncImage(
                                    model = product.imageUrl,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = product.businessName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Cantidad:", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
                            enabled = quantity > 1,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { onQuantityChanged(quantity + 1) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aumentar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total estimado:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "S/ %.2f".format(totalPrice),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChanged,
                    label = { Text("Nota para el emprendedor (opcional)") },
                    placeholder = { Text("Ej: Entregar en la biblioteca a las 3pm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmOrder,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar Solicitud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun OrderRequestSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("¡Solicitud enviada!") },
        text = {
            Text(
                "Tu solicitud fue enviada correctamente. " +
                        "El vendedor podrá revisarla y responderte."
            )
        }
    )
}

@Composable
fun OrderRequestErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        title = { Text("No se pudo enviar la solicitud") },
        text = { Text(message) }
    )
}
