package com.example.marketuniversitario.feature.home.ui.components.ProductQuickView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductQuickViewBottomSheet(
    product: FeedProduct,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onRequestOrderClick: (productId: String) -> Unit,
    onNavigateToBusiness: (businessId: String) -> Unit,
    onNavigateToFullDetail: (productId: String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Header del Producto (Imagen + Nombre + Precio)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUrl.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                                }
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "S/ %.2f".format(product.price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Negocio (Tarjeta sutil clickeable)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBusiness(product.businessId) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Vendido por",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = product.businessName.ifBlank { "Negocio Universitario" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ir al negocio",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Principal: Solicitar Pedido
            Button(
                onClick = { onRequestOrderClick(product.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Solicitar Pedido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Secundario: Ver detalles completos
            OutlinedButton(
                onClick = { onNavigateToFullDetail(product.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Ver detalles completos y opiniones",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
