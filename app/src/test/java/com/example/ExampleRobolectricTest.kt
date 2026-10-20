package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.catalog.DiseaseCatalog
import com.example.data.catalog.MeditationCatalog
import com.example.data.model.OnboardingProfileData
import com.example.data.repository.YogaTherapyRepository
import com.example.ui.screens.CoreRoutineSymptoms
import com.example.ui.screens.PrimaryHealthGoals
import com.example.ui.screens.deriveAgeGroupLabel
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.YogaTherapyViewModel
import com.example.util.PdfReportGenerator
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `validate YogaSwasthya ViewModel auth states, onboarding integration, 5-tab navigation, and 18 clinical programs`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val appName = context.getString(R.string.app_name)
        assertEquals("YogaSwasthya", appName)

        // Verify the 5 primary bottom navigation tabs: Home, Conditions, Practice, Progress, Profile
        val navTitles = MainDestination.entries.map { it.title }
        assertEquals(
            listOf("Home", "Conditions", "Practice", "Progress", "Profile"),
            navTitles
        )

        // Verify ViewModel unauthenticated sign-in / sign-up mode switching and onboarding flow state
        val db = FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
        val viewModel = YogaTherapyViewModel(
            repository = YogaTherapyRepository(db),
            currentUserId = ""
        )
        assertTrue(viewModel.authUiState.value is AuthUiState.Unauthenticated)
        assertFalse((viewModel.authUiState.value as AuthUiState.Unauthenticated).isSignUpMode)

        viewModel.toggleAuthMode(isSignUp = true)
        assertTrue((viewModel.authUiState.value as AuthUiState.Unauthenticated).isSignUpMode)

        // Verify initial onboarding data model and age/goal/symptom options
        val initialOnboarding = viewModel.onboardingData.value
        assertFalse(initialOnboarding.isOnboardingCompleted)
        assertEquals("35–50 yrs", deriveAgeGroupLabel(42))
        assertEquals("65+ Senior", deriveAgeGroupLabel(68))
        assertTrue(PrimaryHealthGoals.size >= 7)
        assertTrue(CoreRoutineSymptoms.isNotEmpty())

        // Verify completing onboarding updates fullName, age, heightCm, weightKg, knownDiseasesOrMedications, emailId, phoneNumber, primaryHealthGoal, selectedSymptoms, and selectedConditionId
        val completedOnboarding = initialOnboarding.copy(
            fullName = "Dr. Lateef Papa",
            age = 54,
            ageGroup = deriveAgeGroupLabel(54),
            heightCm = 172,
            weightKg = 74,
            knownDiseasesOrMedications = "Cervical Spondylitis, Vitamin D3 60000 IU",
            emailId = "dr.lateefpapa@gmail.com",
            phoneNumber = "+91 98765 43210",
            primaryHealthGoal = "Posture, Stiffness & Mobility Recovery",
            primaryConditionId = "frozen_shoulder",
            selectedSymptoms = listOf("Morning joint or spinal stiffness", "Neck or shoulder ache from prolonged sitting")
        )
        viewModel.completeOnboarding(completedOnboarding)
        assertTrue(viewModel.onboardingData.value.isOnboardingCompleted)
        assertEquals("Dr. Lateef Papa", viewModel.onboardingData.value.fullName)
        assertEquals(54, viewModel.onboardingData.value.age)
        assertEquals(172, viewModel.onboardingData.value.heightCm)
        assertEquals(74, viewModel.onboardingData.value.weightKg)
        assertEquals("Cervical Spondylitis, Vitamin D3 60000 IU", viewModel.onboardingData.value.knownDiseasesOrMedications)
        assertEquals("dr.lateefpapa@gmail.com", viewModel.onboardingData.value.emailId)
        assertEquals("+91 98765 43210", viewModel.onboardingData.value.phoneNumber)
        assertEquals("frozen_shoulder", viewModel.selectedConditionId.value)
        val bmi = com.example.data.model.calculateBmi(172, 74)
        assertTrue("Calculated BMI should be ~25.0", bmi in 24.5..25.5)

        // Verify all 18 disease-oriented programs are loaded and populated for ConditionDetailScreen
        assertTrue(DiseaseCatalog.conditions.size >= 18)
        DiseaseCatalog.conditions.forEach { condition ->
            assertTrue("Overview non-blank for ${condition.id}", condition.overview.isNotBlank())
            assertTrue("Goals non-empty for ${condition.id}", condition.programGoals.isNotEmpty())
            assertTrue("Precautions non-blank for ${condition.id}", condition.precautionsSummary.isNotBlank())
            assertTrue("Aggravating movements non-empty for ${condition.id}", condition.aggravatingMovementsToAvoid.isNotEmpty())
            assertTrue("Medical referral criteria non-empty for ${condition.id}", condition.medicalReferralCriteria.isNotEmpty())
            assertTrue("Asanas non-empty for ${condition.id}", condition.asanas.isNotEmpty())
            assertTrue("Pranayamas non-empty for ${condition.id}", condition.pranayamas.isNotEmpty())
            assertTrue("Dhyanas non-empty for ${condition.id}", condition.dhyanas.isNotEmpty())
            assertTrue("Diet foods non-empty for ${condition.id}", condition.dietaryProtocol.healingFoodsToInclude.isNotEmpty())
        }

        // Verify 7 meditation & relaxation tracks exist
        assertEquals(7, MeditationCatalog.tracks.size)

        // Verify PDF clinician report summary builder
        val summary = PdfReportGenerator.buildReportSummary(
            profile = null,
            conditionId = "osteoarthritis",
            logs = emptyList(),
            sessions = emptyList()
        )
        assertTrue(summary.conditionName.contains("Osteoarthritis"))
        assertEquals(3, summary.questionsForClinician.size)

        // Verify daily symptom slider descriptors (Pain, Stiffness, Mood)
        assertEquals("Mild", com.example.ui.screens.describePainSeverity(2))
        assertEquals("Mild Morning Stiffness", com.example.ui.screens.describeStiffnessSeverity(3))
        assertEquals("Calm & Balanced", com.example.ui.screens.describeMoodState(8))

        // Verify ProgressChartScreen chronological trend point transformation from Firestore RecoveryLog models
        val sampleLogs = listOf(
            com.example.data.model.RecoveryLog(
                id = "log2",
                dateLabel = "2026-10-20",
                painLevel = 3,
                stiffnessLevel = 2,
                stressLevel = 2,
                mobilityScore = 8,
                sleepQuality = 9,
                asanaMinutes = 20,
                pranayamaMinutes = 10,
                dhyanaMinutes = 10
            ),
            com.example.data.model.RecoveryLog(
                id = "log1",
                dateLabel = "2026-10-19",
                painLevel = 6,
                stiffnessLevel = 5,
                stressLevel = 5,
                mobilityScore = 5,
                sleepQuality = 6,
                asanaMinutes = 15,
                pranayamaMinutes = 10,
                dhyanaMinutes = 5
            )
        )
        val trendPoints = com.example.ui.screens.buildChronologicalTrendPoints(sampleLogs, maxEntries = 14)
        assertEquals(2, trendPoints.size)
        assertEquals("2026-10-19", trendPoints.first().dateLabel)
        assertEquals("2026-10-20", trendPoints.last().dateLabel)
        assertEquals(8, trendPoints.last().mood)
        assertEquals(3, trendPoints.last().pain)

        // Verify PracticeLibraryScreen catalog entries, categories, difficulties, and condition associations
        val libraryEntries = com.example.ui.screens.buildPracticeLibraryEntries(DiseaseCatalog.conditions)
        assertTrue("Library should contain practices across all conditions", libraryEntries.isNotEmpty())
        assertTrue(com.example.ui.screens.LibraryCategoryOptions.contains("Asana: Spinal & Neck"))
        assertTrue(com.example.ui.screens.LibraryDifficultyOptions.contains("Beginner"))
        libraryEntries.forEach { entry ->
            assertTrue(entry.shortDescription.isNotBlank())
            assertTrue(entry.movementCategory.isNotBlank())
            assertTrue(entry.difficultyTier.isNotBlank())
            assertTrue(entry.conditionIds.isNotEmpty())
        }
        val oaFiltered = libraryEntries.filter { "osteoarthritis" in it.conditionIds }
        assertTrue("Osteoarthritis filter should return practices", oaFiltered.isNotEmpty())

        // Verify weekly recovery progress PDF export from Firestore models and clinician share flow
        val sampleProfile = com.example.data.model.PatientProfile(
            id = "user_123",
            userId = "user_123",
            displayName = "Aarav Sharma",
            primaryConditionId = "osteoarthritis",
            baselinePainScore = 7,
            baselineMobilityScore = 4
        )
        val sampleSessions = listOf(
            com.example.data.model.PracticeSession(
                id = "sess_1",
                userId = "user_123",
                conditionId = "osteoarthritis",
                practiceId = "oa_heel_slides",
                practiceTitle = "Supported Heel Slides",
                category = "Asana",
                durationMinutes = 10,
                reliefRating = 5
            )
        )
        val pdfExportResult = PdfReportGenerator.exportPdfReport(
            context = context,
            profile = sampleProfile,
            conditionId = "osteoarthritis",
            logs = sampleLogs,
            sessions = sampleSessions,
            onboardingData = viewModel.onboardingData.value
        )
        assertTrue("Weekly PDF export should succeed", pdfExportResult.isSuccess)
        val exportedSummary = pdfExportResult.getOrThrow()
        assertEquals(2, exportedSummary.weeklyDaysLogged)
        assertEquals(45, exportedSummary.weeklyAsanaMinutes)
        assertEquals(20, exportedSummary.weeklyPranayamaMinutes)
        assertEquals(15, exportedSummary.weeklyDhyanaMinutes)
        assertTrue(exportedSummary.filePath.endsWith("YogaSwasthya_Recovery_Report.pdf"))

        val pdfFile = java.io.File(exportedSummary.filePath)
        assertTrue("Exported PDF file must exist on disk", pdfFile.exists())
        assertTrue("Exported PDF file must be non-empty", pdfFile.length() > 100L)
        val headerBytes = pdfFile.inputStream().use { stream ->
            val buf = ByteArray(4)
            stream.read(buf)
            String(buf, Charsets.US_ASCII)
        }
        assertEquals("%PDF", headerBytes)

        val shareResult = PdfReportGenerator.sharePdfReport(
            context = context,
            summary = exportedSummary,
            profile = sampleProfile,
            conditionId = "osteoarthritis",
            logs = sampleLogs,
            sessions = sampleSessions
        )
        assertTrue("Sharing weekly PDF report with clinician should succeed", shareResult.isSuccess)
    }
}
