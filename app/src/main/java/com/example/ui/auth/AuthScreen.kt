package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.OnboardingProfileData
import com.example.data.model.calculateBmi
import com.example.data.model.describeBmiCategory
import com.example.ui.screens.CoreRoutineSymptoms
import com.example.ui.screens.PrimaryHealthGoals
import com.example.ui.screens.deriveAgeGroupLabel
import com.example.ui.viewmodel.AuthUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    authState: AuthUiState.Unauthenticated,
    initialOnboardingData: OnboardingProfileData = OnboardingProfileData(),
    onSignInLocal: (displayName: String) -> Unit = {},
    onSignUpLocal: (displayName: String, onboardingData: OnboardingProfileData) -> Unit = { _, _ -> },
    onToggleAuthMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by rememberSaveable { mutableStateOf(initialOnboardingData.fullName) }
    var ageInput by rememberSaveable { mutableStateOf(initialOnboardingData.age.toString()) }
    var selectedAge by rememberSaveable { mutableIntStateOf(initialOnboardingData.age.coerceIn(18, 90)) }
    var heightInput by rememberSaveable { mutableStateOf(initialOnboardingData.heightCm.toString()) }
    var weightInput by rememberSaveable { mutableStateOf(initialOnboardingData.weightKg.toString()) }
    var knownDiseasesInput by rememberSaveable { mutableStateOf(initialOnboardingData.knownDiseasesOrMedications) }
    var emailInput by rememberSaveable { mutableStateOf(initialOnboardingData.emailId) }
    var phoneInput by rememberSaveable { mutableStateOf(initialOnboardingData.phoneNumber) }
    var selectedGoal by rememberSaveable { mutableStateOf(initialOnboardingData.primaryHealthGoal) }
    var selectedSymptoms by rememberSaveable { mutableStateOf(initialOnboardingData.selectedSymptoms) }

    val parsedHeightCm = heightInput.toIntOrNull()?.coerceIn(50, 260) ?: initialOnboardingData.heightCm
    val parsedWeightKg = weightInput.toIntOrNull()?.coerceIn(15, 300) ?: initialOnboardingData.weightKg
    val calculatedBmi = remember(parsedHeightCm, parsedWeightKg) {
        calculateBmi(parsedHeightCm, parsedWeightKg)
    }
    val bmiCategory = remember(calculatedBmi) {
        describeBmiCategory(calculatedBmi)
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0E291D),
            Color(0xFF173D2C),
            Color(0xFF1D4935)
        )
    )

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFBF8F3).copy(alpha = 0.12f),
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color(0xFFE5A93C),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFFE5A93C).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color(0xFFE5A93C),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YOGA THERAPY & RECOVERY COMPANION",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "YogaSwasthya",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFFFBF8F3),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Disease-oriented Asanas, screened Pranayama, Dhyana, Indian dietary guidance & clinician-ready recovery tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD5E6DD),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF132F22).copy(alpha = 0.94f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            FilterChip(
                                selected = !authState.isSignUpMode,
                                onClick = { onToggleAuthMode(false) },
                                label = { Text("Quick Start") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8CD1B3),
                                    selectedLabelColor = Color(0xFF0E291D),
                                    labelColor = Color(0xFFD5E6DD)
                                ),
                                modifier = Modifier.testTag("auth_mode_signin_chip")
                            )
                            FilterChip(
                                selected = authState.isSignUpMode,
                                onClick = { onToggleAuthMode(true) },
                                label = { Text("Custom Profile Setup") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8CD1B3),
                                    selectedLabelColor = Color(0xFF0E291D),
                                    labelColor = Color(0xFFD5E6DD)
                                ),
                                modifier = Modifier.testTag("auth_mode_signup_chip")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (authState.isSignUpMode) {
                                "Create Your Personalized Therapy Profile"
                            } else {
                                "Welcome to YogaSwasthya"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (authState.isSignUpMode) {
                                "Enter your personal details, body metrics, known disease/medication, and health goal to customize your local recovery plan."
                            } else {
                                "Continue directly to your personalized yoga therapy plan, symptom logs, and clinician PDF reports stored locally on your device."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB8D1C4),
                            textAlign = TextAlign.Center
                        )

                        if (authState.isSignUpMode) {
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it.take(80) },
                                label = { Text("Your Full Name") },
                                placeholder = { Text("e.g., Aarav Sharma") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFFFBF8F3),
                                    unfocusedTextColor = Color(0xFFFBF8F3),
                                    focusedBorderColor = Color(0xFF8CD1B3),
                                    unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                    focusedLabelColor = Color(0xFF8CD1B3),
                                    unfocusedLabelColor = Color(0xFFB8D1C4)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = ageInput,
                                    onValueChange = { raw ->
                                        val digits = raw.filter { it.isDigit() }.take(3)
                                        ageInput = digits
                                        digits.toIntOrNull()?.let { parsed ->
                                            selectedAge = parsed.coerceIn(18, 90)
                                        }
                                    },
                                    label = { Text("Age (yrs)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFFFBF8F3),
                                        unfocusedTextColor = Color(0xFFFBF8F3),
                                        focusedBorderColor = Color(0xFF8CD1B3),
                                        unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF8CD1B3),
                                        unfocusedLabelColor = Color(0xFFB8D1C4)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("signup_age_input")
                                )

                                OutlinedTextField(
                                    value = heightInput,
                                    onValueChange = { raw ->
                                        heightInput = raw.filter { it.isDigit() }.take(3)
                                    },
                                    label = { Text("Height (cm)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFFFBF8F3),
                                        unfocusedTextColor = Color(0xFFFBF8F3),
                                        focusedBorderColor = Color(0xFF8CD1B3),
                                        unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF8CD1B3),
                                        unfocusedLabelColor = Color(0xFFB8D1C4)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("signup_height_input")
                                )

                                OutlinedTextField(
                                    value = weightInput,
                                    onValueChange = { raw ->
                                        weightInput = raw.filter { it.isDigit() }.take(3)
                                    },
                                    label = { Text("Weight (kg)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFFFBF8F3),
                                        unfocusedTextColor = Color(0xFFFBF8F3),
                                        focusedBorderColor = Color(0xFF8CD1B3),
                                        unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF8CD1B3),
                                        unfocusedLabelColor = Color(0xFFB8D1C4)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("signup_weight_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = String.format(
                                    Locale.US,
                                    "Age: %d yrs (%s) • BMI: %.1f kg/m² (%s)",
                                    selectedAge,
                                    deriveAgeGroupLabel(selectedAge),
                                    calculatedBmi,
                                    bmiCategory
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8CD1B3),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Slider(
                                value = selectedAge.toFloat(),
                                onValueChange = {
                                    selectedAge = it.toInt()
                                    ageInput = selectedAge.toString()
                                },
                                valueRange = 18f..85f,
                                modifier = Modifier.testTag("signup_age_slider")
                            )

                            OutlinedTextField(
                                value = knownDiseasesInput,
                                onValueChange = { knownDiseasesInput = it.take(300) },
                                label = { Text("Known Disease or Medication") },
                                placeholder = { Text("e.g., Cervical Spondylitis, Hypertension, Vitamin D3") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFFFBF8F3),
                                    unfocusedTextColor = Color(0xFFFBF8F3),
                                    focusedBorderColor = Color(0xFF8CD1B3),
                                    unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                    focusedLabelColor = Color(0xFF8CD1B3),
                                    unfocusedLabelColor = Color(0xFFB8D1C4)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_known_disease_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it.take(120) },
                                    label = { Text("Mail ID (Email)") },
                                    placeholder = { Text("name@example.com") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFFFBF8F3),
                                        unfocusedTextColor = Color(0xFFFBF8F3),
                                        focusedBorderColor = Color(0xFF8CD1B3),
                                        unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF8CD1B3),
                                        unfocusedLabelColor = Color(0xFFB8D1C4)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("signup_email_input")
                                )

                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { raw ->
                                        phoneInput = raw.filter { it.isDigit() || it in "+- ()" }.take(25)
                                    },
                                    label = { Text("Phone No.") },
                                    placeholder = { Text("+91 98765 43210") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFFFBF8F3),
                                        unfocusedTextColor = Color(0xFFFBF8F3),
                                        focusedBorderColor = Color(0xFF8CD1B3),
                                        unfocusedBorderColor = Color(0xFF8CD1B3).copy(alpha = 0.5f),
                                        focusedLabelColor = Color(0xFF8CD1B3),
                                        unfocusedLabelColor = Color(0xFFB8D1C4)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("signup_phone_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Primary Health Goal",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFFBF8F3),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PrimaryHealthGoals.take(5).forEach { goal ->
                                    FilterChip(
                                        selected = selectedGoal == goal.title,
                                        onClick = { selectedGoal = goal.title },
                                        label = {
                                            Text(
                                                text = goal.title,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFE5A93C),
                                            selectedLabelColor = Color(0xFF0E291D),
                                            labelColor = Color(0xFFD5E6DD)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Current Symptoms (Select any that apply)",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFFBF8F3),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CoreRoutineSymptoms.take(6).forEach { symptom ->
                                    val isSelected = symptom in selectedSymptoms
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSymptoms = if (isSelected) {
                                                selectedSymptoms - symptom
                                            } else {
                                                selectedSymptoms + symptom
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = symptom,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF8CD1B3),
                                            selectedLabelColor = Color(0xFF0E291D),
                                            labelColor = Color(0xFFD5E6DD)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (authState.isSignUpMode) {
                                    val matchedConditionId = PrimaryHealthGoals
                                        .firstOrNull { it.title == selectedGoal }
                                        ?.recommendedConditionId
                                        ?: initialOnboardingData.primaryConditionId
                                    val prefilled = initialOnboardingData.copy(
                                        fullName = fullName.trim().ifBlank { "Practitioner" },
                                        age = selectedAge,
                                        ageGroup = deriveAgeGroupLabel(selectedAge),
                                        heightCm = parsedHeightCm,
                                        weightKg = parsedWeightKg,
                                        knownDiseasesOrMedications = knownDiseasesInput.trim().ifBlank { "None Reported" },
                                        emailId = emailInput.trim(),
                                        phoneNumber = phoneInput.trim(),
                                        primaryHealthGoal = selectedGoal,
                                        primaryConditionId = matchedConditionId,
                                        selectedSymptoms = selectedSymptoms
                                    )
                                    onSignUpLocal(fullName, prefilled)
                                } else {
                                    onSignInLocal(fullName)
                                }
                            },
                            enabled = !authState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("google_sign_in_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8CD1B3),
                                contentColor = Color(0xFF0E291D)
                            )
                        ) {
                            if (authState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color(0xFF0E291D)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Opening YogaSwasthya...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = if (authState.isSignUpMode) {
                                        Icons.Default.PersonAdd
                                    } else {
                                        Icons.Default.SelfImprovement
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (authState.isSignUpMode) {
                                        "Save Profile & Continue"
                                    } else {
                                        "Continue to YogaSwasthya"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BenefitRow("Personalizes your routine by Age, Body Metrics, Health Goal & Symptoms")
                            BenefitRow("18 clinical & preventive programs with Chair & Wall modifications")
                            BenefitRow("100% offline-ready daily symptom logs & clinician PDF/CSV exports")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF8CD1B3),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFD5E6DD)
        )
    }
}
