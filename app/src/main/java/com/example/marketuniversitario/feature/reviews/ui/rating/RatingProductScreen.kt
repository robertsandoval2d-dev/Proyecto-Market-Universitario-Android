package com.example.marketuniversitario.feature.reviews.ui.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme

val StarGold = Color(0xFFFFC107)

@Composable
fun RatingBottomSheetRoute(
    productId: String,
    studentId: String,
    studentName: String,
    productName: String,
    productImageUrl: String,
    viewModel: RatingViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(productId, studentId) {
        viewModel.onEvent(
            RatingEvent.InitData(
                productId = productId,
                studentId = studentId,
                studentName = studentName,
                productName = productName,
                productImageUrl = productImageUrl
            )
        )
    }

    LaunchedEffect(state.status) {
        if (state.status is RatingStatus.Success) {
            viewModel.onEvent(RatingEvent.ResetForm)
            onSuccess()
        }
    }

    RatingBottomSheet(
        state = state,
        onEvent = viewModel::onEvent,
        onDismiss = {
            viewModel.onEvent(RatingEvent.ResetForm)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingBottomSheet(
    state: RatingState,
    onEvent: (RatingEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        RatingBottomSheetContent(
            state = state,
            onEvent = onEvent,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun RatingBottomSheetContent(
    state: RatingState,
    onEvent: (RatingEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val maxChar = 200

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

        Text(
            text = if (state.isEditing) "Editar Calificación" else "Calificación de Producto",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.productImageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.productImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = state.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = state.productName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= state.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Estrella $i",
                    tint = StarGold,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onEvent(RatingEvent.RatingChanged(i)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.comment,
            onValueChange = { onEvent(RatingEvent.CommentChanged(it)) },
            placeholder = {
                Text(
                    "Cuéntanos más sobre tu experiencia con este producto...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            enabled = state.status !is RatingStatus.Loading
        )

        Text(
            text = "${state.comment.length}/$maxChar",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End
        )

        if (state.status is RatingStatus.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.status.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onEvent(RatingEvent.SubmitReview)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = state.rating > 0 && state.status !is RatingStatus.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            if (state.status is RatingStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (state.isEditing) "Actualizar Calificación" else "Guardar Calificación",
                    fontSize = 16.sp,
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
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(25.dp),
            enabled = state.status !is RatingStatus.Loading
        ) {
            Text("Cancelar", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBottomSheetContentPreview() {
    MarketUniversitarioTheme {
        RatingBottomSheetContent(
            state = RatingState(
                productName = "Calculadora Científica Casio",
                rating = 4,
                comment = "Excelente producto.",
                isEditing = true
            ),
            onEvent = {},
            onDismiss = {}
        )
    }
}
