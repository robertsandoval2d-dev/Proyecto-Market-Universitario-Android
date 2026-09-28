package com.example.marketuniversitario.feature.user.ui.profile_completion

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import com.example.marketuniversitario.feature.auth.ui.sign_up.LoadingDialog
import com.example.marketuniversitario.feature.auth.ui.sign_up.ResultDialog
import com.example.marketuniversitario.feature.user.domain.model.ProfileConstants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileCompletionRoute(
    viewModel: ProfileCompletionViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit,
    onNavigateToWelcome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ProfileCompletionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onValidateStep1 = { viewModel.validateStep1() }
    )

    when (val status = state.status) {
        is ProfileCompletionStatus.Error -> {
            ResultDialog(
                success = false,
                message = status.message,
                onDismiss = { viewModel.onEvent(ProfileCompletionEvent.DismissDialog) }
            )
        }
        is ProfileCompletionStatus.Success -> {
            LaunchedEffect(Unit) {
                onNavigateHome()
            }
        }
        is ProfileCompletionStatus.LoggedOut -> {
            LaunchedEffect(Unit) {
                onNavigateToWelcome()
            }
        }
        ProfileCompletionStatus.Loading -> {
            LoadingDialog()
        }
        ProfileCompletionStatus.Idle -> { }
    }
}

@Composable
fun ProfileCompletionScreen(
    state: ProfileCompletionState,
    onEvent: (ProfileCompletionEvent) -> Unit,
    onValidateStep1: () -> Boolean
) {
    // Pantalla 1 - Pantalla 2
    var currentStep by rememberSaveable { mutableStateOf(1) }
    
    BackHandler {
        if (currentStep == 2) {
            currentStep = 1
        } else {
            onEvent(ProfileCompletionEvent.Logout)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentStep == 2) currentStep = 1 else onEvent(ProfileCompletionEvent.Logout)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentStep == 1) "Completar Perfil" else "Preferencias",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (currentStep == 1) {
                Step1PersonalData(
                    state = state,
                    onEvent = onEvent,
                    onNext = { 
                        if (onValidateStep1()) {
                            currentStep = 2 
                        }
                    }
                )
            } else {
                Step2Preferences(
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1PersonalData(
    state: ProfileCompletionState,
    onEvent: (ProfileCompletionEvent) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateInteractionSource = remember { MutableInteractionSource() }
    val isDatePressed by dateInteractionSource.collectIsPressedAsState()
    
    if (isDatePressed) {
        showDatePicker = true
    }

    // imágen nativo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onEvent(ProfileCompletionEvent.PhotoSelected(it.toString())) } }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar picker
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            if (state.photoUri.isNullOrBlank()) {
                // Fallback Avatar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = state.name.firstOrNull()?.uppercase() ?: "?"
                    Text(
                        text = initial,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(state.photoUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            // Ícono de editar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar foto",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Añadir foto", color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(32.dp))

        // Campos de Texto
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(ProfileCompletionEvent.NameChanged(it)) },
            label = { Text("Nombre Completo") },
            leadingIcon = { Icon(Icons.Filled.Person, "Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.nameError != null,
            supportingText = if (state.nameError != null) { { Text(state.nameError) } } else null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Cumpleaños
        val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) } // val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) //SEBAS
        val dateString = state.birthday?.let { dateFormatter.format(Date(it)) } ?: ""

        OutlinedTextField(
            value = dateString,
            onValueChange = { },
            label = { Text("Cumpleaños") },
            leadingIcon = { Icon(Icons.Filled.DateRange, "Cumpleaños") },
            readOnly = true,
            interactionSource = dateInteractionSource,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { },
            label = { Text("Email Institucional") },
            leadingIcon = { Icon(Icons.Filled.Email, "Email") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.phone,
            onValueChange = { onEvent(ProfileCompletionEvent.PhoneChanged(it)) },
            label = { Text("Celular") },
            leadingIcon = { Icon(Icons.Filled.Phone, "Celular") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.phoneError != null,
            supportingText = if (state.phoneError != null) { { Text(state.phoneError) } } else null
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdowns de Facultad y Género
        ExposedDropdown(
            label = "Facultad",
            icon = Icons.Filled.LocationOn,
            options = ProfileConstants.faculties,
            selectedOption = state.faculty,
            onOptionSelected = { onEvent(ProfileCompletionEvent.FacultySelected(it)) },
            isError = state.facultyError != null,
            supportingText = if (state.facultyError != null) { { Text(state.facultyError) } } else null
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdown(
            label = "Género",
            icon = Icons.Filled.Wc,
            options = ProfileConstants.genders,
            selectedOption = state.gender,
            onOptionSelected = { onEvent(ProfileCompletionEvent.GenderSelected(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Siguiente", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal del DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onEvent(ProfileCompletionEvent.BirthdaySelected(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun Step2Preferences(
    state: ProfileCompletionState,
    onEvent: (ProfileCompletionEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Selecciona hasta 3 categorías de interés",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProfileConstants.defaultPreferences) { preference ->
                val isSelected = state.selectedPreferences.contains(preference)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEvent(ProfileCompletionEvent.PreferenceToggled(preference)) }
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onEvent(ProfileCompletionEvent.PreferenceToggled(preference)) }
                    )
                    Text(
                        text = preference,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¿Qué te gustaría hacer en UNIVPE?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdown(
            label = "Intención",
            icon = Icons.Filled.CheckCircle,
            options = ProfileConstants.intents,
            selectedOption = state.primaryIntent,
            onOptionSelected = { onEvent(ProfileCompletionEvent.IntentSelected(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onEvent(ProfileCompletionEvent.SubmitProfile) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Finalizar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = supportingText,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}