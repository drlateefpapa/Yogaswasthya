package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.MeditationTrack
import com.example.data.model.OnboardingProfileData
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeCategory
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.example.data.model.TherapeuticPractice
import com.example.data.repository.YogaTherapyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AuthUiState {
    data object Initializing : AuthUiState
    data class Unauthenticated(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSignUpMode: Boolean = false
    ) : AuthUiState
    data class Authenticated(
        val uid: String,
        val displayName: String,
        val email: String? = null,
        val isNewUser: Boolean = false
    ) : AuthUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class YogaTherapyViewModel(
    private val repository: YogaTherapyRepository,
    currentUserId: String = "local_patient",
    defaultDisplayName: String = "Aarav Sharma"
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(
        if (currentUserId.isNotBlank()) {
            AuthUiState.Authenticated(
                uid = currentUserId,
                displayName = defaultDisplayName.ifBlank { "Practitioner" }
            )
        } else {
            AuthUiState.Unauthenticated()
        }
    )
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val activeUserIdFlow: StateFlow<String?> = _authUiState
        .map { state -> (state as? AuthUiState.Authenticated)?.uid }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = (authUiState.value as? AuthUiState.Authenticated)?.uid
        )

    private val _selectedConditionId = MutableStateFlow("cervical_lumbar_spondylitis")
    val selectedConditionId: StateFlow<String> = _selectedConditionId.asStateFlow()

    private val _activeGuidedPractice = MutableStateFlow<TherapeuticPractice?>(null)
    val activeGuidedPractice: StateFlow<TherapeuticPractice?> = _activeGuidedPractice.asStateFlow()

    private val _onboardingData = MutableStateFlow(OnboardingProfileData())
    val onboardingData: StateFlow<OnboardingProfileData> = _onboardingData.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val profileState: StateFlow<UiState<PatientProfile?>> = activeUserIdFlow
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(UiState.Success(null))
            } else {
                repository.observePatientProfile(uid)
                    .map<PatientProfile?, UiState<PatientProfile?>> { profile ->
                        if (profile != null) {
                            if (_selectedConditionId.value == "cervical_lumbar_spondylitis") {
                                _selectedConditionId.value = profile.primaryConditionId
                            }
                            syncLocalOnboardingFromProfile(profile)
                        }
                        UiState.Success(profile)
                    }
                    .catch { error ->
                        Log.w(TAG, "Error observing patient profile", error)
                        emit(UiState.Error(error.localizedMessage ?: "Failed to load patient profile"))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val recoveryLogsState: StateFlow<UiState<List<RecoveryLog>>> = activeUserIdFlow
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observeRecoveryLogs(uid)
                    .map<List<RecoveryLog>, UiState<List<RecoveryLog>>> { UiState.Success(it) }
                    .catch { error ->
                        Log.w(TAG, "Error observing recovery logs", error)
                        emit(UiState.Error(error.localizedMessage ?: "Failed to load recovery logs"))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val practiceSessionsState: StateFlow<UiState<List<PracticeSession>>> = activeUserIdFlow
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observePracticeSessions(uid)
                    .map<List<PracticeSession>, UiState<List<PracticeSession>>> { UiState.Success(it) }
                    .catch { error ->
                        Log.w(TAG, "Error observing practice sessions", error)
                        emit(UiState.Error(error.localizedMessage ?: "Failed to load practice sessions"))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    init {
        if (currentUserId.isNotBlank() && currentUserId != "guest_local") {
            ensureProfileOrTriggerOnboarding(
                uid = currentUserId,
                displayName = defaultDisplayName,
                isNewSignUp = false
            )
        }
    }

    private fun syncLocalOnboardingFromProfile(profile: PatientProfile) {
        val current = _onboardingData.value
        val authEmail = (authUiState.value as? AuthUiState.Authenticated)?.email.orEmpty()
        val resolvedAge = profile.age.coerceIn(18, 95)
        val resolvedAgeGroup = when {
            resolvedAge < 35 -> "18–34 yrs"
            resolvedAge <= 50 -> "35–50 yrs"
            resolvedAge <= 64 -> "51–65 yrs"
            else -> "65+ Senior"
        }
        _onboardingData.value = current.copy(
            fullName = profile.displayName.ifBlank { current.fullName },
            age = resolvedAge,
            ageGroup = resolvedAgeGroup,
            heightCm = profile.heightCm.coerceIn(50, 260),
            weightKg = profile.weightKg.coerceIn(15, 300),
            knownDiseasesOrMedications = profile.knownDiseasesOrMedications.ifBlank { current.knownDiseasesOrMedications },
            emailId = profile.emailId.ifBlank { current.emailId.ifBlank { authEmail } },
            phoneNumber = profile.phoneNumber.ifBlank { current.phoneNumber },
            primaryConditionId = profile.primaryConditionId,
            baselinePainScore = profile.baselinePainScore,
            baselineMobilityScore = profile.baselineMobilityScore,
            baselineStressScore = profile.baselineStressScore,
            availablePracticeMinutes = profile.dailyGoalMinutes
        )
    }

    private fun ensureProfileOrTriggerOnboarding(
        uid: String,
        displayName: String,
        isNewSignUp: Boolean
    ) {
        viewModelScope.launch {
            val result = repository.getPatientProfile(uid)
            val existing = result.getOrNull()
            val authEmail = (authUiState.value as? AuthUiState.Authenticated)?.email.orEmpty()
            if (result.isSuccess && existing == null) {
                val currentOnboarding = _onboardingData.value.copy(
                    fullName = displayName.ifBlank { _onboardingData.value.fullName },
                    emailId = _onboardingData.value.emailId.ifBlank { authEmail },
                    isOnboardingCompleted = false
                )
                _onboardingData.value = currentOnboarding
                val initialProfile = PatientProfile(
                    id = uid,
                    userId = uid,
                    displayName = currentOnboarding.fullName.ifBlank { displayName.ifBlank { "Patient" } },
                    age = currentOnboarding.age,
                    heightCm = currentOnboarding.heightCm,
                    weightKg = currentOnboarding.weightKg,
                    knownDiseasesOrMedications = currentOnboarding.knownDiseasesOrMedications,
                    emailId = currentOnboarding.emailId,
                    phoneNumber = currentOnboarding.phoneNumber,
                    primaryConditionId = currentOnboarding.primaryConditionId,
                    activeConditionIds = listOf(
                        currentOnboarding.primaryConditionId,
                        "osteoarthritis",
                        "frozen_shoulder",
                        "anxiety",
                        "migraine"
                    ).distinct(),
                    baselinePainScore = currentOnboarding.baselinePainScore,
                    baselineMobilityScore = currentOnboarding.baselineMobilityScore,
                    baselineStressScore = currentOnboarding.baselineStressScore,
                    dietaryPreference = "${currentOnboarding.dietaryType} • ${currentOnboarding.regionalFoodStyle}".take(50),
                    dailyGoalMinutes = currentOnboarding.availablePracticeMinutes
                )
                repository.savePatientProfile(initialProfile)
            } else if (existing != null) {
                _selectedConditionId.value = existing.primaryConditionId
                syncLocalOnboardingFromProfile(existing)
                _onboardingData.value = _onboardingData.value.copy(
                    isOnboardingCompleted = !isNewSignUp
                )
            }
        }
    }

    fun toggleAuthMode(isSignUp: Boolean) {
        val current = _authUiState.value
        if (current is AuthUiState.Unauthenticated) {
            _authUiState.value = current.copy(
                isSignUpMode = isSignUp,
                errorMessage = null
            )
        }
    }

    fun clearAuthError() {
        val current = _authUiState.value
        if (current is AuthUiState.Unauthenticated) {
            _authUiState.value = current.copy(errorMessage = null)
        }
    }

    fun signInLocalProfile(displayName: String = "") {
        val resolvedName = displayName.trim().ifBlank {
            _onboardingData.value.fullName.ifBlank { "Aarav Sharma" }
        }
        val uid = YogaTherapyRepository.DEFAULT_USER_ID
        _authUiState.value = AuthUiState.Authenticated(
            uid = uid,
            displayName = resolvedName,
            email = _onboardingData.value.emailId.ifBlank { null },
            isNewUser = false
        )
        ensureProfileOrTriggerOnboarding(
            uid = uid,
            displayName = resolvedName,
            isNewSignUp = false
        )
    }

    fun signUpLocalProfile(
        customDisplayName: String = "",
        initialOnboardingData: OnboardingProfileData? = null
    ) {
        val resolvedName = customDisplayName.trim().ifBlank {
            initialOnboardingData?.fullName?.ifBlank { "Aarav Sharma" } ?: "Aarav Sharma"
        }
        val uid = YogaTherapyRepository.DEFAULT_USER_ID
        if (initialOnboardingData != null) {
            _onboardingData.value = initialOnboardingData.copy(
                fullName = resolvedName,
                isOnboardingCompleted = false
            )
            _selectedConditionId.value = initialOnboardingData.primaryConditionId
        }
        _authUiState.value = AuthUiState.Authenticated(
            uid = uid,
            displayName = resolvedName,
            email = initialOnboardingData?.emailId?.ifBlank { null },
            isNewUser = true
        )
        ensureProfileOrTriggerOnboarding(
            uid = uid,
            displayName = resolvedName,
            isNewSignUp = true
        )
    }

    fun signOutUser() {
        _activeGuidedPractice.value = null
        _authUiState.value = AuthUiState.Unauthenticated()
    }

    private fun currentUserIdOrFallback(): String {
        return (authUiState.value as? AuthUiState.Authenticated)?.uid
            ?.takeIf { it.isNotBlank() }
            ?: YogaTherapyRepository.DEFAULT_USER_ID
    }

    private fun currentDisplayNameOrFallback(): String {
        return (authUiState.value as? AuthUiState.Authenticated)?.displayName ?: "Practitioner"
    }

    fun selectCondition(conditionId: String) {
        _selectedConditionId.value = conditionId
    }

    fun getSelectedCondition(): DiseaseCondition {
        return DiseaseCatalog.getConditionById(_selectedConditionId.value)
    }

    fun updateOnboardingData(updated: OnboardingProfileData) {
        _onboardingData.value = updated
        val currentAuth = _authUiState.value
        if (currentAuth is AuthUiState.Authenticated && updated.fullName.isNotBlank()) {
            _authUiState.value = currentAuth.copy(displayName = updated.fullName)
        }
        setPrimaryConditionAndBaseline(
            conditionId = updated.primaryConditionId,
            baselinePain = updated.baselinePainScore,
            baselineMobility = updated.baselineMobilityScore,
            baselineStress = updated.baselineStressScore,
            dietaryPreference = "${updated.dietaryType} • ${updated.regionalFoodStyle}".take(50),
            dailyGoalMinutes = updated.availablePracticeMinutes,
            customMessage = "Patient profile, personal details & medical settings saved"
        )
    }

    fun completeOnboarding(updated: OnboardingProfileData) {
        _onboardingData.value = updated.copy(isOnboardingCompleted = true)
        _selectedConditionId.value = updated.primaryConditionId
        val currentAuth = _authUiState.value
        if (currentAuth is AuthUiState.Authenticated) {
            _authUiState.value = currentAuth.copy(
                displayName = updated.fullName.ifBlank { currentAuth.displayName },
                isNewUser = false
            )
        }
        setPrimaryConditionAndBaseline(
            conditionId = updated.primaryConditionId,
            baselinePain = updated.baselinePainScore,
            baselineMobility = updated.baselineMobilityScore,
            baselineStress = updated.baselineStressScore,
            dietaryPreference = "${updated.dietaryType} • ${updated.regionalFoodStyle}".take(50),
            dailyGoalMinutes = updated.availablePracticeMinutes
        )
    }

    fun skipOnboarding() {
        _onboardingData.value = _onboardingData.value.copy(isOnboardingCompleted = true)
        val currentAuth = _authUiState.value
        if (currentAuth is AuthUiState.Authenticated && currentAuth.isNewUser) {
            _authUiState.value = currentAuth.copy(isNewUser = false)
        }
    }

    fun reopenOnboarding() {
        _onboardingData.value = _onboardingData.value.copy(isOnboardingCompleted = false)
    }

    fun startGuidedPractice(practice: TherapeuticPractice) {
        _activeGuidedPractice.value = practice
    }

    fun startMeditationTrack(track: MeditationTrack, durationMinutes: Int) {
        val practice = TherapeuticPractice(
            id = track.id,
            title = track.title,
            sanskritName = "${track.subtitle} • ${track.ambientSoundLabel}",
            category = PracticeCategory.DHYANA,
            durationMinutes = durationMinutes,
            holdAndReps = "$durationMinutes min guided relaxation (${track.ambientSoundLabel})",
            difficulty = "All Levels / Restorative",
            anatomicalFocus = track.targetPurpose,
            breathingCue = "Inhale softly for 4 seconds, exhale effortlessly for 6 seconds without breath retention.",
            stepByStepInstructions = track.writtenGuideSteps,
            therapeuticBenefits = listOf(
                track.targetPurpose,
                "Down-regulates sympathetic stress arousal and supports pain coping"
            ),
            contraindications = listOf(
                "Practice seated or with eyes softly open if lying down or closing eyes causes discomfort."
            )
        )
        _activeGuidedPractice.value = practice
    }

    fun dismissGuidedPractice() {
        _activeGuidedPractice.value = null
    }

    fun clearBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun showStatusMessage(message: String) {
        _statusBannerMessage.value = message
    }

    fun setPrimaryConditionAndBaseline(
        conditionId: String,
        baselinePain: Int,
        baselineMobility: Int,
        baselineStress: Int,
        dietaryPreference: String,
        dailyGoalMinutes: Int,
        customMessage: String? = null
    ) {
        val uid = currentUserIdOrFallback()
        val defaultName = currentDisplayNameOrFallback()
        val currentOnboarding = _onboardingData.value

        viewModelScope.launch {
            _isSaving.value = true
            val currentProfile = (profileState.value as? UiState.Success)?.data
            val updatedActive = ((currentProfile?.activeConditionIds ?: emptyList()) + conditionId).distinct().take(10)
            val resolvedName = currentOnboarding.fullName.ifBlank {
                currentProfile?.displayName?.ifBlank { defaultName } ?: defaultName
            }
            val profile = PatientProfile(
                id = uid,
                userId = uid,
                displayName = resolvedName,
                age = currentOnboarding.age.coerceIn(1, 120),
                heightCm = currentOnboarding.heightCm.coerceIn(50, 260),
                weightKg = currentOnboarding.weightKg.coerceIn(15, 300),
                knownDiseasesOrMedications = currentOnboarding.knownDiseasesOrMedications,
                emailId = currentOnboarding.emailId.ifBlank { currentProfile?.emailId.orEmpty() },
                phoneNumber = currentOnboarding.phoneNumber.ifBlank { currentProfile?.phoneNumber.orEmpty() },
                primaryConditionId = conditionId,
                activeConditionIds = updatedActive,
                baselinePainScore = baselinePain,
                baselineMobilityScore = baselineMobility,
                baselineStressScore = baselineStress,
                dietaryPreference = dietaryPreference,
                dailyGoalMinutes = dailyGoalMinutes
            )
            val res = repository.savePatientProfile(profile)
            _isSaving.value = false
            if (res.isSuccess) {
                _selectedConditionId.value = conditionId
                val condName = DiseaseCatalog.getConditionById(conditionId).shortName
                _statusBannerMessage.value = customMessage ?: "Primary therapy protocol updated to $condName"
            } else {
                _statusBannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not save profile"
            }
        }
    }

    fun completeGuidedPracticeSession(
        practice: TherapeuticPractice,
        actualDurationMinutes: Int,
        reliefRating: Int
    ) {
        val uid = currentUserIdOrFallback()

        viewModelScope.launch {
            _isSaving.value = true
            val session = PracticeSession(
                userId = uid,
                conditionId = _selectedConditionId.value,
                practiceId = practice.id,
                practiceTitle = practice.title,
                category = practice.category.label,
                durationMinutes = actualDurationMinutes.coerceIn(1, 180),
                reliefRating = reliefRating.coerceIn(1, 5)
            )
            val result = repository.logPracticeSession(session)
            _isSaving.value = false
            _activeGuidedPractice.value = null
            if (result.isSuccess) {
                _statusBannerMessage.value = "Logged ${practice.title} (${actualDurationMinutes}m • Relief ${reliefRating}/5)"
            } else {
                _statusBannerMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to save session"
            }
        }
    }

    fun submitDailyRecoveryLog(
        conditionId: String,
        painLevel: Int,
        stiffnessLevel: Int,
        stressLevel: Int,
        mobilityScore: Int,
        sleepQuality: Int,
        asanaMinutes: Int,
        pranayamaMinutes: Int,
        dhyanaMinutes: Int,
        completedPractices: List<String>,
        dietAdherenceScore: Int,
        waterIntakeGlasses: Int,
        notes: String
    ) {
        val uid = currentUserIdOrFallback()

        viewModelScope.launch {
            _isSaving.value = true
            val todayLabel = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val log = RecoveryLog(
                userId = uid,
                conditionId = conditionId,
                dateLabel = todayLabel,
                painLevel = painLevel,
                stiffnessLevel = stiffnessLevel,
                stressLevel = stressLevel,
                mobilityScore = mobilityScore,
                sleepQuality = sleepQuality,
                asanaMinutes = asanaMinutes,
                pranayamaMinutes = pranayamaMinutes,
                dhyanaMinutes = dhyanaMinutes,
                completedPractices = completedPractices,
                dietAdherenceScore = dietAdherenceScore,
                waterIntakeGlasses = waterIntakeGlasses,
                notes = notes.trim().ifBlank { null }
            )
            val result = repository.addRecoveryLog(log)
            _isSaving.value = false
            if (result.isSuccess) {
                _statusBannerMessage.value = "Daily recovery log saved for ${DiseaseCatalog.getConditionById(conditionId).shortName}"
            } else {
                _statusBannerMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to save recovery log"
            }
        }
    }

    fun deleteRecoveryLog(logId: String) {
        val uid = currentUserIdOrFallback()

        viewModelScope.launch {
            val result = repository.deleteRecoveryLog(uid, logId)
            if (result.isSuccess) {
                _statusBannerMessage.value = "Recovery entry removed"
            } else {
                _statusBannerMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Could not delete entry"
            }
        }
    }

    fun deleteAllRecoveryLogs() {
        val uid = currentUserIdOrFallback()

        viewModelScope.launch {
            val logs = (recoveryLogsState.value as? UiState.Success)?.data ?: emptyList()
            logs.forEach { log ->
                if (log.id.isNotBlank()) {
                    repository.deleteRecoveryLog(uid, log.id)
                }
            }
            _statusBannerMessage.value = "All personal recovery check-in logs cleared"
        }
    }

    fun exportWeeklyRecoveryPdf(
        context: Context,
        shareWithClinician: Boolean = false,
        onExported: (com.example.util.PdfReportGenerator.GeneratedReportSummary) -> Unit = {}
    ) {
        val uid = currentUserIdOrFallback()
        val currentProfile = (profileState.value as? UiState.Success)?.data
        val currentLogs = (recoveryLogsState.value as? UiState.Success)?.data ?: emptyList()
        val currentSessions = (practiceSessionsState.value as? UiState.Success)?.data ?: emptyList()
        val currentConditionId = _selectedConditionId.value
        val currentOnboarding = _onboardingData.value

        viewModelScope.launch {
            _isSaving.value = true
            val result = repository.exportWeeklyRecoveryProgressToPdf(
                context = context,
                userId = uid,
                conditionId = currentConditionId,
                fallbackProfile = currentProfile,
                fallbackLogs = currentLogs,
                fallbackSessions = currentSessions,
                onboardingData = currentOnboarding
            )
            _isSaving.value = false

            result.onSuccess { generatedSummary ->
                onExported(generatedSummary)
                if (shareWithClinician) {
                    com.example.util.PdfReportGenerator.sharePdfReport(
                        context = context,
                        summary = generatedSummary,
                        profile = currentProfile,
                        conditionId = currentConditionId,
                        logs = currentLogs,
                        sessions = currentSessions,
                        onboardingData = currentOnboarding
                    )
                    _statusBannerMessage.value = "Weekly PDF Recovery Report exported & ready to share with clinician"
                } else {
                    _statusBannerMessage.value = "Weekly PDF Recovery Report saved to Downloads: YogaSwasthya_Recovery_Report.pdf"
                }
            }.onFailure { err ->
                _statusBannerMessage.value = err.localizedMessage ?: "Failed to export weekly PDF report"
            }
        }
    }

    fun exportPatientDataCsv(context: Context, shareFile: Boolean = true) {
        val currentProfile = (profileState.value as? UiState.Success)?.data
        val currentLogs = (recoveryLogsState.value as? UiState.Success)?.data ?: emptyList()
        val currentSessions = (practiceSessionsState.value as? UiState.Success)?.data ?: emptyList()
        val currentConditionId = _selectedConditionId.value
        val currentOnboarding = _onboardingData.value

        val res = com.example.util.PdfReportGenerator.exportPatientDataCsv(
            context = context,
            profile = currentProfile,
            conditionId = currentConditionId,
            logs = currentLogs,
            sessions = currentSessions,
            onboardingData = currentOnboarding,
            shareFile = shareFile
        )
        res.onSuccess {
            _statusBannerMessage.value = "Exported patient & recovery data to Downloads: YogaSwasthya_Patient_Export.csv"
        }.onFailure { err ->
            _statusBannerMessage.value = err.localizedMessage ?: "Failed to export CSV data"
        }
    }

    private companion object {
        const val TAG = "YogaSwasthyaVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
