package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class PatientProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val age: Int = 42,
    val heightCm: Int = 165,
    val weightKg: Int = 68,
    val knownDiseasesOrMedications: String = "Cervical Spondylitis • Vitamin D3",
    val emailId: String = "",
    val phoneNumber: String = "",
    val primaryConditionId: String = "cervical_lumbar_spondylitis",
    val activeConditionIds: List<String> = listOf("cervical_lumbar_spondylitis", "osteoarthritis", "anxiety"),
    val baselinePainScore: Int = 6,
    val baselineMobilityScore: Int = 5,
    val baselineStressScore: Int = 6,
    val dietaryPreference: String = "Vegetarian • Pan-Indian Sattvic",
    val dailyGoalMinutes: Int = 30,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> {
        return mapOf(
            "userId" to uid,
            "displayName" to displayName.ifBlank { "Yoga Practitioner" }.take(80),
            "age" to age.coerceIn(1, 120),
            "heightCm" to heightCm.coerceIn(50, 260),
            "weightKg" to weightKg.coerceIn(15, 300),
            "knownDiseasesOrMedications" to knownDiseasesOrMedications.take(300),
            "emailId" to emailId.take(120),
            "phoneNumber" to phoneNumber.take(30),
            "primaryConditionId" to primaryConditionId.ifBlank { "cervical_lumbar_spondylitis" }.take(64),
            "activeConditionIds" to activeConditionIds.take(10).map { it.take(64) },
            "baselinePainScore" to baselinePainScore.coerceIn(0, 10),
            "baselineMobilityScore" to baselineMobilityScore.coerceIn(1, 10),
            "baselineStressScore" to baselineStressScore.coerceIn(0, 10),
            "dietaryPreference" to dietaryPreference.ifBlank { "Sattvic Anti-Inflammatory" }.take(50),
            "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5, 180),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }

    fun toUpdateMap(): Map<String, Any> {
        return mapOf(
            "displayName" to displayName.ifBlank { "Yoga Practitioner" }.take(80),
            "age" to age.coerceIn(1, 120),
            "heightCm" to heightCm.coerceIn(50, 260),
            "weightKg" to weightKg.coerceIn(15, 300),
            "knownDiseasesOrMedications" to knownDiseasesOrMedications.take(300),
            "emailId" to emailId.take(120),
            "phoneNumber" to phoneNumber.take(30),
            "primaryConditionId" to primaryConditionId.ifBlank { "cervical_lumbar_spondylitis" }.take(64),
            "activeConditionIds" to activeConditionIds.take(10).map { it.take(64) },
            "baselinePainScore" to baselinePainScore.coerceIn(0, 10),
            "baselineMobilityScore" to baselineMobilityScore.coerceIn(1, 10),
            "baselineStressScore" to baselineStressScore.coerceIn(0, 10),
            "dietaryPreference" to dietaryPreference.ifBlank { "Sattvic Anti-Inflammatory" }.take(50),
            "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5, 180),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}

@IgnoreExtraProperties
data class RecoveryLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val conditionId: String = "",
    val dateLabel: String = "",
    val painLevel: Int = 5,
    val stiffnessLevel: Int = 5,
    val stressLevel: Int = 5,
    val mobilityScore: Int = 6,
    val sleepQuality: Int = 4,
    val asanaMinutes: Int = 15,
    val pranayamaMinutes: Int = 10,
    val dhyanaMinutes: Int = 10,
    val completedPractices: List<String> = emptyList(),
    val dietAdherenceScore: Int = 80,
    val waterIntakeGlasses: Int = 8,
    val notes: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> {
        val base = mutableMapOf<String, Any>(
            "userId" to uid,
            "conditionId" to conditionId.ifBlank { "cervical_lumbar_spondylitis" }.take(64),
            "dateLabel" to dateLabel.ifBlank { "Today" }.take(30),
            "painLevel" to painLevel.coerceIn(0, 10),
            "stiffnessLevel" to stiffnessLevel.coerceIn(0, 10),
            "stressLevel" to stressLevel.coerceIn(0, 10),
            "mobilityScore" to mobilityScore.coerceIn(1, 10),
            "sleepQuality" to sleepQuality.coerceIn(1, 10),
            "asanaMinutes" to asanaMinutes.coerceIn(0, 240),
            "pranayamaMinutes" to pranayamaMinutes.coerceIn(0, 240),
            "dhyanaMinutes" to dhyanaMinutes.coerceIn(0, 240),
            "completedPractices" to completedPractices.take(30).map { it.take(100) },
            "dietAdherenceScore" to dietAdherenceScore.coerceIn(0, 100),
            "waterIntakeGlasses" to waterIntakeGlasses.coerceIn(0, 25),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!notes.isNullOrBlank()) {
            base["notes"] = notes.take(500)
        }
        return base
    }
}

@IgnoreExtraProperties
data class PracticeSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val conditionId: String = "",
    val practiceId: String = "",
    val practiceTitle: String = "",
    val category: String = "Asana",
    val durationMinutes: Int = 10,
    val reliefRating: Int = 5,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> {
        val validCategory = if (category in listOf("Asana", "Pranayama", "Dhyana")) category else "Asana"
        return mapOf(
            "userId" to uid,
            "conditionId" to conditionId.ifBlank { "cervical_lumbar_spondylitis" }.take(64),
            "practiceId" to practiceId.ifBlank { "practice" }.take(64),
            "practiceTitle" to practiceTitle.ifBlank { "Guided Practice" }.take(100),
            "category" to validCategory,
            "durationMinutes" to durationMinutes.coerceIn(1, 180),
            "reliefRating" to reliefRating.coerceIn(1, 5),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}

enum class PracticeCategory(val label: String, val sanskritSubtitle: String) {
    ASANA("Asana", "Physio-Informed Postures & Alignment"),
    PRANAYAMA("Pranayama", "Screened Breath Regulation"),
    DHYANA("Dhyana", "Mindfulness & Somatic Relaxation")
}

enum class EvidenceCategory(val badgeLabel: String) {
    CLINICAL_EVIDENCE("Clinical Evidence"),
    AYURVEDIC_TRADITIONAL("Ayurvedic Traditional"),
    GENERAL_NUTRITION("General Nutrition Education")
}

data class TherapeuticPractice(
    val id: String,
    val title: String,
    val sanskritName: String,
    val category: PracticeCategory,
    val durationMinutes: Int,
    val holdAndReps: String,
    val difficulty: String,
    val anatomicalFocus: String,
    val breathingCue: String,
    val stepByStepInstructions: List<String>,
    val therapeuticBenefits: List<String>,
    val contraindications: List<String>,
    val startingPosition: String = "Comfortable seated or supine neutral spinal alignment on a non-slip yoga mat.",
    val chairAndWallModifications: String = "Can be practiced seated on a firm armless chair with feet flat on the floor or using a wall/cushion for support during stiffness or flare-ups.",
    val commonMistakes: List<String> = listOf(
        "Holding the breath or clenching the jaw while concentrating on alignment",
        "Pushing into sharp or radiating pain rather than staying within a comfortable 60–70% range"
    ),
    val safeExitInstructions: String = "Exhale slowly, release muscular engagement gradually, and pause for 2 natural breaths before transitioning.",
    val stopSignals: List<String> = listOf(
        "Sharp, stabbing, or electric-like radiating pain",
        "New numbness, tingling, or weakness in the arms or legs",
        "Dizziness, vertigo, chest discomfort, or unusual breathlessness"
    ),
    val inhaleSeconds: Int = 4,
    val exhaleSeconds: Int = 6
)

data class DietItem(
    val name: String,
    val rationale: String,
    val activeCompoundOrQuality: String,
    val evidenceType: EvidenceCategory = EvidenceCategory.GENERAL_NUTRITION,
    val isVegetarian: Boolean = true,
    val budgetFriendlySwap: String = "Locally seasonal greens, roasted chana, or homemade moong dal soup"
)

data class MealSlot(
    val timeOfDay: String,
    val title: String,
    val recommendation: String,
    val nonVegOption: String = "Optional poached egg whites or lightly spiced steamed fish/chicken clear broth (if non-vegetarian)"
)

data class DietaryProtocol(
    val doshaBalance: String,
    val corePrinciple: String,
    val herbalRemedy: String,
    val healingFoodsToInclude: List<DietItem>,
    val foodsToAvoid: List<DietItem>,
    val dailyMealSchedule: List<MealSlot>,
    val hydrationGuidance: String = "Sip 2.0–2.5 liters of warm or room-temperature water throughout the day; avoid gulping ice-cold water with meals.",
    val ergonomicsAndLifestyle: List<String> = listOf(
        "Keep screens at eye level and take a 2-minute standing/posture break every 30–40 minutes.",
        "Maintain a consistent sleep window (10:00 PM – 6:00 AM) and dim screens 45 minutes before bed.",
        "Follow your physician's prescribed medications; dietary education supports but never replaces medical therapy."
    )
)

data class DiseaseCondition(
    val id: String,
    val name: String,
    val shortName: String,
    val clinicalSubtitle: String,
    val categoryTag: String,
    val overview: String,
    val rootCauseAyurvedic: String,
    val targetSymptoms: List<String>,
    val accentHex: Long,
    val asanas: List<TherapeuticPractice>,
    val pranayamas: List<TherapeuticPractice>,
    val dhyanas: List<TherapeuticPractice>,
    val dietaryProtocol: DietaryProtocol,
    val dailyRecoveryTips: List<String>,
    val programGoals: List<String> = listOf(
        "Support comfortable, pain-free functional range of motion",
        "Reduce muscular guarding and autonomic stress reactivity",
        "Build sustainable daily movement adherence without provoking flare-ups"
    ),
    val aggravatingMovementsToAvoid: List<String> = listOf(
        "Rapid, jerky ballistic stretching or forcing joints past pain limits",
        "Unsupported headstands, shoulder stands, or deep spinal hyperextension",
        "Prolonged breath retention (Kumbhaka) or forceful hyperventilation"
    ),
    val flareUpAndMobilityModifications: String = "During painful flare-ups, reduce session duration by 50%, switch to Chair Yoga or supine bolster-supported positions, and prioritize gentle diaphragmatic breathing and body-scan relaxation.",
    val trackedFunctionalMetrics: List<String> = listOf(
        "Daily Pain Score (0–10) & Morning Stiffness (0–10)",
        "Sitting, Standing & Walking Tolerance",
        "Sleep Duration & Restorative Quality (1–5)",
        "Ability to Perform Everyday Functional Tasks"
    ),
    val medicalReferralCriteria: List<String> = listOf(
        "New or progressive limb weakness, numbness, or pins-and-needles",
        "Unexplained fever, weight loss, or unremitting night pain",
        "Sudden severe headache, chest pain, fainting, or bladder/bowel changes"
    ),
    val weeklyScheduleSummary: String = "Morning: 10–15 min gentle Asanas • Afternoon: 3–5 min ergonomic posture break • Evening: 5–10 min Pranayama & Dhyana • Night: 2-min sleep & symptom check-in (5–6 days/week).",
    val precautionsSummary: String = "Practice within 60–70% of comfortable range of motion, never hold the breath during physical effort, and stop immediately if sharp, radiating pain, dizziness, or numbness occurs."
)

data class MeditationTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetPurpose: String,
    val availableDurationsMinutes: List<Int> = listOf(3, 5, 10, 15),
    val ambientSoundLabel: String,
    val writtenGuideSteps: List<String>
)

data class OnboardingProfileData(
    val fullName: String = "Aarav Sharma",
    val age: Int = 42,
    val ageGroup: String = "35–50 yrs",
    val heightCm: Int = 165,
    val weightKg: Int = 68,
    val knownDiseasesOrMedications: String = "Cervical Spondylitis • Vitamin D3",
    val emailId: String = "",
    val phoneNumber: String = "",
    val primaryHealthGoal: String = "Joint & Spinal Pain Relief",
    val primaryConditionId: String = "cervical_lumbar_spondylitis",
    val selectedSymptoms: List<String> = listOf(
        "Neck stiffness & occipital radiating pain",
        "Lower back ache aggravated by prolonged sitting",
        "Morning joint or spinal stiffness"
    ),
    val baselinePainScore: Int = 6,
    val baselineMobilityScore: Int = 5,
    val baselineStressScore: Int = 5,
    val mobilityLevel: String = "Moderate (Occasional Stiffness)",
    val yogaExperience: String = "Beginner",
    val languageCode: String = "English",
    val dietaryType: String = "Vegetarian",
    val regionalFoodStyle: String = "Pan-Indian",
    val availablePracticeMinutes: Int = 20,
    val reminderTime: String = "07:00 AM",
    val hasCompletedSafetyScreening: Boolean = true,
    val redFlagsDetected: Boolean = false,
    val clinicianSharingConsent: Boolean = false,
    val clinicianNotes: String = "Conservative low-load spinal & joint protocol approved. Avoid end-range flexion during morning stiffness.",
    val nextClinicianReviewDate: String = "In 14 Days",
    val isOnboardingCompleted: Boolean = false
)

fun calculateBmi(heightCm: Int, weightKg: Int): Double {
    val safeHeightMeters = (heightCm.coerceIn(50, 260)) / 100.0
    val safeWeightKg = weightKg.coerceIn(15, 300).toDouble()
    return safeWeightKg / (safeHeightMeters * safeHeightMeters)
}

fun describeBmiCategory(bmi: Double): String = when {
    bmi < 18.5 -> "Underweight (Gentle Nutrient & Strength Focus)"
    bmi < 23.0 -> "Normal BMI (Balanced Mobility & Core)"
    bmi < 27.5 -> "Overweight (Low-Impact Joint Unloading)"
    else -> "Metabolic & Joint-Protective Pacing"
}
