package com.example.marketuniversitario.feature.business.ui.business

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.R
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.business.domain.models.BusinessCategory

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
                onAddProductClick = onNavigateToAddProduct,
                onEvent = onEvent,
                state = state
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
            modifier = Modifier
                .fillMaxWidth()
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
    onAddProductClick: () -> Unit,
    onEvent: (BusinessEvent) -> Unit,
    state: BusinessState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Información del negocio",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = if(state.isEditing) state.name
                else state.name.ifBlank { "Sin nombre" } ,
            onValueChange = { onEvent(BusinessEvent.NameChanged(it)) },
            label = { Text(text = "Nombre de tu negocio") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            readOnly = !state.isEditing
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = if(state.isEditing) state.description
                else state.description.ifBlank { "Sin descripción" },
            onValueChange = { onEvent(BusinessEvent.DescriptionChanged(it) )},
            label = { Text(text = "Eslogan o Descripción Corta") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            readOnly = !state.isEditing
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if(state.isEditing) onEvent(BusinessEvent.SaveBusiness)
                else onEvent(BusinessEvent.IsEditing)
                      },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if(state.isEditing) "Guardar Cambios" else "Editar Información",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        // --- SECCIÓN: GESTIÓN DE CONTENIDO ---
        Text(
            text = "Gestión de Contenido",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta 1: Total Productos
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Total \nProductos",
                value = "150",
                icon = Icons.Default.Inventory
            )
            // Tarjeta 2: Servicios Ofrecidos
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Servicios \nOfrecidos",
                value = "20",
                icon = Icons.Default.Build
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // --- SECCIÓN: CATEGORÍAS DEL NEGOCIO ---
        Text(
            text = "Categorías del Negocio",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            items(state.categories) { categoryId ->
                val category = BusinessCategory.fromId(categoryId)
                CategoryItem(
                    title = category.displayName,
                    icon = category.icon,
                    onEditClick = { onEvent(BusinessEvent.SelectCategoryToEdit(category.id)) }
                )
            }
            item {
                AddCategoryItem(
                    addCategory = { onEvent(BusinessEvent.ShowAddCategoryDialog) }
                )
            }
        }

        // --- DIÁLOGO DE OPCIONES AL PRESIONAR EL LAPICITO DE UNA CATEGORÍA ---
        if (state.selectedCategoryToEdit != null) {
            val categoryToEdit = BusinessCategory.fromId(state.selectedCategoryToEdit)

            AlertDialog(
                onDismissRequest = { onEvent(BusinessEvent.DismissEditCategoryDialog) },
                title = {
                    Text(
                        text = "Categoría: ${categoryToEdit.displayName}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = "¿Qué deseas hacer con la categoría \"${categoryToEdit.displayName}\"?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(BusinessEvent.ShowAddCategoryDialog)
                        }
                    ) {
                        Text("Cambiar por otra")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onEvent(BusinessEvent.RemoveCategory(categoryToEdit.id))
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // --- DIÁLOGO DE AÑADIR / CAMBIAR CATEGORÍA ---
        if (state.showAddCategoryDialog) {
            val availableCategories = BusinessCategory.getAvailableCategories(state.categories)

            AlertDialog(
                onDismissRequest = { onEvent(BusinessEvent.DismissAddCategoryDialog) },
                title = {
                    Text(
                        text = if (state.selectedCategoryToEdit != null) "Selecciona la Nueva Categoría" else "Añadir Categoría",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    if (availableCategories.isEmpty()) {
                        Text(
                            text = "Ya has agregado todas las categorías disponibles.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            items(availableCategories) { category ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (state.selectedCategoryToEdit != null) {
                                                onEvent(
                                                    BusinessEvent.ReplaceCategory(
                                                        oldCategoryId = state.selectedCategoryToEdit,
                                                        newCategoryId = category.id
                                                    )
                                                )
                                            } else {
                                                onEvent(BusinessEvent.AddCategory(category.id))
                                            }
                                        }
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = category.displayName,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = category.displayName,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onEvent(BusinessEvent.DismissAddCategoryDialog) }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    onManageClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.inversePrimary)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ícono izquierdo (reemplazar con Image() para tus assets 3D)
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.DarkGray
            )

            // Textos y botón derecho
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 10.sp,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Botón "Gestionar"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable { onManageClick() }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gestionar",
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    icon: ImageVector,
    onEditClick:() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(2.dp))
        Box(contentAlignment = Alignment.TopEnd) {
            // Círculo principal de la categoría
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { onEditClick() }
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Badge de edición (lapicito) en la esquina superior derecha
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = 4.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AddCategoryItem(
    addCategory: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable{ addCategory() }
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir Nueva",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Añadir Nueva",
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            fontSize = 10.sp
        )
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

@Preview(showBackground = true)
@Composable
fun ManageBusinessContentPreview(){
    MarketUniversitarioTheme() {
        ManageBusinessContent(
            onAddProductClick = {},
            onEvent = {},
            state = BusinessState()
        )
    }
}
