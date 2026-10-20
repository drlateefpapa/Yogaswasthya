package com.example.ui.auth

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.model.OnboardingProfileData
import com.example.ui.components.SacredLotusEmblem
import com.example.ui.screens.PrimaryHealthGoals
import com.example.ui.screens.deriveAgeGroupLabel
import com.example.ui.viewmodel.AuthUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun signOut(
    context: Context,
    credentialManager: CredentialManager,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    Firebase.auth.signOut()
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("Auth", "Failed to clear credential state", e)
        } finally {
            onSignOutComplete()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    authState: AuthUiState.Unauthenticated = AuthUiState.Unauthenticated(),
    initialOnboardingData: OnboardingProfileData = OnboardingProfileData(),
    onAttemptSilentGoogleSignIn: (Context, CredentialManager) -> Unit = { _, _ -> },
    onSignInWithGoogle: (Context, CredentialManager) -> Unit = { _, _ -> },
    onSignUpWithGoogle: (Context, CredentialManager, String, OnboardingProfileData) -> Unit = { _, _, _, _ -> },
    onToggleAuthMode: (isSignUp: Boolean) -> Unit = {},
    onAuthSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var isSignUpMode by rememberSaveable(authState.isSignUpMode) {
        mutableStateOf(authState.isSignUpMode)
    }
    var fullName by rememberSaveable { mutableStateOf("") }
    var ageInput by rememberSaveable { mutableStateOf(initialOnboardingData.age.toString()) }
    var selectedAge by rememberSaveable { mutableIntStateOf(initialOnboardingData.age.coerceIn(18, 95)) }
    var heightInput by rememberSaveable { mutableStateOf(initialOnboardingData.heightCm.toString()) }
    var weightInput by rememberSaveable { mutableStateOf(initialOnboardingData.weightKg.toString()) }
    var knownDiseaseInput by rememberSaveable { mutableStateOf(initialOnboardingData.knownDiseasesOrMedications) }
    var emailInput by rememberSaveable { mutableStateOf(initialOnboardingData.emailId) }
    var phoneInput by rememberSaveable { mutableStateOf(initialOnboardingData.phoneNumber) }
    var selectedGoalTitle by rememberSaveable { mutableStateOf(initialOnboardingData.primaryHealthGoal) }
    var selectedConditionId by rememberSaveable { mutableStateOf(initialOnboardingData.primaryConditionId) }

    LaunchedEffect(Unit) {
        onAttemptSilentGoogleSignIn(context, credentialManager)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF122C20),
                        Color(0xFF1D4632),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 560.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SacredLotusEmblem(
                modifier = Modifier.size(104.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0x33D4A359),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = Color(0xFFF3C878),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YOGA THERAPY & RECOVERY COMPANION",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF8E8C8),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = Color(0xFFFBF8F3),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Disease-oriented Asanas, screened Pranayama, Dhyana, Indian dietary guidance & clinician-ready recovery tracking.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD9EDE2),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sign-In vs Sign-Up Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        FilterChip(
                            selected = !isSignUpMode,
                            onClick = {
                                isSignUpMode = false
                                onToggleAuthMode(false)
                            },
                            label = {
                                Text(
                                    text = "Sign In",
                                    fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("auth_mode_sign_in_chip")
                        )
                        FilterChip(
                            selected = isSignUpMode,
                            onClick = {
                                isSignUpMode = true
                                onToggleAuthMode(true)
                            },
                            label = {
                                Text(
                                    text = "Create Account (Sign Up)",
                                    fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("auth_mode_sign_up_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSignUpMode) {
                        Text(
                            text = "New Practitioner Sign-Up & Onboarding",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set your name and initial focus below, then authenticate with Google to complete your 3-step symptom & routine onboarding.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Patient / Practitioner Name") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(2)
                                    ageInput = digits
                                    digits.toIntOrNull()?.let { parsed ->
                                        selectedAge = parsed.coerceIn(18, 95)
                                    }
                                },
                                label = { Text("Age (yrs)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_signup_age_input")
                            )

                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { raw ->
                                    heightInput = raw.filter { it.isDigit() }.take(3)
                                },
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_signup_height_input")
                            )

                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { raw ->
                                    weightInput = raw.filter { it.isDigit() }.take(3)
                                },
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_signup_weight_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = knownDiseaseInput,
                            onValueChange = { knownDiseaseInput = it.take(300) },
                            label = { Text("Known Disease or Medication") },
                            placeholder = { Text("e.g., Hypertension, Diabetes, Vitamin D3") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_signup_disease_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it.take(120) },
                                label = { Text("Mail ID") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_signup_email_input")
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { raw ->
                                    phoneInput = raw.filter { it.isDigit() || it in "+- ()" }.take(25)
                                },
                                label = { Text("Phone No.") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_signup_phone_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Primary Health Goal (customizable in onboarding):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PrimaryHealthGoals.take(4).forEach { goal ->
                                val selected = selectedGoalTitle == goal.title
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        selectedGoalTitle = goal.title
                                        selectedConditionId = goal.recommendedConditionId
                                    },
                                    label = {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("signup_goal_chip_${goal.id}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    } else {
                        Text(
                            text = "Welcome Back to YogaSwasthya",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sign in with your Google account to restore your personalized therapy plan, symptom logs, and clinician progress reports.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (authState.errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("auth_error_banner")
                        ) {
                            Text(
                                text = authState.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Primary Google Sign-In / Sign-Up Button
                    Button(
                        onClick = {
                            if (isSignUpMode) {
                                val prefilledOnboarding = initialOnboardingData.copy(
                                    fullName = fullName.trim().ifBlank { initialOnboardingData.fullName },
                                    age = selectedAge,
                                    ageGroup = deriveAgeGroupLabel(selectedAge),
                                    heightCm = heightInput.toIntOrNull()?.coerceIn(50, 260) ?: initialOnboardingData.heightCm,
                                    weightKg = weightInput.toIntOrNull()?.coerceIn(15, 300) ?: initialOnboardingData.weightKg,
                                    knownDiseasesOrMedications = knownDiseaseInput.trim().ifBlank { "None Reported" },
                                    emailId = emailInput.trim(),
                                    phoneNumber = phoneInput.trim(),
                                    primaryHealthGoal = selectedGoalTitle,
                                    primaryConditionId = selectedConditionId,
                                    isOnboardingCompleted = false
                                )
                                onSignUpWithGoogle(
                                    context,
                                    credentialManager,
                                    fullName,
                                    prefilledOnboarding
                                )
                            } else {
                                onSignInWithGoogle(context, credentialManager)
                            }
                            onAuthSuccess()
                        },
                        enabled = !authState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (authState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSignUpMode)
                                    "Sign up with Google & Start Onboarding"
                                else
                                    "Sign in with Google",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    val highlights = listOf(
                        "Personalizes your routine by Age, Primary Health Goal & Symptoms",
                        "18 clinical & preventive programs with Chair & Wall modifications",
                        "Cloud-synced daily symptom logs & clinician PDF reports"
                    )

                    highlights.forEach { point ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
