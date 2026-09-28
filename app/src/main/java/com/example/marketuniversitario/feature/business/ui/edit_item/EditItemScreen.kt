package com.example.marketuniversitario.feature.business.ui.edit_item

import android.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.ProductCategory

@Composable
fun EditItemScreen(
    viewModel: EditItemViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.status) {
        when (state.status) {
            is EditItemStatus.Success -> {
                Toast.makeText(context, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                onNavigateBack()
                viewModel.onEvent(EditItemEvent.ResetStatus)
            }
            else -> {}
        }
    }

    EditItemContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemContent(
    state: EditItemState,
    onEvent: (EditItemEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isProduct = state.itemType == ItemType.PRODUCT
    
    val titleText = when {
        state.isEditMode && isProduct -> "Editar Producto"
        state.isEditMode && !isProduct -> "Editar Servicio"
        !state.isEditMode && isProduct -> "Añadir Producto"
        else -> "Añadir Servicio"
    }

    val buttonText = if (state.isEditMode) "GUARDAR CAMBIOS" else "CREAR ${if (isProduct) "PRODUCTO" else "SERVICIO"}"

    // AlertDialog para mostrar el error
    if (state.status is EditItemStatus.Error) {
        AlertDialog(
            onDismissRequest = { onEvent(EditItemEvent.ResetStatus) },
            title = { Text(text = "Error") },
            text = { Text(text = state.status.message) },
            confirmButton = {
                TextButton(onClick = { onEvent(EditItemEvent.ResetStatus) }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 10.dp)
    ) {
        // --- Top Bar Personalizado ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titleText,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // --- Contenedor Principal con bordes redondeados ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Box para añadir foto
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                    .clickable { /* Acción añadir foto */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu_camera),
                        contentDescription = "Cámara",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Añadir foto", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo: Nombre del Producto/Servicio
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(EditItemEvent.NameChanged(it)) },
                label = { Text("Nombre del ${if (isProduct) "Producto" else "Servicio"}", fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("Ej. Smartphone X") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Precio
            OutlinedTextField(
                value = state.price,
                onValueChange = { onEvent(EditItemEvent.PriceChanged(it)) },
                label = { Text("Precio", fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("0.00") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Cantidad (Solo si es Producto)
            if (isProduct) {
                OutlinedTextField(
                    value = state.stock,
                    onValueChange = { onEvent(EditItemEvent.StockChanged(it)) },
                    label = { Text("Cantidad Disponible", fontWeight = FontWeight.SemiBold) },
                    placeholder = { Text("Ej. 10") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo: Descripción
            OutlinedTextField(
                value = state.description,
                onValueChange = { onEvent(EditItemEvent.DescriptionChanged(it)) },
                label = { Text("Descripción", fontWeight = FontWeight.SemiBold) },
                placeholder = { Text("Descripción detallada...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Categoría (Dropdown utilizando EditItemState.selectedCategoryDisplayName)
            var categoryExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = state.selectedCategoryDisplayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría", fontWeight = FontWeight.SemiBold) },
                    placeholder = { Text("Seleccionar categoría") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    ProductCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                onEvent(EditItemEvent.CategoryChanged(category.id))
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón: CREAR / GUARDAR
            Button(
                onClick = { onEvent(EditItemEvent.SaveItem) },
                shape = RoundedCornerShape(24.dp),
                enabled = state.status !is EditItemStatus.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.status is EditItemStatus.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = buttonText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditItemScreenPreview() {
    MarketUniversitarioTheme() {
        EditItemContent(
            state = EditItemState(
                isEditMode = false,
                itemType = ItemType.PRODUCT
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
