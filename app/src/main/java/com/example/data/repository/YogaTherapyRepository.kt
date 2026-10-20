package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class YogaTherapyRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observePatientProfile(userId: String): Flow<PatientProfile?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                close(error)
                return@addSnapshotListener
            }
            val profile = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(
                    PatientProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
            trySend(profile)
        }
        awaitClose { registration.remove() }
    }

    suspend fun getPatientProfile(userId: String): Result<PatientProfile?> {
        val docRef = db.collection("users").document(userId)
        return try {
            val snapshot = docRef.get().await()
            val profile = if (snapshot.exists()) {
                snapshot.toObject(
                    PatientProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun savePatientProfile(profile: PatientProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                docRef.update(profile.toUpdateMap()).await()
            } else {
                docRef.set(profile.toCreateMap(uid)).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeRecoveryLogs(userId: String): Flow<List<RecoveryLog>> = callbackFlow {
        val collectionRef = db.collection("users").document(userId).collection("recovery_logs")
        val path = "users/$userId/recovery_logs"
        val registration = collectionRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val logs = querySnapshot?.toObjects(
                    RecoveryLog::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.sortedByDescending { it.createdAt ?: Timestamp(0, 0) } ?: emptyList()
                trySend(logs)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getRecoveryLogs(userId: String): Result<List<RecoveryLog>> {
        val collectionRef = db.collection("users").document(userId).collection("recovery_logs")
        val path = "users/$userId/recovery_logs"
        return try {
            val snapshot = collectionRef.whereEqualTo("userId", userId).get().await()
            val logs = snapshot.toObjects(
                RecoveryLog::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            Result.success(logs)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun addRecoveryLog(log: RecoveryLog): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("recovery_logs")
        val docRef = collectionRef.document()
        return try {
            docRef.set(log.toCreateMap(uid)).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteRecoveryLog(userId: String, logId: String): Result<Unit> {
        val docRef = db.collection("users").document(userId)
            .collection("recovery_logs").document(logId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observePracticeSessions(userId: String): Flow<List<PracticeSession>> = callbackFlow {
        val collectionRef = db.collection("users").document(userId).collection("practice_sessions")
        val path = "users/$userId/practice_sessions"
        val registration = collectionRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = querySnapshot?.toObjects(
                    PracticeSession::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.sortedByDescending { it.createdAt ?: Timestamp(0, 0) } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getPracticeSessions(userId: String): Result<List<PracticeSession>> {
        val collectionRef = db.collection("users").document(userId).collection("practice_sessions")
        val path = "users/$userId/practice_sessions"
        return try {
            val snapshot = collectionRef.whereEqualTo("userId", userId).get().await()
            val sessions = snapshot.toObjects(
                PracticeSession::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            Result.success(sessions)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun logPracticeSession(session: PracticeSession): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("practice_sessions")
        val docRef = collectionRef.document()
        return try {
            docRef.set(session.toCreateMap(uid)).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Fetches the user's latest weekly recovery progress from Firestore (`users/{userId}`,
     * `recovery_logs`, and `practice_sessions`), falling back to in-memory snapshots when offline,
     * and exports a structured clinician-ready PDF file.
     */
    suspend fun exportWeeklyRecoveryProgressToPdf(
        context: Context,
        userId: String,
        conditionId: String,
        fallbackProfile: PatientProfile? = null,
        fallbackLogs: List<RecoveryLog> = emptyList(),
        fallbackSessions: List<PracticeSession> = emptyList(),
        onboardingData: com.example.data.model.OnboardingProfileData? = null
    ): Result<com.example.util.PdfReportGenerator.GeneratedReportSummary> {
        val resolvedProfile = if (userId.isNotBlank() && userId != "guest_local") {
            getPatientProfile(userId).getOrNull() ?: fallbackProfile
        } else {
            fallbackProfile
        }

        val resolvedLogs = if (userId.isNotBlank() && userId != "guest_local") {
            val remoteLogs = getRecoveryLogs(userId).getOrNull()
            if (!remoteLogs.isNullOrEmpty()) remoteLogs else fallbackLogs
        } else {
            fallbackLogs
        }

        val resolvedSessions = if (userId.isNotBlank() && userId != "guest_local") {
            val remoteSessions = getPracticeSessions(userId).getOrNull()
            if (!remoteSessions.isNullOrEmpty()) remoteSessions else fallbackSessions
        } else {
            fallbackSessions
        }

        val weeklyLogs = com.example.util.PdfReportGenerator.extractWeeklyLogs(resolvedLogs)
        val weeklySessions = com.example.util.PdfReportGenerator.extractWeeklySessions(resolvedSessions)

        return com.example.util.PdfReportGenerator.exportPdfReport(
            context = context,
            profile = resolvedProfile,
            conditionId = conditionId.ifBlank { resolvedProfile?.primaryConditionId ?: "osteoarthritis" },
            logs = weeklyLogs,
            sessions = weeklySessions,
            onboardingData = onboardingData
        )
    }
}
