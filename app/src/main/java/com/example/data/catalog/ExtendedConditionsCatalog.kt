package com.example.data.catalog

import com.example.data.model.DietItem
import com.example.data.model.DietaryProtocol
import com.example.data.model.DiseaseCondition
import com.example.data.model.EvidenceCategory
import com.example.data.model.MealSlot
import com.example.data.model.PracticeCategory
import com.example.data.model.TherapeuticPractice

object ExtendedConditionsCatalog {

    private val gentleChairNeckRelease = TherapeuticPractice(
        id = "chair_neck_scapular_release",
        title = "Seated Scapular Setting & Micro-Chin Nod",
        sanskritName = "ग्रीवा शक्ति विकासक • Griva Shakti Vikasaka",
        category = PracticeCategory.ASANA,
        durationMinutes = 6,
        holdAndReps = "Hold 5 seconds × 8 gentle repetitions",
        difficulty = "Beginner / Chair-Safe",
        anatomicalFocus = "Deep Cervical Flexors, Lower Trapezius & Scapular Stabilizers",
        breathingCue = "Inhale tall through the crown; exhale gently drawing the chin 1 cm inward without tilting down.",
        startingPosition = "Sit upright on a firm chair with feet flat on the floor, thighs parallel, and forearms resting on thighs.",
        stepByStepInstructions = listOf(
            "Sit tall and let your shoulder blades glide gently down and slightly together without overarching your lower back.",
            "Gaze straight ahead at horizon level and softly glide your chin straight back (like making a subtle double chin) to activate deep neck stabilizers.",
            "Hold for 5 calm seconds while breathing normally, then release 50% of the effort and repeat 8 times."
        ),
        therapeuticBenefits = listOf(
            "Unloads cervical facet joints and counteracts forward-head desk posture",
            "Reduces upper trapezius and levator scapulae overactivity",
            "Improves comfortable desk and reading tolerance"
        ),
        contraindications = listOf(
            "Never force the chin aggressively backward or perform full 360-degree neck circles"
        ),
        chairAndWallModifications = "Practice standing with the back of your pelvis, shoulder blades, and occiput lightly touching a wall for postural feedback."
    )

    private val lumbarPelvicTilt = TherapeuticPractice(
        id = "supine_pelvic_tilt_kati",
        title = "Supta Kati Chalana (Supported Supine Pelvic Tilts & Knee Swings)",
        sanskritName = "सुप्त कटि चलन • Supta Kati Chalana",
        category = PracticeCategory.ASANA,
        durationMinutes = 8,
        holdAndReps = "10 slow pelvic tilts + 6 gentle windshield-wiper sways",
        difficulty = "Beginner / Flare-Up Safe",
        anatomicalFocus = "Lumbar L1–S1 Segments, Transverse Abdominis & Thoracolumbar Fascia",
        breathingCue = "Inhale in neutral pelvis; exhale gently imprinting the lower back into the mat.",
        startingPosition = "Lie on your back on a firm mat with knees bent, feet hip-width apart, and a thin pillow under your head.",
        stepByStepInstructions = listOf(
            "Rest your palms lightly on your lower abdomen with knees bent and feet flat.",
            "On a slow exhalation, gently engage your lower belly to flatten the small of your back softly against the mat.",
            "On an inhalation, allow the pelvis to roll back to a comfortable neutral curve—never force an arch.",
            "Follow with tiny 20-degree side-to-side knee sways within a 100% pain-free arc."
        ),
        therapeuticBenefits = listOf(
            "Relieves lumbar morning stiffness and lubricates intervertebral facet joints",
            "Activates deep core stabilizers without spinal flexion load",
            "Improves sitting-to-standing transition comfort"
        ),
        contraindications = listOf(
            "Stop immediately if you experience new leg weakness, saddle numbness, or radiating electric pain"
        ),
        chairAndWallModifications = "Can be performed seated on the front half of a sturdy chair by gently rocking the pelvis from neutral to a slight posterior tilt."
    )

    private val diaphragmaticSlowBreath = TherapeuticPractice(
        id = "screened_diaphragmatic_breath",
        title = "Gentle Diaphragmatic & Coherent Slow Breathing",
        sanskritName = "उदर श्वसन • Udara Pranayama (No Retention)",
        category = PracticeCategory.PRANAYAMA,
        durationMinutes = 6,
        holdAndReps = "5-second inhale : 5-second exhale for 6 minutes (Zero breath hold)",
        difficulty = "All Levels / Medically Screened",
        anatomicalFocus = "Thoraco-Abdominal Diaphragm, Vagus Nerve & Baroreceptor Reflex",
        breathingCue = "Inhale softly through the nose for 4–5s expanding the lower ribs; exhale smoothly for 5–6s.",
        startingPosition = "Sit supported in a chair or lie semi-reclined with knees supported by a pillow.",
        stepByStepInstructions = listOf(
            "Place one palm gently over the upper abdomen and the other over the side ribs.",
            "Breathe in quietly through the nostrils at a comfortable pace, feeling the lower ribs widen without lifting the shoulders.",
            "Exhale smoothly without forcing the air out; never hold your breath if you have hypertension, cardiac history, or dizziness."
        ),
        therapeuticBenefits = listOf(
            "Supports autonomic calm, healthy blood pressure regulation, and pain coping",
            "Safe for older adults, hypertension, and beginners"
        ),
        contraindications = listOf(
            "Avoid breath retention (Kumbhaka) or rapid hyperventilation (Kapalabhati/Bhastrika)"
        )
    )

    private val somaticBodyScanDhyana = TherapeuticPractice(
        id = "somatic_body_scan_relaxation",
        title = "Progressive Somatic Body-Scan & Pain Coping Dhyana",
        sanskritName = "काया स्थैर्यम् • Kaya Sthairyam & Shithilikarana",
        category = PracticeCategory.DHYANA,
        durationMinutes = 10,
        holdAndReps = "5, 10, or 15-minute guided or silent body scan",
        difficulty = "Restorative",
        anatomicalFocus = "Central Nervous System Pain Modulation & Parasympathetic Recovery",
        breathingCue = "Natural, unhurried breath; soften one muscle group on every exhalation.",
        stepByStepInstructions = listOf(
            "Settle into a comfortable seated or bolster-supported supine posture.",
            "Guide your attention progressively from the forehead and jaw down through the neck, shoulders, back, hips, knees, and feet.",
            "Notice areas of discomfort with non-judgmental kindness, allowing surrounding protective muscle tension to soften."
        ),
        therapeuticBenefits = listOf(
            "Reduces central pain amplification, mental fatigue, and sleep latency",
            "Supports emotional regulation alongside medical care"
        ),
        contraindications = listOf("If lying flat causes reflux or dizziness, elevate the head and torso on pillows")
    )

    private val standardBalancedIndianDiet = DietaryProtocol(
        doshaBalance = "Balanced Sattvic & Evidence-Informed Indian Nutrition",
        corePrinciple = "Combine clinical nutrition principles (adequate protein, high fiber, low glycemic load, and optimal hydration) with traditional Indian home-cooked meals tailored to your digestive tolerance and physician's guidance.",
        herbalRemedy = "Warm cumin-coriander-fennel (CCF) water or ginger-tulsi infusion between meals.",
        healingFoodsToInclude = listOf(
            DietItem(
                name = "Moong Dal, Masoor Dal, Paneer/Tofu, Curd, or Steamed Fish/Egg Whites",
                rationale = "Provides essential amino acids for muscle and tendon repair around vulnerable joints and spine.",
                activeCompoundOrQuality = "High-Bioavailability Protein",
                evidenceType = EvidenceCategory.CLINICAL_EVIDENCE,
                isVegetarian = true,
                budgetFriendlySwap = "Roasted chana (gram), soya chunks, or sprouted green moong"
            ),
            DietItem(
                name = "Whole Millets (Ragi, Jowar, Bajra), Hand-Pounded Rice & Oats",
                rationale = "Delivers slow-release complex carbohydrates, magnesium, and calcium without sharp glycemic spikes.",
                activeCompoundOrQuality = "Low Glycemic Index • Rich in Calcium & Magnesium",
                evidenceType = EvidenceCategory.CLINICAL_EVIDENCE,
                isVegetarian = true,
                budgetFriendlySwap = "Broken wheat (Daliya) or unpolished parboiled rice"
            ),
            DietItem(
                name = "Turmeric (Haldi), Ginger (Adrak), Garlic & Cold-Pressed Mustard/Sesame Oil",
                rationale = "Traditionally used in Ayurveda to pacify Vata-Kapha stiffness and clinically studied for anti-inflammatory polyphenol support.",
                activeCompoundOrQuality = "Curcuminoids & Gingerols",
                evidenceType = EvidenceCategory.AYURVEDIC_TRADITIONAL,
                isVegetarian = true,
                budgetFriendlySwap = "Fresh kitchen haldi-adrak tadka in daily dal"
            )
        ),
        foodsToAvoid = listOf(
            DietItem(
                name = "Ultra-Processed Packaged Snacks, Excess Salt (Papad/Pickles) & Sugary Cola/Juices",
                rationale = "Promotes systemic low-grade inflammation, fluid retention, blood pressure elevation, and glycemic swings.",
                activeCompoundOrQuality = "High Sodium & Refined Sugars",
                evidenceType = EvidenceCategory.CLINICAL_EVIDENCE
            )
        ),
        dailyMealSchedule = listOf(
            MealSlot("07:00 AM • Morning", "Warm Water + 5 Soaked Almonds & 2 Walnuts", "Hydrates tissues after sleep; avoid strong tea/coffee on an empty stomach."),
            MealSlot("08:30 AM • Breakfast", "Vegetable Poha / Moong Dal Chilla / Ragi Idli with Sambar", "Balanced fiber and protein breakfast.", "2 boiled or poached eggs with multigrain toast & sautéed greens"),
            MealSlot("01:00 PM • Lunch", "2 Phulkas or Red Rice + Bowl of Dal + Seasonal Sabzi (Lauki/Palak/Bhindi) + Buttermilk", "Follow the half-plate vegetable, quarter-plate protein, quarter-plate whole grain rule.", "Grilled/curried fish or skinless chicken breast with roti, dal & cucumber kachumber"),
            MealSlot("04:30 PM • Evening Snack", "Roasted Makhana (Foxnuts) or Sprouted Chana Chaat with Lemon", "Prevents evening energy slump and late-night overeating."),
            MealSlot("07:30 PM • Dinner", "Light Vegetable Khichdi or Daliya Soup with Turmeric & 1 tsp Ghee", "Finish dinner 2–3 hours before sleep for optimal digestion and sleep quality.", "Clear chicken-vegetable soup with lightly steamed vegetables")
        )
    )

    val additionalConditions: List<DiseaseCondition> = listOf(
        // Dedicated Cervical Spondylosis (Section 8B)
        DiseaseCondition(
            id = "cervical_spondylosis",
            name = "Cervical Spondylosis",
            shortName = "Cervical Spondylosis",
            clinicalSubtitle = "Conservative Neck Mobility, Postural Control & Brachial Unloading",
            categoryTag = "Musculoskeletal",
            overview = "Age- or posture-related wear of the cervical spine discs and facet joints. Supportive yoga therapy focuses on gentle chin-nod stabilization, shoulder-blade setting, and relaxation—never forceful neck circles or inversions.",
            rootCauseAyurvedic = "Griva Sandhigata Vata: aggravated Vata in the cervical region causing stiffness and paraspinal guarding.",
            targetSymptoms = listOf(
                "Neck pain and stiffness after desk or phone use",
                "Reduced comfortable head rotation when looking over the shoulder",
                "Upper shoulder blade and trapezius fatigue",
                "Occasional arm heaviness, tingling, or sleep disturbance"
            ),
            accentHex = 0xFF1F4E38,
            asanas = listOf(gentleChairNeckRelease),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf(
                "Raise laptops and phones to eye level; take a 60-second scapular reset every 30 minutes.",
                "Use a single supportive pillow that keeps your neck aligned with your mid-back."
            ),
            programGoals = listOf(
                "Improve comfortable neck rotation and lateral flexion tolerance",
                "Build endurance in deep cervical flexors and lower scapular stabilizers",
                "Extend pain-free desk work duration and improve sleep comfort"
            ),
            aggravatingMovementsToAvoid = listOf(
                "Forceful 360° neck circles or rapid head tossing",
                "Aggressive passive neck pulling or manipulation",
                "Unsupported inversions (Sarvangasana / Sirsasana)"
            ),
            trackedFunctionalMetrics = listOf(
                "Neck Pain Score (0–10)",
                "Comfortable Neck Movement Tolerance",
                "Time Spent Comfortably at a Desk (minutes)",
                "Sleep Disturbance & Any Arm Numbness/Tingling"
            ),
            medicalReferralCriteria = listOf(
                "Progressive arm or hand weakness (e.g., dropping objects)",
                "Spreading numbness/tingling down the arm into the fingers",
                "Gait unsteadiness, clumsy hands, or dizziness with neck motion"
            )
        ),

        // Dedicated Lumbar Spondylosis & Chronic Lower-Back Pain (Section 8C)
        DiseaseCondition(
            id = "lumbar_spondylosis",
            name = "Lumbar Spondylosis & Chronic Low-Back Pain",
            shortName = "Lumbar Spondylosis",
            clinicalSubtitle = "Supported Spinal Mobility, Trunk Conditioning & Psoas Release",
            categoryTag = "Musculoskeletal",
            overview = "Degenerative changes in the lower lumbar discs and facet joints causing chronic ache and morning stiffness. Yoga therapy provides conservative trunk conditioning, supported pelvic mobility, and nervous-system relaxation.",
            rootCauseAyurvedic = "Kati Graha / Katishoola: Vata accumulation in the lumbosacral region.",
            targetSymptoms = listOf(
                "Lower-back stiffness on waking or after prolonged sitting",
                "Reduced standing or walking endurance",
                "Guarding when bending or rising from a chair"
            ),
            accentHex = 0xFF2E5A44,
            asanas = listOf(lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf(
                "Place a small rolled towel at your belt-line when sitting in office chairs or car seats.",
                "Bend at the hips and knees (golfer's pick-up) rather than rounding the waist when lifting objects."
            ),
            programGoals = listOf(
                "Reduce morning lumbar stiffness and paraspinal muscle spasm",
                "Improve sitting, standing, and walking endurance for daily activities",
                "Build gentle core endurance without disc compression"
            ),
            aggravatingMovementsToAvoid = listOf(
                "Unsupported straight-leg toe touches or loaded forward flexion with rotation",
                "High-arch backbends (Chakrasana) or double straight-leg lifts"
            ),
            trackedFunctionalMetrics = listOf(
                "Lower-Back Pain (0–10) & Morning Stiffness (0–10)",
                "Sitting & Standing Tolerance (minutes)",
                "Comfortable Walking Duration",
                "Ability to Perform Everyday Household/Work Activities"
            ),
            medicalReferralCriteria = listOf(
                "URGENT RED FLAG: New bladder or bowel incontinence or retention",
                "URGENT RED FLAG: Saddle-area numbness (around groin/buttocks)",
                "New or worsening foot drop or leg weakness"
            )
        ),

        // 5. Non-Specific Neck Pain
        DiseaseCondition(
            id = "nonspecific_neck_pain",
            name = "Non-Specific Mechanical Neck Pain",
            shortName = "Neck Pain",
            clinicalSubtitle = "Postural Relief, Upper Trapezius Release & Ergonomic Reset",
            categoryTag = "Musculoskeletal",
            overview = "Everyday mechanical neck and upper-shoulder tension often linked to screen ergonomics, stressclenching, or sustained static postures.",
            rootCauseAyurvedic = "Manyastambha: Vata-Kapha muscular tightness in the cervical fascia.",
            targetSymptoms = listOf("Tightness across neck and shoulder tops", "End-of-day postural fatigue"),
            accentHex = 0xFF2B6359,
            asanas = listOf(gentleChairNeckRelease),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Perform 5 gentle shoulder rolls and 3 slow diaphragmatic breaths every hour at your desk.")
        ),

        // 6. Non-Specific Lower-Back Pain
        DiseaseCondition(
            id = "nonspecific_low_back_pain",
            name = "Non-Specific Lower-Back Pain",
            shortName = "Low-Back Pain",
            clinicalSubtitle = "Gentle Core Activation, Hip Mobility & Movement Confidence",
            categoryTag = "Musculoskeletal",
            overview = "Mechanical lower-back ache without specific nerve-root compression. Gentle movement and relaxation reduce fear-avoidance and restore comfortable daily activity.",
            rootCauseAyurvedic = "Kati Vata: muscular fatigue and pelvic stiffness.",
            targetSymptoms = listOf("Dull ache across the belt line", "Stiffness after sitting or standing still"),
            accentHex = 0xFF347456,
            asanas = listOf(lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Avoid prolonged bed rest; gentle 10-minute walks and pelvic tilts aid recovery.")
        ),

        // 7. Knee Stiffness & Reduced Mobility
        DiseaseCondition(
            id = "knee_stiffness",
            name = "Knee Stiffness & Reduced Mobility",
            shortName = "Knee Stiffness",
            clinicalSubtitle = "Chair-Supported Joint Lubrication & Quadriceps Activation",
            categoryTag = "Musculoskeletal",
            overview = "Reduced knee flexion/extension comfort after inactivity or aging. Low-load synovial movement and quadriceps engagement support joint confidence.",
            rootCauseAyurvedic = "Janu Stambha: diminished joint lubrication.",
            targetSymptoms = listOf("Heaviness or tightness when bending knees", "Hesitation on stairs"),
            accentHex = 0xFFC98A2E,
            asanas = listOf(lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Extend and flex both ankles and knees gently 10 times before standing up from prolonged sitting.")
        ),

        // 8. Postural Discomfort (Prolonged Sitting)
        DiseaseCondition(
            id = "postural_discomfort",
            name = "Postural Discomfort (Desk & Prolonged Sitting)",
            shortName = "Desk Posture",
            clinicalSubtitle = "Thoracic Extension, Hip Flexor Opening & Micro-Breaks",
            categoryTag = "Musculoskeletal",
            overview = "Rounded shoulders, tight hip flexors, and mid-back fatigue from prolonged computer or mobile device use.",
            rootCauseAyurvedic = "Asana-Janya Vata: stagnation from static seated posture.",
            targetSymptoms = listOf("Mid-back ache between shoulder blades", "Tight front hips and forward head posture"),
            accentHex = 0xFF3A6B56,
            asanas = listOf(gentleChairNeckRelease, lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Follow the 30-2 rule: every 30 minutes of sitting, stand and stretch the chest for 2 minutes.")
        ),

        // 10. Mild Stress-Related Sleep Difficulties
        DiseaseCondition(
            id = "sleep_difficulties",
            name = "Mild Stress-Related Sleep Difficulties",
            shortName = "Sleep Support",
            clinicalSubtitle = "Evening Parasympathetic Down-Regulation & Yoga Nidra",
            categoryTag = "Mental Wellness",
            overview = "Difficulty unwinding or falling asleep due to mental hyperarousal and evening stress. Gentle restorative postures and slow extended-exhale breathing prepare the nervous system for sleep.",
            rootCauseAyurvedic = "Anidra: elevated Vata and Pitta disturbing Manovaha Srotas at night.",
            targetSymptoms = listOf("Racing mind at bedtime", "Unrefreshing or fragmented sleep"),
            accentHex = 0xFF284B5A,
            asanas = listOf(lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Practice 10 minutes of supine body-scan relaxation in bed with dim lights and zero screen exposure.")
        ),

        // 11. Difficulty Relaxing & Mental Fatigue
        DiseaseCondition(
            id = "mental_fatigue",
            name = "Difficulty Relaxing & Mental Fatigue",
            shortName = "Mental Fatigue",
            clinicalSubtitle = "Cognitive Rest, Grounding Breath & 5-Minute Reset Breaks",
            categoryTag = "Mental Wellness",
            overview = "Burnout, sensory overload, and difficulty switching off from constant cognitive demands.",
            rootCauseAyurvedic = "klama & Ojas depletion from excessive mental Rajas.",
            targetSymptoms = listOf("Brain fog and afternoon exhaustion", "Inability to sit quietly without restlessness"),
            accentHex = 0xFF426A5A,
            asanas = listOf(gentleChairNeckRelease),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Take a 5-minute eyes-closed diaphragmatic breathing break between afternoon tasks.")
        ),

        // 13. Tension-Type Headache
        DiseaseCondition(
            id = "tension_headache",
            name = "Tension-Type Headache",
            shortName = "Tension Headache",
            clinicalSubtitle = "Suboccipital, Jaw (TMJ) & Pericranial Myofascial Relaxation",
            categoryTag = "Neurological & Headache",
            overview = "Bilateral band-like head pressure often associated with neck muscle tension, eye strain, dehydration, or stress.",
            rootCauseAyurvedic = "Vataja Shiroroga: pericranial muscular tension.",
            targetSymptoms = listOf("Band-like tightness around forehead or back of skull", "Jaw clenching and neck tenderness"),
            accentHex = 0xFF2B6359,
            asanas = listOf(gentleChairNeckRelease),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Relax your tongue on the floor of your mouth and keep upper and lower teeth slightly parted throughout the day.")
        ),

        // 14. Type 2 Diabetes (Adjunct to Medical Care)
        DiseaseCondition(
            id = "type2_diabetes",
            name = "Type 2 Diabetes (Adjunct to Medical Care)",
            shortName = "Type 2 Diabetes",
            clinicalSubtitle = "Glycemic-Supportive Movement, Stress Reduction & Active Lifestyle",
            categoryTag = "Metabolic & Preventive",
            overview = "Supportive yoga and regular physical activity complement (and never replace) your physician's prescribed diabetes medications, glucose monitoring, and medical nutrition plan.",
            rootCauseAyurvedic = "Madhumeha / Prameha: Kapha-Vata metabolic imbalance.",
            targetSymptoms = listOf("Post-meal glucose fluctuations", "Fatigue and sedentary deconditioning", "Stress-induced glycemic spikes"),
            accentHex = 0xFF1F5C48,
            asanas = listOf(lumbarPelvicTilt, gentleChairNeckRelease),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf(
                "IMPORTANT: Continue all prescribed diabetes medicines and follow your clinician's dietary plan. Never undertake extreme fasting.",
                "A gentle 10–15 minute walk after meals supports healthy post-prandial glucose uptake."
            ),
            medicalReferralCriteria = listOf(
                "Episodes of hypoglycemia (shakiness, sweating, confusion) or severe hyperglycemia",
                "Foot sores, numbness in feet (neuropathy), or vision changes"
            )
        ),

        // 15. Obesity & Weight Management
        DiseaseCondition(
            id = "obesity_weight",
            name = "Obesity & Sustainable Weight Management",
            shortName = "Weight Wellness",
            clinicalSubtitle = "Joint-Friendly Low-Impact Movement, Mindful Eating & Sleep Rhythm",
            categoryTag = "Metabolic & Preventive",
            overview = "Joint-safe, progressive movement and stress-cortisol regulation that build stamina, protect knees and lower back, and cultivate sustainable eating and sleep habits.",
            rootCauseAyurvedic = "Sthaulya / Medoroga: Kapha and Meda Dhatu accumulation.",
            targetSymptoms = listOf("Joint loading during prolonged standing", "Low energy and breathlessness on exertion"),
            accentHex = 0xFFB85D3B,
            asanas = listOf(gentleChairNeckRelease, lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Focus on consistent daily movement, protein-rich home meals, and 7.5 hours of sleep rather than crash diets.")
        ),

        // 16. Hypertension (With Exercise Modifications)
        DiseaseCondition(
            id = "hypertension",
            name = "Hypertension (Blood Pressure-Safe Yoga Protocol)",
            shortName = "Hypertension",
            clinicalSubtitle = "Zero-Inversion, Zero-Retention Autonomic & Vascular Relaxation",
            categoryTag = "Metabolic & Preventive",
            overview = "Specially modified yoga protocol for individuals with high blood pressure—strictly avoiding head-below-heart inversions, isometric straining, and breath retention while emphasizing slow coherent breathing and relaxation as an adjunct to prescribed antihypertensive therapy.",
            rootCauseAyurvedic = "Raktagata Vata / Vyana Vata-Pitta imbalance.",
            targetSymptoms = listOf("Elevated blood pressure readings", "Stress-induced cardiovascular reactivity"),
            accentHex = 0xFF2B5B44,
            asanas = listOf(gentleChairNeckRelease, lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf(
                "IMPORTANT: Take your prescribed blood pressure medicines daily as directed by your doctor.",
                "Never hold your breath during postures, and always keep your head above the level of your heart."
            ),
            aggravatingMovementsToAvoid = listOf(
                "Strictly avoid headstands, shoulder stands, deep downward dog, and breath retention (Kumbhaka)",
                "Avoid rapid forceful breathing (Kapalabhati / Bhastrika)"
            ),
            medicalReferralCriteria = listOf(
                "Blood pressure >= 180/110 mmHg (do not exercise; seek immediate medical care)",
                "Chest pain, shortness of breath, severe headache, or visual changes"
            )
        ),

        // 17. General Deconditioning & Sedentary Lifestyle
        DiseaseCondition(
            id = "general_deconditioning",
            name = "General Deconditioning & Sedentary Lifestyle",
            shortName = "Active Start",
            clinicalSubtitle = "Gentle Progressive Mobility, Stamina, Balance & Habit Building",
            categoryTag = "Metabolic & Preventive",
            overview = "A welcoming, low-barrier beginner routine designed for people returning to movement after prolonged inactivity, illness, or desk-bound routines.",
            rootCauseAyurvedic = "Alasya & Kapha-Vata stagnation.",
            targetSymptoms = listOf("Stiffness, low stamina, and reduced balance confidence"),
            accentHex = 0xFF347456,
            asanas = listOf(gentleChairNeckRelease, lumbarPelvicTilt),
            pranayamas = listOf(diaphragmaticSlowBreath),
            dhyanas = listOf(somaticBodyScanDhyana),
            dietaryProtocol = standardBalancedIndianDiet,
            dailyRecoveryTips = listOf("Start with just 10 minutes a day; consistency matters far more than intensity.")
        )
    )
}
