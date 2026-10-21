package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.catalog.DiseaseCatalog
import com.example.data.repository.YogaTherapyRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.screens.ConditionDetailScreen
import com.example.ui.screens.DietaryAdviceScreen
import com.example.ui.screens.GuidedPracticeSessionModal
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PracticeLibraryScreen
import com.example.ui.screens.ProfileAndClinicianScreen
import com.example.ui.screens.RecoveryTrackerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.YogaTherapyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val viewModel: YogaTherapyViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                YogaTherapyViewModel(
                    repository = YogaTherapyRepository(app),
                    currentUserId = YogaTherapyRepository.DEFAULT_USER_ID,
                    defaultDisplayName = "Aarav Sharma"
                )
            }
        }
    )

    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val onboardingData by viewModel.onboardingData.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthUiState.Initializing -> {
            AuthScreen(
                authState = AuthUiState.Unauthenticated(isLoading = true),
                initialOnboardingData = onboardingData,
                onSignInLocal = { displayName ->
                    viewModel.signInLocalProfile(displayName)
                },
                onSignUpLocal = { displayName, prefilledOnboarding ->
                    viewModel.signUpLocalProfile(displayName, prefilledOnboarding)
                },
                onToggleAuthMode = { isSignUp ->
                    viewModel.toggleAuthMode(isSignUp)
                }
            )
        }

        is AuthUiState.Unauthenticated -> {
            AuthScreen(
                authState = state,
                initialOnboardingData = onboardingData,
                onSignInLocal = { displayName ->
                    viewModel.signInLocalProfile(displayName)
                },
                onSignUpLocal = { displayName, prefilledOnboarding ->
                    viewModel.signUpLocalProfile(displayName, prefilledOnboarding)
                },
                onToggleAuthMode = { isSignUp ->
                    viewModel.toggleAuthMode(isSignUp)
                }
            )
        }

        is AuthUiState.Authenticated -> {
            YogaSwasthyaMainScreen(
                currentUserId = state.uid,
                userDisplayName = state.displayName,
                sharedViewModel = viewModel
            )
        }
    }
}

/**
 * Primary 5-tab navigation destinations for YogaSwasthya:
 * Home, Conditions, Practice, Progress, and Profile.
 */
enum class MainDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    CONDITIONS(
        route = "conditions",
        title = "Conditions",
        selectedIcon = Icons.Filled.MedicalInformation,
        unselectedIcon = Icons.Outlined.MedicalInformation
    ),
    PRACTICE(
        route = "practice",
        title = "Practice",
        selectedIcon = Icons.Filled.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement
    ),
    PROGRESS(
        route = "progress",
        title = "Progress",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights
    ),
    PROFILE(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaSwasthyaMainScreen(
    currentUserId: String,
    userDisplayName: String,
    sharedViewModel: YogaTherapyViewModel? = null
) {
    val viewModel: YogaTherapyViewModel = sharedViewModel ?: viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                YogaTherapyViewModel(
                    repository = YogaTherapyRepository(app),
                    currentUserId = currentUserId,
                    defaultDisplayName = userDisplayName
                )
            }
        }
    )

    val selectedConditionId by viewModel.selectedConditionId.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val onboardingData by viewModel.onboardingData.collectAsStateWithLifecycle()
    val recoveryLogsState by viewModel.recoveryLogsState.collectAsStateWithLifecycle()
    val practiceSessionsState by viewModel.practiceSessionsState.collectAsStateWithLifecycle()
    val activeGuidedPractice by viewModel.activeGuidedPractice.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val selectedCondition = remember(selectedConditionId) {
        DiseaseCatalog.getConditionById(selectedConditionId)
    }
    val patientProfile = (profileState as? UiState.Success)?.data
    val recoveryLogs = (recoveryLogsState as? UiState.Success)?.data ?: emptyList()
    val practiceSessions = (practiceSessionsState as? UiState.Success)?.data ?: emptyList()

    var currentDestination by rememberSaveable { mutableStateOf(MainDestination.HOME) }
    var showDietSubScreen by rememberSaveable { mutableStateOf(false) }
    var showSettingsExportModal by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // BackHandler for Diet sub-screen or secondary tabs returning to Home
    if (showDietSubScreen) {
        BackHandler {
            showDietSubScreen = false
        }
    } else if (currentDestination != MainDestination.HOME) {
        BackHandler {
            currentDestination = MainDestination.HOME
        }
    }

    LaunchedEffect(bannerMessage) {
        bannerMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBannerMessage()
        }
    }

    if (!onboardingData.isOnboardingCompleted) {
        OnboardingScreen(
            initialData = onboardingData,
            onCompleteOnboarding = { updatedData ->
                viewModel.completeOnboarding(updatedData)
                currentDestination = MainDestination.HOME
            },
            onSkipOnboarding = {
                viewModel.skipOnboarding()
            }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (showDietSubScreen) {
                            IconButton(
                                onClick = { showDietSubScreen = false },
                                modifier = Modifier.testTag("back_from_diet_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (showDietSubScreen) "Diet & Daily Routine" else "YogaSwasthya",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Focus: ${selectedCondition.shortName} • $userDisplayName",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDietSubScreen = !showDietSubScreen },
                            modifier = Modifier.testTag("top_bar_diet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = "Indian Diet & Routine Guide",
                                tint = if (showDietSubScreen)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "${recoveryLogs.size} Logs",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        IconButton(
                            onClick = { showSettingsExportModal = true },
                            modifier = Modifier.testTag("top_bar_settings_export_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings, Download & Export",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.signOutUser()
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Switch Profile"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (!isExpandedScreen) {
                    YogaSwasthyaBottomNavigationBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            showDietSubScreen = false
                            currentDestination = destination
                        }
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen) {
                    NavigationRail(
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag("main_navigation_rail"),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        MainDestination.entries.forEach { dest ->
                            val isSelected = !showDietSubScreen && currentDestination == dest
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    showDietSubScreen = false
                                    currentDestination = dest
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.title
                                    )
                                },
                                label = { Text(dest.title) },
                                modifier = Modifier.testTag("nav_rail_${dest.route}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (showDietSubScreen) {
                        DietaryAdviceScreen(
                            selectedCondition = selectedCondition,
                            onSelectCondition = { viewModel.selectCondition(it) }
                        )
                    } else {
                        Crossfade(
                            targetState = currentDestination,
                            label = "YogaSwasthyaMainScreenTransition"
                        ) { destination ->
                            when (destination) {
                                MainDestination.HOME -> {
                                    HomeScreen(
                                        selectedCondition = selectedCondition,
                                        patientProfile = patientProfile,
                                        onboardingData = onboardingData,
                                        recoveryLogs = recoveryLogs,
                                        practiceSessions = practiceSessions,
                                        onStartPractice = { viewModel.startGuidedPractice(it) },
                                        onNavigateToConditions = {
                                            showDietSubScreen = false
                                            currentDestination = MainDestination.CONDITIONS
                                        },
                                        onNavigateToPractice = {
                                            showDietSubScreen = false
                                            currentDestination = MainDestination.PRACTICE
                                        },
                                        onNavigateToDiet = {
                                            showDietSubScreen = true
                                        },
                                        onNavigateToProgress = {
                                            showDietSubScreen = false
                                            currentDestination = MainDestination.PROGRESS
                                        },
                                        onNavigateToProfileAndClinician = {
                                            showDietSubScreen = false
                                            currentDestination = MainDestination.PROFILE
                                        },
                                        onSelectCondition = { viewModel.selectCondition(it) },
                                        onReopenOnboarding = { viewModel.reopenOnboarding() }
                                    )
                                }

                                MainDestination.CONDITIONS -> {
                                    ConditionDetailScreen(
                                        condition = selectedCondition,
                                        patientProfile = patientProfile,
                                        availableConditions = DiseaseCatalog.conditions,
                                        onSelectCondition = { viewModel.selectCondition(it) },
                                        onStartPractice = { viewModel.startGuidedPractice(it) },
                                        onSetPrimaryCondition = { condition ->
                                            viewModel.setPrimaryConditionAndBaseline(
                                                conditionId = condition.id,
                                                baselinePain = patientProfile?.baselinePainScore
                                                    ?: onboardingData.baselinePainScore,
                                                baselineMobility = patientProfile?.baselineMobilityScore
                                                    ?: onboardingData.baselineMobilityScore,
                                                baselineStress = patientProfile?.baselineStressScore
                                                    ?: onboardingData.baselineStressScore,
                                                dietaryPreference = patientProfile?.dietaryPreference
                                                    ?: "${onboardingData.dietaryType} • ${onboardingData.regionalFoodStyle}".take(50),
                                                dailyGoalMinutes = patientProfile?.dailyGoalMinutes
                                                    ?: onboardingData.availablePracticeMinutes
                                            )
                                        },
                                        onNavigateToPracticeLibrary = {
                                            showDietSubScreen = false
                                            currentDestination = MainDestination.PRACTICE
                                        },
                                        onNavigateToDietProtocol = {
                                            showDietSubScreen = true
                                        }
                                    )
                                }

                                MainDestination.PRACTICE -> {
                                    PracticeLibraryScreen(
                                        selectedCondition = selectedCondition,
                                        availableConditions = DiseaseCatalog.conditions,
                                        onSelectCondition = { viewModel.selectCondition(it) },
                                        onStartPractice = { viewModel.startGuidedPractice(it) },
                                        onStartMeditationTrack = { track, mins ->
                                            viewModel.startMeditationTrack(track, mins)
                                        },
                                        onNavigateToDiet = { showDietSubScreen = true }
                                    )
                                }

                                MainDestination.PROGRESS -> {
                                    RecoveryTrackerScreen(
                                        selectedCondition = selectedCondition,
                                        patientProfile = patientProfile,
                                        recoveryLogs = recoveryLogs,
                                        practiceSessions = practiceSessions,
                                        onSubmitDailyLog = { condId, pain, stiff, stress, mob, sleep, asana, prana, dhyana, practices, dietScore, water, notes ->
                                            viewModel.submitDailyRecoveryLog(
                                                conditionId = condId,
                                                painLevel = pain,
                                                stiffnessLevel = stiff,
                                                stressLevel = stress,
                                                mobilityScore = mob,
                                                sleepQuality = sleep,
                                                asanaMinutes = asana,
                                                pranayamaMinutes = prana,
                                                dhyanaMinutes = dhyana,
                                                completedPractices = practices,
                                                dietAdherenceScore = dietScore,
                                                waterIntakeGlasses = water,
                                                notes = notes
                                            )
                                        },
                                        onDeleteLog = { viewModel.deleteRecoveryLog(it) },
                                        onExportStatusMessage = { viewModel.showStatusMessage(it) },
                                        onExportWeeklyPdf = { shareWithClinician, onExported ->
                                            viewModel.exportWeeklyRecoveryPdf(
                                                context = context,
                                                shareWithClinician = shareWithClinician,
                                                onExported = onExported
                                            )
                                        }
                                    )
                                }

                                MainDestination.PROFILE -> {
                                    ProfileAndClinicianScreen(
                                        selectedCondition = selectedCondition,
                                        patientProfile = patientProfile,
                                        onboardingData = onboardingData,
                                        recoveryLogs = recoveryLogs,
                                        onUpdateOnboardingData = { viewModel.updateOnboardingData(it) },
                                        onSelectCondition = { viewModel.selectCondition(it) },
                                        onDeleteAllLogs = { viewModel.deleteAllRecoveryLogs() },
                                        onSignOut = {
                                            viewModel.signOutUser()
                                        },
                                        onReopenOnboarding = { viewModel.reopenOnboarding() },
                                        onExportWeeklyPdf = { shareWithClinician ->
                                            viewModel.exportWeeklyRecoveryPdf(
                                                context = context,
                                                shareWithClinician = shareWithClinician
                                            )
                                        },
                                        onExportPatientCsv = {
                                            viewModel.exportPatientDataCsv(
                                                context = context,
                                                shareFile = true
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSettingsExportModal) {
        AlertDialog(
            onDismissRequest = { showSettingsExportModal = false },
            title = {
                Text(
                    text = "Settings, Download & Export Options",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "1. Export & Download Reports (Inside App):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Button(
                        onClick = {
                            showSettingsExportModal = false
                            viewModel.exportWeeklyRecoveryPdf(
                                context = context,
                                shareWithClinician = false
                            )
                        },
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .testTag("modal_download_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download Weekly PDF to Downloads")
                    }
                    OutlinedButton(
                        onClick = {
                            showSettingsExportModal = false
                            viewModel.exportWeeklyRecoveryPdf(
                                context = context,
                                shareWithClinician = true
                            )
                        },
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .testTag("modal_share_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share PDF with Clinician / WhatsApp")
                    }
                    OutlinedButton(
                        onClick = {
                            showSettingsExportModal = false
                            viewModel.exportPatientDataCsv(
                                context = context,
                                shareFile = true
                            )
                        },
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .testTag("modal_export_csv_button")
                    ) {
                        Text("Export Complete Patient Record (CSV)")
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "2. Local On-Device Mode Active:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "• All patient profile details, daily recovery logs, and PDF/CSV exports are stored locally on your Android device with zero Firebase or Google Sign-In dependencies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSettingsExportModal = false
                        showDietSubScreen = false
                        currentDestination = MainDestination.PROFILE
                    }
                ) {
                    Text("Open Full Profile & Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsExportModal = false }) {
                    Text("Close")
                }
            }
        )
    }

    activeGuidedPractice?.let { practice ->
        GuidedPracticeSessionModal(
            practice = practice,
            conditionName = selectedCondition.shortName,
            onDismiss = { viewModel.dismissGuidedPractice() },
            onCompleteSession = { durationMinutes, reliefRating ->
                viewModel.completeGuidedPracticeSession(
                    practice = practice,
                    actualDurationMinutes = durationMinutes,
                    reliefRating = reliefRating
                )
            }
        )
    }
}

@Composable
fun YogaSwasthyaBottomNavigationBar(
    currentDestination: MainDestination,
    onNavigate: (MainDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("main_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        MainDestination.entries.forEach { destination ->
            val isSelected = currentDestination == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_tab_${destination.route}")
            )
        }
    }
}
