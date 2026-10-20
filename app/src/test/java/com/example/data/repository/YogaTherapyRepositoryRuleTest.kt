package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class YogaTherapyRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndObservePatientProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = YogaTherapyRepository(firestore)

        val profile = PatientProfile(
            userId = uid,
            displayName = "Dr. Meera Nair",
            age = 48,
            heightCm = 162,
            weightKg = 64,
            knownDiseasesOrMedications = "Cervical Spondylitis, Vitamin D3",
            emailId = "meera.nair@example.com",
            phoneNumber = "+91 98450 12345",
            primaryConditionId = "cervical_lumbar_spondylitis",
            activeConditionIds = listOf("cervical_lumbar_spondylitis", "migraine"),
            baselinePainScore = 7,
            baselineMobilityScore = 4,
            baselineStressScore = 6,
            dietaryPreference = "Sattvic Anti-Inflammatory",
            dailyGoalMinutes = 35
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.savePatientProfile(profile) }
        assertTrue("Expected profile save to succeed", saveResult.isSuccess)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observePatientProfile(uid).first { it != null }
        }
        assertNotNull(observed)
        assertEquals("Dr. Meera Nair", observed?.displayName)
        assertEquals(48, observed?.age)
        assertEquals(162, observed?.heightCm)
        assertEquals(64, observed?.weightKg)
        assertEquals("Cervical Spondylitis, Vitamin D3", observed?.knownDiseasesOrMedications)
        assertEquals("meera.nair@example.com", observed?.emailId)
        assertEquals("+91 98450 12345", observed?.phoneNumber)
        assertEquals("cervical_lumbar_spondylitis", observed?.primaryConditionId)
    }

    @Test
    fun addAndObserveRecoveryLogs_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = YogaTherapyRepository(firestore)

        val log = RecoveryLog(
            userId = uid,
            conditionId = "frozen_shoulder",
            dateLabel = "2026-10-20",
            painLevel = 4,
            stiffnessLevel = 4,
            stressLevel = 3,
            mobilityScore = 7,
            sleepQuality = 8,
            asanaMinutes = 20,
            pranayamaMinutes = 10,
            dhyanaMinutes = 10,
            completedPractices = listOf("gomukhasana_arms", "nadi_shodhana"),
            dietAdherenceScore = 90,
            waterIntakeGlasses = 8,
            notes = "Better abduction range in right shoulder"
        )

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.addRecoveryLog(log) }
        assertTrue(createResult.isSuccess)
        val logId = createResult.getOrThrow()
        assertTrue(logId.isNotEmpty())

        val emittedLogs = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeRecoveryLogs(uid).first { list -> list.any { it.id == logId } }
        }
        assertTrue(emittedLogs.any { it.id == logId && it.conditionId == "frozen_shoulder" })
    }

    @Test
    fun logAndObservePracticeSession_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = YogaTherapyRepository(firestore)

        val session = PracticeSession(
            userId = uid,
            conditionId = "anxiety",
            practiceId = "nadi_shodhana",
            practiceTitle = "Nadi Shodhana Pranayama",
            category = "Pranayama",
            durationMinutes = 12,
            reliefRating = 5
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) { repository.logPracticeSession(session) }
        assertTrue(result.isSuccess)
        val sessionId = result.getOrThrow()

        val sessions = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observePracticeSessions(uid).first { list -> list.any { it.id == sessionId } }
        }
        assertTrue(sessions.any { it.id == sessionId && it.category == "Pranayama" })
    }

    @Test
    fun getRecoveryLogs_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = YogaTherapyRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.addRecoveryLog(
                RecoveryLog(
                    userId = aliceUid,
                    conditionId = "migraine",
                    dateLabel = "2026-10-20"
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = YogaTherapyRepository(firestore)
        val crossUserResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getRecoveryLogs(aliceUid)
        }
        assertTrue("Cross-user access must fail", crossUserResult.isFailure)
        val ex = crossUserResult.exceptionOrNull() as? FirebaseFirestoreException
        assertNotNull(ex)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
    }

    @Test
    fun observeRecoveryLogs_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = YogaTherapyRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeRecoveryLogs(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated user")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@pranaveda.test"
        const val BOB_EMAIL = "bob@pranaveda.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
