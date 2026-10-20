package com.example.data.catalog

import com.example.data.model.DietItem
import com.example.data.model.DietaryProtocol
import com.example.data.model.DiseaseCondition
import com.example.data.model.MealSlot
import com.example.data.model.PracticeCategory
import com.example.data.model.TherapeuticPractice

object DiseaseCatalog {

    val conditions: List<DiseaseCondition> = listOf(
        // 1. CERVICAL & LUMBAR SPONDYLITIS
        DiseaseCondition(
            id = "cervical_lumbar_spondylitis",
            name = "Cervical & Lumbar Spondylitis",
            shortName = "Spondylitis",
            clinicalSubtitle = "Griva & Kati Graha • Spinal Disc & Facet Decompression",
            categoryTag = "Spine & Neuro-Ortho",
            overview = "A progressive degenerative condition of the cervical (C3–C7) and lumbar (L3–S1) intervertebral discs and facet joints causing axial stiffness, radicular nerve impingement, and paraspinal muscle spasm.",
            rootCauseAyurvedic = "Aggravated Vyana Vata in the Asthi-Majja Dhatu (bone & nerve tissue) dries synovial cushioning, leading to disc narrowing and nerve root irritation.",
            targetSymptoms = listOf(
                "Neck stiffness & occipital radiating pain",
                "Lower back ache aggravated by prolonged sitting",
                "Trapezius spasm & shoulder blade tightness",
                "Tingling or numbness along cervical/lumbar dermatomes"
            ),
            accentHex = 0xFF1F4E38,
            asanas = listOf(
                TherapeuticPractice(
                    id = "makarasana_crocodile",
                    title = "Makarasana (Supported Crocodile Pose)",
                    sanskritName = "मकरासन • Makarasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 3 mins × 2 rounds with diaphragmatic breathing",
                    difficulty = "Gentle / Acute Phase Safe",
                    anatomicalFocus = "Cervical C4–C7 & Lumbar L4–S1 Lordotic Curve Restoration",
                    breathingCue = "Inhale to expand lower ribs laterally against the mat; exhale to release lumbar paraspinal tension.",
                    stepByStepInstructions = listOf(
                        "Lie prone on a firm yoga mat with feet comfortably wide apart and toes pointing outward, heels dropping inward.",
                        "Bend both elbows, placing palms softly under your chin and jawline so the cervical spine rests in gentle, supported extension.",
                        "Keep elbows slightly ahead of the shoulders to avoid hyperextending the lower back; adjust elbow distance if lumbar tightness is felt.",
                        "Close your eyes and breathe rhythmically into the abdomen, feeling each inhalation gently traction the lumbar vertebrae."
                    ),
                    therapeuticBenefits = listOf(
                        "Restores natural cervical and lumbar physiological lordosis without axial load",
                        "Encourages anterior hydration of dehydrated intervertebral discs",
                        "Releases deep multifidus and suboccipital muscle guarding"
                    ),
                    contraindications = listOf(
                        "Avoid placing elbows too close to the chest if experiencing acute lumbar stenosis",
                        "Do not force neck extension if severe vertigo is present"
                    )
                ),
                TherapeuticPractice(
                    id = "ardha_bhujangasana_sphinx",
                    title = "Ardha Bhujangasana (Therapeutic Sphinx Pose)",
                    sanskritName = "अर्ध भुजंगासन • Ardha Bhujangasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 10,
                    holdAndReps = "Hold 30 seconds × 5 repetitions",
                    difficulty = "Gentle-Moderate",
                    anatomicalFocus = "Erector Spinae, Rhomboids, and Anterior Longitudinal Ligament",
                    breathingCue = "Inhale while gently drawing the sternum forward between forearms; exhale while softening shoulders down.",
                    stepByStepInstructions = listOf(
                        "From prone position, bring legs hip-width apart with the tops of the feet pressing lightly into the mat.",
                        "Slide forearms forward so elbows align directly underneath the shoulder joints, forearms parallel.",
                        "Inhale and lift the head and upper thoracic spine by pressing down through the forearms—keep the lower ribs resting on the mat.",
                        "Tuck the chin slightly (Jalandhara micro-alignment) to keep the back of the neck long; hold for 5 slow breaths before lowering."
                    ),
                    therapeuticBenefits = listOf(
                        "Strengthens deep spinal extensors supporting cervical and lumbar stability",
                        "Counteracts forward-head posture and thoracic kyphosis from desk work",
                        "Reduces posterior disc bulge pressure on spinal nerve roots"
                    ),
                    contraindications = listOf(
                        "Avoid full straight-arm Cobra during acute lumbar radiculopathy flares",
                        "Discontinue if sharp shooting pain radiates down the leg or arm"
                    )
                ),
                TherapeuticPractice(
                    id = "marjaryasana_bitilasana_spinal",
                    title = "Marjaryasana-Bitilasana (Micro-Segmented Cat-Cow)",
                    sanskritName = "मार्जरी-बितिलासन • Marjaryasana-Bitilasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "10 slow synchronized breath cycles",
                    difficulty = "Gentle",
                    anatomicalFocus = "Full Vertebral Column Articulation & Synovial Fluid Circulation",
                    breathingCue = "Inhale slowly for 4 seconds into gentle spinal extension; exhale for 6 seconds into mild flexion.",
                    stepByStepInstructions = listOf(
                        "Come onto hands and knees in tabletop position with wrists directly under shoulders and knees padded under hips.",
                        "On an inhalation, gently tilt the pelvis forward, allow the mid-back to soften, and gaze straight ahead (do not crunch the back of the neck).",
                        "On a slow exhalation, draw the navel lightly toward the spine and round the upper back between the shoulder blades while keeping the neck relaxed.",
                        "Keep the movement within a 60% pain-free range of motion to bathe the facet joints in synovial fluid."
                    ),
                    therapeuticBenefits = listOf(
                        "Mobilizes stiff thoracic segments to offload overworked cervical and lumbar hinges",
                        "Coordinates pelvic-lumbar rhythm and core transverse abdominis engagement",
                        "Relieves morning spinal rigidity"
                    ),
                    contraindications = listOf(
                        "For cervical spondylitis, never drop the head heavily downward or tilt the head sharply back",
                        "Use a folded blanket under knees if knee osteoarthritis co-exists"
                    )
                ),
                TherapeuticPractice(
                    id = "brahma_mudra_cervical",
                    title = "Brahma Mudra (Isometric & Slow Cervical Release)",
                    sanskritName = "ब्रह्म मुद्रा • Brahma Mudra",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 7,
                    holdAndReps = "4 directional movements × 3 ultra-slow rounds",
                    difficulty = "Gentle",
                    anatomicalFocus = "Sternocleidomastoid, Scalenes, Levator Scapulae & C1–C7 Facets",
                    breathingCue = "Exhale slowly with a soft 'Ahhh' or 'Mmm' sound while rotating to each side; inhale back to center.",
                    stepByStepInstructions = listOf(
                        "Sit tall in Sukhasana or upright on a firm chair with spine supported and shoulders relaxed.",
                        "Inhale at center; exhale over 6 seconds while turning the head smoothly to the right without lifting the chin.",
                        "Inhale slowly back to center; exhale over 6 seconds turning smoothly to the left.",
                        "Perform gentle lateral ear-to-shoulder releases with the opposite shoulder anchored down; never perform full 360° neck circles."
                    ),
                    therapeuticBenefits = listOf(
                        "Decompresses cervical nerve roots and relieves brachial plexus tension",
                        "Improves vertebral artery blood flow, easing cervicogenic dizziness",
                        "Releases chronic upper trapezius and scalene trigger points"
                    ),
                    contraindications = listOf(
                        "Strictly avoid rapid neck movements or backward head rolling",
                        "Keep eyes open if prone to cervical vertigo"
                    )
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "bhramari_spinal_resonance",
                    title = "Bhramari Pranayama (Acoustic Vagal & Muscle Spasm Release)",
                    sanskritName = "भ्रामरी प्राणायाम • Bhramari",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 8,
                    holdAndReps = "9 rounds with Shanmukhi or gentle ear tragus closure",
                    difficulty = "All Levels",
                    anatomicalFocus = "Cervical Fascia, Vagus Nerve & Paraspinal Muscle Spindle Tone",
                    breathingCue = "Inhale 4s through both nostrils → Exhale 8s with a steady low-pitched humming vibration.",
                    stepByStepInstructions = listOf(
                        "Sit comfortably with an elongated spine; if raising arms fatigues your neck, keep hands resting on thighs instead of raising elbows.",
                        "Keep lips gently closed with upper and lower teeth slightly parted and tongue tip resting behind the upper front teeth.",
                        "Inhale deeply through both nostrils into the lower ribcage.",
                        "Exhale slowly while creating a smooth, low-frequency humming sound like a bumblebee, feeling the acoustic vibration resonate down the cervical and lumbar spine."
                    ),
                    therapeuticBenefits = listOf(
                        "Nitric oxide and acoustic resonance directly reduce cervical paraspinal muscle spasm",
                        "Down-regulates central pain sensitization via parasympathetic vagal activation",
                        "Safe to practice even during acute spondylitic flare-ups"
                    ),
                    contraindications = listOf(
                        "Do not hold elbows lifted high if it strains the cervical or shoulder musculature—rest hands on knees"
                    )
                ),
                TherapeuticPractice(
                    id = "nadi_shodhana_spondylitis",
                    title = "Nadi Shodhana (Alternate Nostril Autonomic Balance)",
                    sanskritName = "नाडी शोधन • Nadi Shodhana",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 10,
                    holdAndReps = "12 cycles (4s Inhale : 6s Exhale, no breath retention initially)",
                    difficulty = "Gentle",
                    anatomicalFocus = "Ida & Pingala Nadis • Sympathetic Pain Loop Down-regulation",
                    breathingCue = "Inhale Left 4s → Exhale Right 6s → Inhale Right 4s → Exhale Left 6s.",
                    stepByStepInstructions = listOf(
                        "Support your right elbow with your left hand or a cushion on your lap so your neck and right trapezius remain completely relaxed.",
                        "Adopt Nasagra (Vishnu) Mudra, closing the right nostril gently with the thumb and inhaling smoothly through the left nostril for 4 counts.",
                        "Close the left nostril with the ring finger, release the right nostril, and exhale smoothly for 6 counts.",
                        "Inhale through the right nostril for 4 counts, switch, and exhale through the left nostril for 6 counts to complete one cycle."
                    ),
                    therapeuticBenefits = listOf(
                        "Reduces neurogenic inflammation and stress-induced muscular guarding",
                        "Enhances oxygenation to ischemic paraspinal tissues",
                        "Balances autonomic nervous system tone"
                    ),
                    contraindications = listOf(
                        "Avoid Kumbhaka (breath retention) if hypertension or acute radicular pain is present"
                    )
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "sushumna_prana_dharana",
                    title = "Sushumna Spinal Decompression Dhyana",
                    sanskritName = "सुषुम्ना प्राण धारणा • Sushumna Dhyana",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 12,
                    holdAndReps = "12-minute guided supine or supported seated meditation",
                    difficulty = "Restorative",
                    anatomicalFocus = "Central Spinal Canal (Sushumna) & Neuromuscular Pain Gating",
                    breathingCue = "Inhale visualizing warm golden space expanding between each vertebra; exhale releasing deep guarding.",
                    stepByStepInstructions = listOf(
                        "Lie in Savasana with a bolster under your knees (to flatten lumbar lordosis) and a thin folded towel supporting the natural curve of your neck.",
                        "Bring effortless awareness to the base of the sacrum (Muladhara) and trace the spinal column upward to the base of the skull (bindu).",
                        "With each inhalation, visualize a warm, golden stream of Prana gently creating cushioned space between every cervical and lumbar disc.",
                        "With each exhalation, mentally repeat 'Shanti, Release' as paraspinal muscles melt into the support of the mat."
                    ),
                    therapeuticBenefits = listOf(
                        "Interrupts chronic pain-spasm-pain neurological feedback loops",
                        "Promotes endorphin and GABA release for natural analgesia",
                        "Improves sleep onset in patients with nocturnal back or neck ache"
                    ),
                    contraindications = listOf(
                        "Ensure bolster support under knees so the lower back never arches uncomfortably"
                    )
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Vata-Shamaka & Asthi-Poshaka (Vata-Pacifying & Bone/Disc Nourishing)",
                corePrinciple = "In Ayurveda, Spondylitis is driven by Ruksha (dryness) and Sheeta (cold) qualities of aggravated Vata depleting the Shleshaka Kapha cushion inside intervertebral discs. Focus on warm, unctuous, anti-inflammatory foods rich in bioavailable calcium, magnesium, collagen precursors, and healthy lipids.",
                herbalRemedy = "Golden Dashamoola & Shallaki (Boswellia) Decoction + 1 cup warm A2 or Almond Milk with ½ tsp Turmeric, a pinch of Black Pepper, and ½ tsp Ashwagandha at bedtime.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Black Sesame Seeds (Til) & Moringa (Drumstick) Soup",
                        rationale = "Delivers bioavailable calcium, magnesium, and phosphorus to strengthen vertebral bone density and ligament resilience.",
                        activeCompoundOrQuality = "Snigdha (Unctuous) • Rich in Calcium & Zinc"
                    ),
                    DietItem(
                        name = "Garlic-Infused Warm Milk (Lashuna Ksheerapaka)",
                        rationale = "Classical Ayurvedic formulation for Kati Graha and Gridhrasi; reduces nerve root edema and Vata stiffness.",
                        activeCompoundOrQuality = "Allicin & Vata-Anulomana (Nerve Anti-inflammatory)"
                    ),
                    DietItem(
                        name = "Cow's Ghee / Cold-Pressed Sesame Oil & Soaked Walnuts",
                        rationale = "Lubricates connective fascia and supplies Omega-3 fatty acids to calm facet joint synovitis.",
                        activeCompoundOrQuality = "Butyrate & Alpha-Linolenic Acid"
                    ),
                    DietItem(
                        name = "Kulthi Dal (Horse Gram) & Moong Dal Khichdi with Ginger",
                        rationale = "Easy-to-digest protein that prevents Ama (metabolic toxins) and intestinal gas from aggravating lumbar Vata.",
                        activeCompoundOrQuality = "Deepana-Pachana • Anti-Ama Protein"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "Cold, Dry, Raw Salads & Refrigerated Beverages",
                        rationale = "Directly increases Vata dryness and vasoconstriction in paraspinal muscles.",
                        activeCompoundOrQuality = "Ruksha & Sheeta (Vata-Aggravating)"
                    ),
                    DietItem(
                        name = "Fermented Batter, White Flour (Maida) & Nightshades in Excess",
                        rationale = "Increases systemic gut-joint inflammation and morning spinal stiffness.",
                        activeCompoundOrQuality = "Abhishyandi (Channel-Blocking)"
                    ),
                    DietItem(
                        name = "Excessive Caffeine & Carbonated Drinks",
                        rationale = "Leaches calcium from vertebral bone matrix and heightens neuromuscular tension.",
                        activeCompoundOrQuality = "Sympathetic Stimulant & Bone Demineralizer"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("07:00 AM • Awakening", "Warm Methi-Ajwain Infusion + Soaked Almonds", "1 glass warm water steeped with ½ tsp fenugreek seeds + 5 peeled soaked almonds & 2 walnut halves."),
                    MealSlot("08:30 AM • Breakfast", "Spiced Ragi (Finger Millet) Porridge or Moong Chilla", "Warm calcium-rich Ragi malt with cardamom & dates, or savory green moong crepe with ginger-mint chutney."),
                    MealSlot("01:00 PM • Lunch", "Moringa-Vegetable Dal, Soft Roti with Ghee & Steamed Greens", "1 tsp pure ghee over warm rotis, drumstick (moringa) sambar/dal, and sautéed ridge gourd or spinach."),
                    MealSlot("07:30 PM • Early Dinner", "Light Ginger-Garlic Vegetable Soup & Moong Khichdi", "Warm, soupy khichdi tempered with cumin, hing (asafoetida), and turmeric at least 2 hours before sleep.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Keep computer monitors at eye level so cervical flexion never exceeds 10°.",
                "Take a 90-second micro-break every 35 minutes of sitting to stand and gently lengthen the spine.",
                "Sleep on a medium-firm mattress with a thin contoured pillow supporting the cervical curve."
            )
        ),

        // 2. OSTEOARTHRITIS (OA)
        DiseaseCondition(
            id = "osteoarthritis",
            name = "Osteoarthritis (Knee & Hip OA)",
            shortName = "Osteoarthritis",
            clinicalSubtitle = "Sandhivata • Articular Cartilage & Synovial Joint Preservation",
            categoryTag = "Joints & Mobility",
            overview = "Degenerative joint wear characterized by articular cartilage thinning, subchondral bone remodeling, crepitus, and peri-articular muscle weakness—most commonly affecting the tibiofemoral, patellofemoral, and hip joints.",
            rootCauseAyurvedic = "Known as Sandhivata: Vata accumulates inside weight-bearing joints (Janu Sandhi), drying up Shleshaka Kapha (synovial fluid) and causing painful friction and crepitus.",
            targetSymptoms = listOf(
                "Joint stiffness lasting <30 mins upon waking or after sitting",
                "Knee pain during stair descent or weight-bearing",
                "Audible joint crepitus (grating/clicking) on flexion",
                "Quadriceps & gluteal muscle deconditioning"
            ),
            accentHex = 0xFFC98A2E,
            asanas = listOf(
                TherapeuticPractice(
                    id = "pawanmuktasana_janu_chakra",
                    title = "Pawanmuktasana Part 1 (Seated Janu Naman & Ankle Pumps)",
                    sanskritName = "जानु नमन • Janu Naman (Joint-Freeing Series)",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 10,
                    holdAndReps = "12 slow repetitions per leg + 5s isometric quad hold",
                    difficulty = "Gentle / Zero Weight-Bearing",
                    anatomicalFocus = "Vastus Medialis Obliquus (VMO), Patellar Tracking & Synovial Pump",
                    breathingCue = "Inhale while extending the knee and engaging the kneecap; exhale while bending smoothly toward the chest.",
                    stepByStepInstructions = listOf(
                        "Sit in Dandasana (Staff Pose) with back supported against a wall and a rolled towel placed under the right knee.",
                        "Clasp hands under the right thigh just above the knee to unload joint compression.",
                        "Inhale and smoothly straighten the right knee, flexing the ankle toward you and contracting the quadriceps for 5 seconds.",
                        "Exhale and gently bend the knee without letting the heel drag on the floor; repeat 12 times per side."
                    ),
                    therapeuticBenefits = listOf(
                        "Circulates nutrient-rich synovial fluid across avascular articular cartilage without weight-bearing stress",
                        "Strengthens the Vastus Medialis (VMO) to stabilize patellar tracking",
                        "Reduces peri-articular edema and morning joint locking"
                    ),
                    contraindications = listOf(
                        "Never force full hyperextension or deep knee flexion past 90° if acute effusion (swelling) is present"
                    )
                ),
                TherapeuticPractice(
                    id = "setu_bandhasana_oa",
                    title = "Setu Bandhasana (Therapeutic Glute-Hamstring Bridge)",
                    sanskritName = "सेतु बन्धासन • Setu Bandhasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 15 seconds × 6 repetitions with yoga block between thighs",
                    difficulty = "Gentle-Moderate",
                    anatomicalFocus = "Gluteus Maximus, Hamstrings, Medial Quadriceps & Hip Extensors",
                    breathingCue = "Exhale as you press heels down and lift hips; breathe steadily while holding the bridge.",
                    stepByStepInstructions = listOf(
                        "Lie on your back with knees bent and feet hip-width apart, keeping ankles slightly ahead of the knees so knee angle is comfortable (~100°).",
                        "Place a soft yoga block or folded cushion between your inner thighs and gently squeeze it to activate the medial quadriceps.",
                        "Press evenly through both heels and lift the pelvis until thighs and torso form a gentle diagonal line.",
                        "Hold for 4–5 calm breaths while keeping the knees tracking directly over the second toe, then lower vertebra by vertebra."
                    ),
                    therapeuticBenefits = listOf(
                        "Strengthens hip abductors and extensors, reducing varus/valgus load on the medial knee compartment by up to 25%",
                        "Activates inner quad stabilizers via adductor-VMO co-contraction",
                        "Decompresses hip joint capsule"
                    ),
                    contraindications = listOf(
                        "Keep feet slightly further away from hips if closer foot placement causes anterior knee discomfort"
                    )
                ),
                TherapeuticPractice(
                    id = "supta_padangusthasana_strap",
                    title = "Supta Padangusthasana (Strap-Assisted Supine Hamstring Lengthening)",
                    sanskritName = "सुप्त पादांगुष्ठासन • Supta Padangusthasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 45 seconds per leg × 2 rounds",
                    difficulty = "Gentle",
                    anatomicalFocus = "Hamstring Complex, Gastrocnemius-Soleus & Posterior Knee Capsule",
                    breathingCue = "Inhale to press the heel gently toward the ceiling; exhale to relax the back of the knee.",
                    stepByStepInstructions = listOf(
                        "Lie supine with your left knee bent and foot flat on the mat to protect the lower back.",
                        "Loop a yoga strap or long cotton towel around the ball of your right foot and extend the right leg upward.",
                        "Keep the shoulders and elbows resting on the mat as you gently press the right heel up until a mild stretch is felt in the calf and hamstring.",
                        "Breathe deeply for 45 seconds, allowing posterior knee contractures to release so full extension becomes easier during walking."
                    ),
                    therapeuticBenefits = listOf(
                        "Releases tight hamstrings and calf muscles that increase patellofemoral joint compression",
                        "Restores terminal knee extension for a smoother, pain-free gait",
                        "Zero compressive load on knee or hip cartilage"
                    ),
                    contraindications = listOf(
                        "Keep a micro-bend in the raised knee if sciatic nerve tension is felt behind the knee"
                    )
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "anulom_vilom_oa",
                    title = "Anulom Vilom (Rhythmic Anti-Inflammatory Breathwork)",
                    sanskritName = "अनुलोम विलोम • Anulom Vilom",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 10,
                    holdAndReps = "10 minutes continuous slow rhythm (4s Inhale : 8s Exhale)",
                    difficulty = "All Levels",
                    anatomicalFocus = "Systemic Cytokine Modulation & Vata Pacification",
                    breathingCue = "Slow, noiseless inhalation for 4 counts; extended, unhurried exhalation for 8 counts.",
                    stepByStepInstructions = listOf(
                        "Sit on a chair or elevated cushion so your knees remain higher than or level with your ankles without deep bending.",
                        "Use Vishnu Mudra to alternate nostrils smoothly, keeping the breath completely silent and silky.",
                        "Double the length of your exhalation relative to inhalation to stimulate the anti-inflammatory cholinergic pathway."
                    ),
                    therapeuticBenefits = listOf(
                        "Lowers systemic inflammatory markers (IL-6 and TNF-alpha) associated with OA cartilage breakdown",
                        "Calms Vata dosha in the nervous and musculoskeletal systems",
                        "Improves pain tolerance and emotional resilience"
                    ),
                    contraindications = listOf(
                        "Always sit on a chair if sitting cross-legged on the floor stresses the medial meniscus"
                    )
                ),
                TherapeuticPractice(
                    id = "dirgha_three_part_breath",
                    title = "Dirgha Pranayama (Three-Part Complete Oxygenation Breath)",
                    sanskritName = "दीर्घ प्राणायाम • Dirgha Pranayama",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 7,
                    holdAndReps = "12 deep three-chamber breaths",
                    difficulty = "Gentle",
                    anatomicalFocus = "Diaphragm, Intercostals & Lymphatic Return from Lower Extremities",
                    breathingCue = "Inhale Abdomen → Lower Ribs → Collarbones; Exhale Collarbones → Ribs → Abdomen.",
                    stepByStepInstructions = listOf(
                        "Lie comfortably or sit supported in a chair with one hand on the abdomen and one on the ribcage.",
                        "Inhale smoothly in three continuous waves: expanding the belly, widening the ribcage, and gently lifting the upper chest.",
                        "Exhale slowly from top to bottom, feeling the thoracic pump assist venous and lymphatic drainage from swollen lower-limb joints."
                    ),
                    therapeuticBenefits = listOf(
                        "Acts as a thoraco-abdominal lymphatic pump to reduce lower-limb fluid stagnation and joint puffiness",
                        "Enhances cellular oxygenation for tissue repair"
                    ),
                    contraindications = listOf("Never strain or force lung capacity")
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "prana_dharana_joint_warmth",
                    title = "Janu Prana Dharana (Golden Synovial Warmth Meditation)",
                    sanskritName = "प्राण धारणा • Prana Dharana",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 10,
                    holdAndReps = "10-minute guided healing focus on affected joints",
                    difficulty = "Restorative",
                    anatomicalFocus = "Cortical Pain Matrix & Peri-Articular Vasodilation",
                    breathingCue = "Inhale drawing healing warmth into the joint space; exhale dissolving stiffness.",
                    stepByStepInstructions = listOf(
                        "Rest in a supported reclining posture with warm palms placed lightly over both kneecaps (or hip joints).",
                        "Feel the natural infrared warmth of your hands penetrating into the joint capsule.",
                        "With every inhalation, visualize luminous, golden-olive synovial fluid cushioning and lubricating the joint surfaces.",
                        "With every exhalation, release unconscious muscular bracing around the joint."
                    ),
                    therapeuticBenefits = listOf(
                        "Local hand warmth and focused attention promote peri-articular microcirculation",
                        "Reduces kinesiophobia (fear of movement) and central pain amplification"
                    ),
                    contraindications = listOf("If joint is acutely hot/red from inflammatory flare, visualize cool moonlight instead of warmth")
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Sandhivata Shamaka (Warm, Lubricating & Anti-Inflammatory)",
                corePrinciple = "Preserve articular cartilage and replenish Shleshaka Kapha by combining potent natural COX-2 inhibitors (Curcumin, Gingerols, Boswellic acids) with healthy unctuous fats while maintaining optimal body weight to unload knee joints.",
                herbalRemedy = "Shallaki (Boswellia serrata) + Guduchi + Dry Ginger (Shunthi) warm infusion twice daily after meals.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Fresh Turmeric Root, Black Pepper & Virgin Coconut/Sesame Oil",
                        rationale = "Piperine boosts curcumin bioavailability by 2000%, inhibiting MMP enzymes that degrade joint cartilage.",
                        activeCompoundOrQuality = "Curcumin + Piperine • Chondroprotective"
                    ),
                    DietItem(
                        name = "Methi (Fenugreek) Sprouts & Warm Ginger Broth",
                        rationale = "Reduces morning joint stiffness and synovial inflammation while supporting metabolic weight balance.",
                        activeCompoundOrQuality = "Diosgenin & Gingerols"
                    ),
                    DietItem(
                        name = "Flaxseeds, Walnuts & Chia Seeds (Soaked)",
                        rationale = "Rich plant Omega-3s that suppress prostaglandin E2 inflammatory pathways inside osteoarthritic joints.",
                        activeCompoundOrQuality = "Omega-3 ALA & Mucilage Lubrication"
                    ),
                    DietItem(
                        name = "Stewed Apples, Papaya & Pomegranate",
                        rationale = "High in antioxidants (anthocyanins and papain) that protect chondrocytes from oxidative stress.",
                        activeCompoundOrQuality = "Polyphenols & Cartilage Antioxidants"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "Refined Sugar, Bakery Pastries & Deep-Fried Snacks",
                        rationale = "Forms Advanced Glycation End-products (AGEs) that embrittle collagen fibers in joint cartilage.",
                        activeCompoundOrQuality = "Pro-Glycation & Pro-Inflammatory"
                    ),
                    DietItem(
                        name = "Cold Curd/Yogurt at Night & Sour Fermented Pickles",
                        rationale = "Aggravates Kapha-Ama blockage in joint micro-channels, worsening morning swelling.",
                        activeCompoundOrQuality = "Abhishyandi (Edema-Promoting)"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("06:45 AM • Awakening", "Warm Ginger-Turmeric-Fenugreek Elixir", "Warm water infused with grated fresh ginger, raw turmeric, and 1 tsp soaked methi seeds."),
                    MealSlot("08:30 AM • Breakfast", "Warm Oats or Amaranth (Rajgira) Upma with Walnuts", "Cooked with curry leaves, cumin, grated carrot, and topped with 1 tbsp ground flaxseed."),
                    MealSlot("01:00 PM • Lunch", "Barley/Jowar Roti, Methi-Palak Greens & Moong Dal", "Served warm with 1 tsp sesame oil or cow's ghee and steamed bottle gourd (lauki)."),
                    MealSlot("07:00 PM • Dinner", "Anti-Inflammatory Pumpkin-Lentil Soup", "Light, warm dinner with turmeric, black pepper, and garlic to prevent nighttime joint stiffness.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Perform 5 minutes of warm sesame oil self-massage (Janu Abhyanga) in circular motions around the kneecap before a warm shower.",
                "Every 1 kg of healthy weight reduction removes 4 kg of compressive force from the knees on every step.",
                "Use chairs with higher seats and avoid deep squatting or sitting cross-legged on hard floors during flares."
            )
        ),

        // 3. FROZEN SHOULDER (ADHESIVE CAPSULITIS)
        DiseaseCondition(
            id = "frozen_shoulder",
            name = "Frozen Shoulder (Adhesive Capsulitis)",
            shortName = "Frozen Shoulder",
            clinicalSubtitle = "Apabahuka • Glenohumeral Capsule & Scapular Mobilization",
            categoryTag = "Shoulder & Upper Limb",
            overview = "Fibrotic thickening and contracture of the glenohumeral joint capsule and coracohumeral ligament, progressing through Freezing (painful), Frozen (stiff), and Thawing phases with restricted external rotation and abduction.",
            rootCauseAyurvedic = "Termed Apabahuka in Ayurveda: Vata and Kapha constrict the Sira and Snayu (ligaments & tendons) around the Amsa Sandhi (shoulder joint), restricting bahu-prasarana (arm elevation).",
            targetSymptoms = listOf(
                "Severe restriction in external rotation & overhead reach",
                "Dull, aching night pain in the outer deltoid region",
                "Scapular hiking (shrugging) when trying to lift the arm",
                "Difficulty fastening clothing behind the back"
            ),
            accentHex = 0xFFB85D3B,
            asanas = listOf(
                TherapeuticPractice(
                    id = "pendulum_wall_walk_asana",
                    title = "Codman Pendulum & Wall Finger-Climb Asana",
                    sanskritName = "बाहु चलन • Bahu Chalana (Capsular Traction)",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "90s gentle pendulum circles + 8 slow wall climbs",
                    difficulty = "Gentle / Freezing & Frozen Phase Safe",
                    anatomicalFocus = "Inferior & Anterior Glenohumeral Capsule Decompression",
                    breathingCue = "Exhale slowly as fingers walk one inch higher up the wall while keeping the shoulder blade dropped down.",
                    stepByStepInstructions = listOf(
                        "Phase 1 (Pendulum): Lean forward slightly supporting your unaffected hand on a table; let the affected arm hang completely dead-weight and use tiny torso sways to create gentle 6-inch circles.",
                        "Phase 2 (Wall Climb): Stand facing a wall at arm's reach; walk index and middle fingers slowly up the wall like a spider without shrugging your upper trapezius.",
                        "Pause at the point of mild stretch (never sharp pain), take 3 slow breaths into the armpit, and walk fingers slowly back down."
                    ),
                    therapeuticBenefits = listOf(
                        "Uses gravity distraction to open the contracted inferior glenohumeral fold",
                        "Re-educates upward rotation of the scapula without subacromial impingement",
                        "Gently stretches coracohumeral ligament adhesions"
                    ),
                    contraindications = listOf(
                        "Never force the arm violently past the pain barrier during the acute 'Freezing' stage"
                    )
                ),
                TherapeuticPractice(
                    id = "gomukhasana_strap_assisted",
                    title = "Modified Gomukhasana Arms (Towel/Strap-Assisted Rotation)",
                    sanskritName = "गोमुखासन बाहु • Gomukhasana Arms",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 25 seconds × 4 repetitions per side",
                    difficulty = "Moderate (Thawing & Frozen Phase)",
                    anatomicalFocus = "Subscapularis, Infraspinatus & Posterior-Inferior Capsule",
                    breathingCue = "Inhale to broaden the collarbones; exhale to softly lengthen through the strap.",
                    stepByStepInstructions = listOf(
                        "Sit or stand tall holding a yoga strap or soft towel in your unaffected top hand, draping it down behind your back.",
                        "Reach your affected hand gently behind your lower back (at waist or sacrum level) to hold the lower end of the strap.",
                        "Without arching your lower back or jutting your head forward, gently draw the strap upward only until a mild, tolerable stretch is felt.",
                        "Hold for 5 deep breaths, then slowly release tension before unclasping."
                    ),
                    therapeuticBenefits = listOf(
                        "Restores functional internal rotation needed for reaching behind the back",
                        "Opens anterior chest and pectoralis minor tightness that pulls the shoulder forward"
                    ),
                    contraindications = listOf(
                        "Keep strap slack generous during acute night-pain stages; never jerk the affected arm"
                    )
                ),
                TherapeuticPractice(
                    id = "garudasana_scapular_release",
                    title = "Modified Garudasana Arms (Hug-to-Eagle Posterior Capsule Stretch)",
                    sanskritName = "गरुड़ासन बाहु • Garudasana Arms",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 7,
                    holdAndReps = "Hold 30 seconds × 3 rounds",
                    difficulty = "Gentle",
                    anatomicalFocus = "Rhomboids, Infraspinatus, Teres Minor & Posterior Shoulder Capsule",
                    breathingCue = "Inhale directly into the space between your shoulder blades; exhale and soften the jaw and neck.",
                    stepByStepInstructions = listOf(
                        "Sit tall and cross your arms in front of your chest, placing hands on opposite shoulders in a warm self-hug (or intertwining forearms if comfortable).",
                        "Keep the shoulders dropped away from the ears while gently nudging the elbows 1 inch forward to protract the shoulder blades.",
                        "Breathe deeply into the upper back for 6 slow breaths, feeling the posterior shoulder capsule gently expand."
                    ),
                    therapeuticBenefits = listOf(
                        "Releases posterior capsule tightness that blocks horizontal adduction",
                        "Relieves secondary rhomboid and levator scapulae trigger points"
                    ),
                    contraindications = listOf(
                        "Use the self-hug modification if forearm wrapping causes anterior shoulder pinching"
                    )
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "thoracic_ujjayi_breath",
                    title = "Ujjayi Pranayama with Lateral Rib Expansion",
                    sanskritName = "उज्जायी प्राणायाम • Ujjayi",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 8,
                    holdAndReps = "10 slow cycles (5s Inhale : 7s Exhale)",
                    difficulty = "All Levels",
                    anatomicalFocus = "Serratus Anterior, Intercostals & Axillary Lymphatic Flow",
                    breathingCue = "Soft ocean-whisper breath expanding the upper ribs beneath the shoulder blades.",
                    stepByStepInstructions = listOf(
                        "Sit comfortably with a pillow propped under your affected elbow/forearm so the shoulder joint is 100% supported and unloaded.",
                        "Slightly constrict the back of the throat to produce a gentle, soothing ocean-wave sound on both inhale and exhale.",
                        "Direct the inhalation into the side ribs and axillary (armpit) region of the affected shoulder to mobilize the thoracic cage from within."
                    ),
                    therapeuticBenefits = listOf(
                        "Mobilizes the scapulothoracic rib bed from the inside out without painful arm elevation",
                        "Generates internal heat (Ushna) to soften fibrotic capsular tissue"
                    ),
                    contraindications = listOf("Keep throat constriction very light and effortless")
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "amsa_marma_relaxation",
                    title = "Amsa & Kakshadhara Marma Release Dhyana",
                    sanskritName = "अंस मर्म ध्यान • Amsa Marma Dhyana",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 10,
                    holdAndReps = "10-minute supine meditation with supported arm pillow",
                    difficulty = "Restorative",
                    anatomicalFocus = "Brachial Plexus, Subscapularis Guarding & Nocturnal Pain Relief",
                    breathingCue = "Exhale for 6 counts, consciously dropping the shoulder blade 2 millimeters deeper into the pillow.",
                    stepByStepInstructions = listOf(
                        "Lie on your back (or unaffected side) with a thick pillow supporting the affected arm slightly away from the body so the capsule is never stretched taut.",
                        "Bring awareness to the Amsa Marma (top of the shoulder) and Kakshadhara Marma (front of the shoulder joint).",
                        "With each exhalation, release the subconscious protective shrugging reflex that patients with frozen shoulder hold 24/7."
                    ),
                    therapeuticBenefits = listOf(
                        "Significantly reduces nocturnal deltoid ache and improves sleep continuity",
                        "Releases autonomic sympathetic vasoconstriction in the rotator cuff interval"
                    ),
                    contraindications = listOf("Never sleep directly on the affected shoulder")
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Vata-Kaphahara Protocol (Warm, Fibrosis-Softening & Anti-Stiffness)",
                corePrinciple = "Apabahuka combines Vata dryness with Kapha fibrotic stiffness (Stambha). Therapeutic nutrition targets collagen cross-linking, glycemic control (since Frozen Shoulder is closely linked with insulin resistance), and warm herbal circulation.",
                herbalRemedy = "Rasna-Saptaka Kwath or warm Ginger-Cinnamon-Turmeric tea twice daily; topical warm Dhanwantharam Tailam application before bath.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Cinnamon, Fenugreek & Ginger Herbal Infusion",
                        rationale = "Improves insulin sensitivity and inhibits Advanced Glycation End-products (AGEs) that stiffen the shoulder capsule.",
                        activeCompoundOrQuality = "Cinnamaldehyde & Anti-Fibrotic"
                    ),
                    DietItem(
                        name = "Warm Sesame Oil, Garlic & Curry Leaves Tempering",
                        rationale = "Clears Kapha-Vata stagnation in upper-body micro-channels and supports tendon elasticity.",
                        activeCompoundOrQuality = "Ushna Virya (Warming Potency)"
                    ),
                    DietItem(
                        name = "Papaya, Pineapple (Bromelain) & Steamed Cruciferous Greens",
                        rationale = "Natural proteolytic enzymes help remodel fibrinous adhesions and reduce capsular synovitis.",
                        activeCompoundOrQuality = "Bromelain & Papain Enzymes"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "High-Glycemic Sugars, Sweetened Beverages & White Rice Excess",
                        rationale = "Blood sugar spikes accelerate collagen glycation and capsular contracture.",
                        activeCompoundOrQuality = "Collagen Cross-Linking Trigger"
                    ),
                    DietItem(
                        name = "Ice-Cold Water, Heavy Cheese & Deep-Fried Foods",
                        rationale = "Increases Kapha stiffness (Stambha) and lymphatic sluggishness around the shoulder girdle.",
                        activeCompoundOrQuality = "Guru & Sheeta (Stiffness-Inducing)"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("07:00 AM • Awakening", "Warm Cinnamon-Ginger-Lemon Water", "1 glass warm water with ¼ tsp Ceylon cinnamon and grated ginger + 4 soaked walnuts."),
                    MealSlot("08:30 AM • Breakfast", "Savory Vegetable Oats or Millet Pongal with Black Pepper", "Tempered with cumin, ginger, curry leaves, and ½ tsp turmeric."),
                    MealSlot("01:00 PM • Lunch", "Multigrain Roti, Garlic-Spinach Dal & Warm Vegetable Stir-Fry", "Balanced low-glycemic meal with moringa or fenugreek leaves."),
                    MealSlot("07:30 PM • Dinner", "Warm Spiced Moong Soup with Steamed Broccoli & Carrots", "Light, warm anti-inflammatory dinner taken early.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Apply a warm moist compress to the shoulder for 10 minutes immediately before practicing gentle pendulum and wall-walk exercises.",
                "When sleeping on your back, always place a pillow under the affected elbow so the humerus doesn't drop backward and strain the anterior capsule.",
                "Avoid carrying heavy bags on the affected shoulder."
            )
        ),

        // 4. ANXIETY & AUTONOMIC DYSREGULATION
        DiseaseCondition(
            id = "anxiety",
            name = "Anxiety, Panic & Chronic Stress",
            shortName = "Anxiety",
            clinicalSubtitle = "Chittodvega • Vagal Tone & HPA-Axis Neuro-Regulation",
            categoryTag = "Mind & Nervous System",
            overview = "Hyperarousal of the sympathetic nervous system and hypothalamic-pituitary-adrenal (HPA) axis presenting with racing thoughts, chest tightness, shallow thoracic breathing, palpitations, and restless insomnia.",
            rootCauseAyurvedic = "Known as Chittodvega: aggravated Prana Vata and Sadhaka Pitta disturb Manovaha Srotas (channels of the mind), depleting Ojas and elevating Rajas.",
            targetSymptoms = listOf(
                "Racing thoughts, anticipatory worry & mental restlessness",
                "Shallow chest breathing & elevated resting heart rate",
                "Solar plexus knot, jaw clenching & neck tension",
                "Difficulty falling or staying asleep"
            ),
            accentHex = 0xFF347456,
            asanas = listOf(
                TherapeuticPractice(
                    id = "viparita_karani_legs_up",
                    title = "Viparita Karani (Supported Legs-Up-The-Wall Pose)",
                    sanskritName = "विपरीत करणी • Viparita Karani",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 10,
                    holdAndReps = "Hold 8–10 minutes with slow abdominal breathing",
                    difficulty = "Restorative",
                    anatomicalFocus = "Carotid Baroreceptors, Vagus Nerve & Venous Return",
                    breathingCue = "Inhale 4 counts into the soft belly; exhale 7 counts letting the chest and shoulders sink.",
                    stepByStepInstructions = listOf(
                        "Sit sideways close to a wall with a folded blanket or bolster placed 4 inches from the baseboard.",
                        "Swing your legs smoothly up onto the wall as you lower your shoulders and head onto the mat, resting your pelvis on the folded blanket.",
                        "Open your arms out to a relaxed 'T' or cactus shape with palms facing upward, or rest one palm on the heart and one on the belly.",
                        "Close your eyes and stay for 8–10 minutes, allowing gravity to trigger the baroreceptor reflex that naturally slows heart rate."
                    ),
                    therapeuticBenefits = listOf(
                        "Activates aortic and carotid baroreceptors, immediately signaling the brainstem to lower heart rate and blood pressure",
                        "Shifts autonomic balance from sympathetic fight-or-flight to parasympathetic rest-and-digest",
                        "Relieves adrenal fatigue and lower-limb heaviness"
                    ),
                    contraindications = listOf(
                        "Keep pelvis flat on the mat (without a high bolster) if unmanaged glaucoma or hiatal hernia is present"
                    )
                ),
                TherapeuticPractice(
                    id = "balasana_supported_child",
                    title = "Salamba Balasana (Bolster-Supported Child's Pose)",
                    sanskritName = "सालम्ब बालासन • Salamba Balasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 7,
                    holdAndReps = "Hold 4 minutes (turning head gently halfway)",
                    difficulty = "Restorative",
                    anatomicalFocus = "Posterior Ribcage, Adrenal Region & Enteric Nervous System",
                    breathingCue = "Breathe into the back of the lower ribs, feeling the kidneys and adrenals rise and fall.",
                    stepByStepInstructions = listOf(
                        "Kneel on a soft mat with big toes touching and knees separated wide enough to accommodate a long bolster or stack of pillows between your thighs.",
                        "Exhale and fold your torso forward over the bolster, resting your torso and one cheek completely on the support.",
                        "Let your arms drape forward in complete surrender; switch cheek direction after 2 minutes for balanced neck alignment."
                    ),
                    therapeuticBenefits = listOf(
                        "Creates neuroception of safety by protecting the vulnerable anterior torso and solar plexus",
                        "Releases diaphragm constriction and encourages posterior lung expansion"
                    ),
                    contraindications = listOf(
                        "Place an extra cushion between calves and thighs if knee flexion is sensitive"
                    )
                ),
                TherapeuticPractice(
                    id = "supta_baddha_konasana_anxiety",
                    title = "Supta Baddha Konasana (Supported Reclined Bound Angle)",
                    sanskritName = "सुप्त बद्ध कोणासन • Supta Baddha Konasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 5–8 minutes with cushions under outer knees",
                    difficulty = "Restorative",
                    anatomicalFocus = "Respiratory Diaphragm, Psoas Major & Cardiac Plexus",
                    breathingCue = "Inhale softening the solar plexus; exhale releasing psoas tension.",
                    stepByStepInstructions = listOf(
                        "Lie back over a lengthwise bolster supporting your spine and head, bringing the soles of the feet together.",
                        "Prop cushions or yoga blocks under both outer thighs so there is zero groin strain.",
                        "Allow the chest and respiratory diaphragm to open effortlessly while breathing slowly."
                    ),
                    therapeuticBenefits = listOf(
                        "Releases the iliopsoas ('muscle of the soul'), which chronically contracts during stress and anxiety",
                        "Opens restricted intercostal breathing mechanics"
                    ),
                    contraindications = listOf("Always support outer thighs with cushions to avoid sacroiliac strain")
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "chandra_bhedana_cooling",
                    title = "Chandra Bhedana & Extended-Exhale Nadi Shodhana",
                    sanskritName = "चन्द्र भेदन • Chandra Bhedana (Lunar Breath)",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 8,
                    holdAndReps = "12 cycles (Inhale Left 4s → Exhale Right 8s)",
                    difficulty = "All Levels",
                    anatomicalFocus = "Ida Nadi (Parasympathetic Right-Hemisphere Activation)",
                    breathingCue = "Inhale exclusively through the Left Nostril for 4s → Exhale through the Right Nostril for 8s.",
                    stepByStepInstructions = listOf(
                        "Sit comfortably with spine tall and jaw relaxed.",
                        "Close the right nostril with your right thumb and inhale slowly, coolly through the left nostril for 4 seconds.",
                        "Close the left nostril with your ring finger, open the right nostril, and exhale twice as slowly for 8 seconds.",
                        "Repeat for 12 rounds to selectively stimulate the cooling, calming Ida Nadi pathway."
                    ),
                    therapeuticBenefits = listOf(
                        "Rapidly increases Heart Rate Variability (HRV) and vagal brake efficiency",
                        "Calms acute panic, palpitations, and evening mental overactivity"
                    ),
                    contraindications = listOf("Never hold the breath if feeling acutely anxious—keep the breath flowing smoothly")
                ),
                TherapeuticPractice(
                    id = "bhramari_anxiety_relief",
                    title = "Bhramari Pranayama (Vagal Acoustic Resonance)",
                    sanskritName = "भ्रामरी प्राणायाम • Bhramari",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 7,
                    holdAndReps = "11 rounds of long humming exhalations",
                    difficulty = "All Levels",
                    anatomicalFocus = "Auricular & Pharyngeal Branches of the Vagus Nerve",
                    breathingCue = "Inhale 4s → Humming Exhale 8–10s.",
                    stepByStepInstructions = listOf(
                        "Gently close the ear tragus with your index fingers and soften your gaze inward toward the brow center.",
                        "Inhale smoothly through the nose and exhale with a steady, resonant 'Mmmmm' vibration.",
                        "Pause for one natural breath after each round to notice the deep stillness in the mind."
                    ),
                    therapeuticBenefits = listOf(
                        "Shifts brainwave activity from high-beta anxiety patterns toward calm alpha and theta waves",
                        "Stimulates pharyngeal vagal fibers via vocal cord vibration"
                    ),
                    contraindications = listOf("Practice gently without forcing exhalation length")
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "yoga_nidra_nsdr",
                    title = "Yoga Nidra (61-Point Neuro-Somatic Deep Rest)",
                    sanskritName = "योग निद्रा • Yoga Nidra",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 15,
                    holdAndReps = "15-minute guided rotation of consciousness",
                    difficulty = "Restorative",
                    anatomicalFocus = "Prefrontal Cortex, Amygdala & Thalamic Gating",
                    breathingCue = "Effortless natural breath; observe the belly rising 'So' and falling 'Hum'.",
                    stepByStepInstructions = listOf(
                        "Lie warmly covered in Savasana with palms turned upward and eyes softly closed.",
                        "Silently affirm your Sankalpa (healing intention): 'My nervous system is safe, grounded, and at peace.'",
                        "Rotate awareness systematically from the brow center, throat, right hand fingers, arm, torso, leg, left side, and heart center.",
                        "Rest in witness awareness (Sakshi Bhava), allowing anxious thoughts to pass like clouds across a vast sky."
                    ),
                    therapeuticBenefits = listOf(
                        "Clinically shown to reduce serum cortisol and boost striatal dopamine and GABA",
                        "Provides restorative nervous system recovery equivalent to deep slow-wave sleep"
                    ),
                    contraindications = listOf("None; safe for all patients")
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Vata-Pitta Shamaka & Ojas-Building Sattvic Protocol",
                corePrinciple = "Stabilize erratic Prana Vata and blood glucose fluctuations—which mimic and trigger adrenaline surges—through warm, grounding, magnesium- and tryptophan-rich Sattvic meals taken at regular times.",
                herbalRemedy = "Brahmi (Bacopa), Shankhpushpi & Ashwagandha warm chamomile/cardamom infusion 45 minutes before sleep.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Soaked Almonds, Pumpkin Seeds & Medjool Dates",
                        rationale = "Rich in intracellular magnesium and L-tryptophan to support serotonin and GABA synthesis.",
                        activeCompoundOrQuality = "Medhya (Nervine Tonic) • Magnesium & Tryptophan"
                    ),
                    DietItem(
                        name = "Warm A2 Milk or Oat Milk with Nutmeg (Jaiphal), Saffron & Cardamom",
                        rationale = "Myristicin in nutmeg and crocin in saffron calm nighttime Vata restlessness and deepen sleep.",
                        activeCompoundOrQuality = "Nidrajanana (Natural Sleep Promoter)"
                    ),
                    DietItem(
                        name = "Ghee-Tempered Moong Khichdi & Root Vegetables (Sweet Potato, Ash Gourd)",
                        rationale = "Petha (Ash Gourd / Kushmanda) is revered in Ayurveda as a premier Medhya Rasayana for cooling anxious Pitta-Vata.",
                        activeCompoundOrQuality = "Madhura Vipaka • Grounding & Stabilizing"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "Coffee, Energy Drinks & Empty-Stomach Tea",
                        rationale = "Directly triggers adenosine receptor blockade, cortisol release, and sympathetic tachycardia.",
                        activeCompoundOrQuality = "Rajasic Sympathetic Stimulant"
                    ),
                    DietItem(
                        name = "Skipped Meals, Cold Dry Crackers & Ultra-Processed Snacks",
                        rationale = "Reactive hypoglycemia triggers epinephrine release, directly provoking panic symptoms.",
                        activeCompoundOrQuality = "Vata-Provoking Glycemic Spike"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("07:15 AM • Awakening", "Ash Gourd (Petha) Juice or Warm Cardamom Water + 7 Soaked Almonds", "Stabilizes morning cortisol awakening response and nourishes nerve myelin."),
                    MealSlot("08:30 AM • Breakfast", "Warm Spiced Oatmeal with Stewed Apple, Ghee & Pumpkin Seeds", "Slow-release complex carbohydrates to keep blood sugar and mood steady."),
                    MealSlot("01:00 PM • Lunch", "Sattvic Thali: Soft Roti, Yellow Moong Dal, Steamed Greens & Sweet Potato", "Eaten mindfully without screens in a calm setting."),
                    MealSlot("07:30 PM • Dinner", "Comforting Vegetable-Barley Soup or Khichdi + Bedtime Nutmeg Milk", "Warm, grounding meal followed at 9:30 PM by warm milk with a pinch of nutmeg & saffron.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Massage the soles of your feet with warm sesame oil or Brahmi oil (Pada Abhyanga) for 3 minutes before bed to ground upward-moving Prana Vata.",
                "Eliminate caffeine after 11:00 AM and avoid doomscrolling within 60 minutes of sleep.",
                "Use the 4-8 breath ratio whenever you feel an acute wave of overwhelm."
            )
        ),

        // 5. CHRONIC MIGRAINE & TENSION HEADACHE
        DiseaseCondition(
            id = "migraine",
            name = "Chronic Migraine & Tension Headache",
            shortName = "Migraine",
            clinicalSubtitle = "Ardhavabhedaka & Suryavarta • Trigeminal-Vascular & Pitta Cooling",
            categoryTag = "Neurovascular & Head",
            overview = "Recurrent neurovascular headache disorder involving trigeminovascular activation, cranial vasodilation, photophobia, nausea, and pericranial/suboccipital myofascial trigger points.",
            rootCauseAyurvedic = "Correlates to Ardhavabhedaka and Suryavarta: Pitta and Vata vitiate Rakta Dhatu in the cranial vessels, often triggered by hyperacidity (Amlapitta), bright heat, fasting, or sleep disruption.",
            targetSymptoms = listOf(
                "Unilateral or bilateral throbbing temple/orbital headache",
                "Sensitivity to light (photophobia) & sound (phonophobia)",
                "Suboccipital & jaw (TMJ) prodromal tightness",
                "Acidity, nausea, or visual aura preceding attacks"
            ),
            accentHex = 0xFF2B6359,
            asanas = listOf(
                TherapeuticPractice(
                    id = "setu_bandhasana_bolster_migraine",
                    title = "Supported Setu Bandhasana with Head Cooling Alignment",
                    sanskritName = "सालम्ब सेतु बन्धासन • Supported Bridge",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 5 minutes with bolster under sacrum",
                    difficulty = "Restorative",
                    anatomicalFocus = "Suboccipital Triangle, Jalandhara Chin Lock & Cranial Venous Drainage",
                    breathingCue = "Slow, cool inhalation through the nose; soft exhalation releasing temple and jaw tension.",
                    stepByStepInstructions = listOf(
                        "Lie on your back with knees bent, lift the hips gently, and slide a bolster or firm stack of blankets under the flat sacrum.",
                        "Allow the back of the skull and neck to lengthen along the mat so the chin rests slightly lower than the sternum (gentle Jalandhara effect).",
                        "Place a cool folded eye pillow over your eyes and rest for 5 minutes."
                    ),
                    therapeuticBenefits = listOf(
                        "Gently traction-releases the suboccipital muscles where the greater occipital nerve exits",
                        "Calms trigeminal-cervical nucleus convergence without increasing intracranial pressure"
                    ),
                    contraindications = listOf(
                        "Do not practice headstands, full inversions, or unsupported downward folds during an active throbbing migraine"
                    )
                ),
                TherapeuticPractice(
                    id = "shashankasana_bolster_hare",
                    title = "Supported Shashankasana (Forehead-Supported Hare Pose)",
                    sanskritName = "शशांकासन • Shashankasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 7,
                    holdAndReps = "Hold 4 minutes with stacked blocks/cushion under forehead",
                    difficulty = "Gentle",
                    anatomicalFocus = "Supraorbital Nerve, Frontalis Muscle & Cervical Paraspinals",
                    breathingCue = "Breathe softly; feel gentle pressure on the brow center (Ajna) quieting vascular throbbing.",
                    stepByStepInstructions = listOf(
                        "Sit on your heels (with a cushion between hips and heels) and place a high bolster or wrapped yoga block in front of you.",
                        "Fold forward only partway so your head stays level with or slightly above your heart, resting your forehead (between the eyebrows) firmly on the soft support.",
                        "Unclench your molars, soften your tongue, and breathe calmly for 4 minutes."
                    ),
                    therapeuticBenefits = listOf(
                        "Gentle acupressure on the supraorbital and Sthapani Marma point calms trigeminal nerve firing",
                        "Relieves prodromal neck stiffness that triggers 70% of migraine episodes"
                    ),
                    contraindications = listOf(
                        "Keep the head elevated on a high support so blood does not rush to the head"
                    )
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "sheetali_cooling_breath",
                    title = "Sheetali & Sheetkari (Cranial Pitta-Cooling Pranayama)",
                    sanskritName = "शीतली प्राणायाम • Sheetali",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 7,
                    holdAndReps = "9 slow cooling breaths",
                    difficulty = "Gentle",
                    anatomicalFocus = "Hypothalamic Thermoregulation & Cranial Vasomotor Tone",
                    breathingCue = "Inhale cool air over curled tongue for 4s → Close mouth & Exhale through nostrils for 6s.",
                    stepByStepInstructions = listOf(
                        "Sit comfortably in a dim, quiet room with spine upright and shoulders dropped.",
                        "Extend the tongue slightly and curl the sides upward like a tube (or gently part lips with teeth lightly together in Sheetkari).",
                        "Inhale slowly through the curled tongue, feeling a refreshing cooling sensation across the palate and sinuses.",
                        "Draw the tongue in, close the mouth, and exhale slowly through both nostrils."
                    ),
                    therapeuticBenefits = listOf(
                        "Cools aggravated Pitta and Rakta in cranial blood vessels",
                        "Reduces prodromal heat, irritability, and gastric acidity linked to migraine onset"
                    ),
                    contraindications = listOf("Practice in a clean, warm room; avoid if experiencing acute sinusitis or asthma")
                ),
                TherapeuticPractice(
                    id = "bhramari_migraine",
                    title = "Soft-Pitch Bhramari (7-Round Sinus & Temple Soother)",
                    sanskritName = "भ्रामरी प्राणायाम • Bhramari",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 6,
                    holdAndReps = "7 gentle, low-volume humming rounds",
                    difficulty = "All Levels",
                    anatomicalFocus = "Cranial Nerves V & X, Paranasal Sinuses & Pericranial Fascia",
                    breathingCue = "Whisper-soft, low-frequency hum on exhalation.",
                    stepByStepInstructions = listOf(
                        "Inhale smoothly through both nostrils.",
                        "Exhale with a very soft, low-pitched humming vibration (avoid loud high-pitched humming during headache sensitivity).",
                        "Rest in silence for 30 seconds after the 7th round."
                    ),
                    therapeuticBenefits = listOf(
                        "Releases pericranial myofascial constriction and boosts cranial nitric oxide balance",
                        "Reduces migraine frequency when practiced daily as prophylaxis"
                    ),
                    contraindications = listOf("Keep humming volume very gentle if phonophobia is present")
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "ajna_chandra_dhyana",
                    title = "Chandra-Ajna Cooling Visualization & Temple Release",
                    sanskritName = "आज्ञा चन्द्र ध्यान • Ajna Dhyana",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 10,
                    holdAndReps = "10-minute dark-room neuro-sensory rest",
                    difficulty = "Restorative",
                    anatomicalFocus = "Visual Cortex Hyperexcitability & Trigeminal Ganglion",
                    breathingCue = "Inhale cool silver moonlight at the brow; exhale tension out through the soles of the feet.",
                    stepByStepInstructions = listOf(
                        "Lie in Savasana in a darkened room with a cool damp cloth or eye pillow across the forehead and eyes.",
                        "Consciously relax the temporalis muscles, the space behind the eyeballs, and the root of the tongue.",
                        "Visualize cooling silver moonlight bathing the brow center and drawing excess heat downward from the head toward the feet."
                    ),
                    therapeuticBenefits = listOf(
                        "Quiets occipital visual cortex hyperexcitability and photophobia",
                        "Aborts early prodromal tension before full vascular dilation occurs"
                    ),
                    contraindications = listOf("None")
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Pitta-Vata Shamaka (Cooling, Alkaline & Trigger-Free)",
                corePrinciple = "Prevent Amlapitta (gastric hyperacidity) and neurovascular triggers by never fasting or skipping meals, staying hydrated with coriander-fennel infused water, and eliminating tyramine/histamine migraine triggers.",
                herbalRemedy = "10 soaked black raisins + 1 tsp coriander seeds soaked overnight in 1 cup water, crushed and taken first thing in the morning; Pathyadi Kwath as advised.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Overnight-Soaked Black Raisins, Coriander (Dhaniya) & Fennel Water",
                        rationale = "Directly cools Pitta in the liver and stomach, preventing the gut-brain acid-migraine cascade.",
                        activeCompoundOrQuality = "Sheeta Virya (Cooling) • Anti-Amlapitta"
                    ),
                    DietItem(
                        name = "Soaked Almonds, Pumpkin Seeds & Spinach (Riboflavin + Magnesium)",
                        rationale = "Magnesium and Vitamin B2 (Riboflavin) stabilize mitochondrial energy in cortical neurons and prevent cortical spreading depression.",
                        activeCompoundOrQuality = "Magnesium & Riboflavin Prophylaxis"
                    ),
                    DietItem(
                        name = "Sweet Pomegranate, Coconut Water & Ghee on Warm Rice/Roti",
                        rationale = "Nasya (2 drops warm cow's ghee in each nostril) and dietary ghee pacify cranial Vata-Pitta.",
                        activeCompoundOrQuality = "Pitta-Vata Shamaka"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "Aged Cheese, Fermented Foods, MSG, Dark Chocolate & Red Wine",
                        rationale = "High in tyramine, phenylethylamine, and histamines that trigger cranial vasodilation.",
                        activeCompoundOrQuality = "Vasoactive Amine Triggers"
                    ),
                    DietItem(
                        name = "Fasting, Late Lunches & Excess Chili/Sour/Fried Foods",
                        rationale = "Skipping meals under midday sun (Suryavarta) spikes Pitta and triggers hypoglycemia headaches.",
                        activeCompoundOrQuality = "Pitta-Rakta Vitiating"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("07:00 AM • Awakening", "Soaked Black Raisins & Coriander-Fennel Cooling Infusion", "10 soaked black raisins + 5 peeled almonds to prevent morning acidity and hypoglycemia."),
                    MealSlot("08:30 AM • Breakfast", "Sweet Poha with Pomegranate or Moong Dal Idli with Coconut Chutney", "Light, cooling, non-fermented or freshly steamed breakfast."),
                    MealSlot("12:45 PM • On-Time Lunch", "Steamed Rice/Roti, Moong Dal with Ghee, Bottle Gourd (Lauki) & Mint-Coriander", "Never delay lunch past 1:15 PM to avoid midday Pitta peak headaches."),
                    MealSlot("07:30 PM • Dinner", "Soft Vegetable Khichdi with Fennel & 1 tsp Pure Ghee", "Calming, easy-to-digest meal supporting uninterrupted 8-hour sleep.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Maintain an identical wake-up time every day (even on weekends) to stabilize hypothalamic circadian rhythms.",
                "Never skip breakfast or delay lunch; carry soaked almonds and dates when traveling.",
                "Practice 1 drop of warm pure cow's ghee (Pratimarsha Nasya) inside each nostril in the morning to lubricate and cool cranial pathways."
            )
        ),

        // 6. SCIATICA & PIRIFORMIS SYNDROME
        DiseaseCondition(
            id = "sciatica",
            name = "Sciatica & Lumbar Radiculopathy",
            shortName = "Sciatica",
            clinicalSubtitle = "Gridhrasi • Sciatic Nerve Flossing & Piriformis Decompression",
            categoryTag = "Spine & Nerve Root",
            overview = "Compression or irritation of the L4–S3 nerve roots or sciatic nerve trunk beneath the piriformis muscle, causing radiating pain, paresthesia, or electric-like shooting sensations from the buttock down the posterior thigh and calf.",
            rootCauseAyurvedic = "Classical Gridhrasi in Ayurveda: Vata (alone or with Kapha) invades the Kandara (tendons/nerve pathways) running from Kati (lower back) and Sphik (gluteal region) down to the foot.",
            targetSymptoms = listOf(
                "Radiating pain from gluteal region down back of thigh/calf",
                "Deep piriformis tenderness when sitting on hard surfaces",
                "Pins-and-needles or numbness in the outer leg or foot",
                "Pain worsened by unsupported forward bending or coughing"
            ),
            accentHex = 0xFF5A4E3C,
            asanas = listOf(
                TherapeuticPractice(
                    id = "supta_kapotasana_reclined_pigeon",
                    title = "Supta Kapotasana (Supine Figure-Four Piriformis Release)",
                    sanskritName = "सुप्त कपोतासन • Supta Kapotasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 45 seconds × 3 rounds per side",
                    difficulty = "Gentle",
                    anatomicalFocus = "Piriformis, Gemelli & Deep Gluteal Sciatic Tunnel",
                    breathingCue = "Inhale into the outer hip; exhale softening the deep gluteal muscles around the sciatic nerve.",
                    stepByStepInstructions = listOf(
                        "Lie on your back with both knees bent and feet flat on the mat, keeping the sacrum heavy and neutral.",
                        "Cross your right ankle over the left thigh just below the knee, flexing the right foot gently to protect the knee joint.",
                        "Keep your head and shoulders resting on a pillow; if a gentle stretch is already felt in the right outer hip, stay here—or thread hands behind the left thigh to draw it slightly closer.",
                        "Breathe slowly for 45 seconds, then switch sides."
                    ),
                    therapeuticBenefits = listOf(
                        "Directly releases piriformis muscle spasm compressing the sciatic nerve trunk",
                        "Unloads the lumbar spine completely while opening external hip rotators"
                    ),
                    contraindications = listOf(
                        "Never lift your head and neck off the floor to reach your thigh—use a yoga strap around the supporting thigh instead"
                    )
                ),
                TherapeuticPractice(
                    id = "shalabhasana_alternating_locust",
                    title = "Ardha Shalabhasana (Alternating Single-Leg Locust)",
                    sanskritName = "अर्ध शलभासन • Ardha Shalabhasana",
                    category = PracticeCategory.ASANA,
                    durationMinutes = 8,
                    holdAndReps = "Hold 8 seconds per leg × 5 alternating rounds",
                    difficulty = "Gentle-Moderate",
                    anatomicalFocus = "Lumbar Multifidus, Gluteus Maximus & L4–S1 Disc Centralization",
                    breathingCue = "Inhale to lengthen and lift one leg 4 inches off the mat; exhale to lower smoothly.",
                    stepByStepInstructions = listOf(
                        "Lie prone with forehead resting on folded hands and pelvis anchored evenly on the mat.",
                        "On an inhalation, reach the right leg straight back (think length rather than height) and lift it just 4–6 inches off the mat without twisting the pelvis.",
                        "Hold for 2 breaths, lower on exhalation, and repeat with the left leg."
                    ),
                    therapeuticBenefits = listOf(
                        "Promotes anterior migration ('centralization') of L4–L5 and L5–S1 posterior disc bulges away from the sciatic nerve root",
                        "Reactivates inhibited gluteal and lumbar multifidus stabilizers"
                    ),
                    contraindications = listOf("Keep lift low and pain-free; never jerk both legs up high")
                )
            ),
            pranayamas = listOf(
                TherapeuticPractice(
                    id = "nadi_shodhana_sciatica",
                    title = "Supine or Chair-Supported Nadi Shodhana",
                    sanskritName = "नाडी शोधन • Nadi Shodhana",
                    category = PracticeCategory.PRANAYAMA,
                    durationMinutes = 8,
                    holdAndReps = "10 slow cycles (4s Inhale : 6s Exhale)",
                    difficulty = "All Levels",
                    anatomicalFocus = "Apana Vata Regulation & Neuropathic Pain Calming",
                    breathingCue = "Smooth, uninterrupted 4:6 breath ratio.",
                    stepByStepInstructions = listOf(
                        "Sit on a firm chair with hips slightly higher than knees (never sit slumped on a soft sofa during sciatica).",
                        "Practice 10 slow cycles of alternate nostril breathing to calm Apana Vata and reduce radicular nerve hyperexcitability."
                    ),
                    therapeuticBenefits = listOf(
                        "Harmonizes Apana Vata governing the pelvic and lower-limb nerve pathways",
                        "Reduces neuropathic pain perception"
                    ),
                    contraindications = listOf("Avoid sitting cross-legged on the floor if it aggravates gluteal nerve pain")
                )
            ),
            dhyanas = listOf(
                TherapeuticPractice(
                    id = "gridhrasi_nerve_pathway_dhyana",
                    title = "Apana-Prana Sciatic Pathway Relaxation Dhyana",
                    sanskritName = "गृध्रसी प्रशमन ध्यान • Sciatic Pathway Dhyana",
                    category = PracticeCategory.DHYANA,
                    durationMinutes = 10,
                    holdAndReps = "10-minute supine knees-elevated meditation",
                    difficulty = "Restorative",
                    anatomicalFocus = "L4–S3 Dermatomes & Central Pain Modulation",
                    breathingCue = "Exhale visualizing warm golden oil soothing the entire pathway from lower back to toes.",
                    stepByStepInstructions = listOf(
                        "Lie on your back with calves resting up on a chair seat or high bolster (90-90 psoas-unloading position).",
                        "Trace your awareness gently from the L4–S1 lumbar spine through the gluteal region, down the back of the thigh, calf, and sole of the foot.",
                        "With each exhalation, visualize warm, soothing Prana dissolving electrical irritation along the sciatic pathway."
                    ),
                    therapeuticBenefits = listOf(
                        "The 90-90 calf-elevated posture maximizes intervertebral foramen space at L4–S1",
                        "Calms dorsal horn nerve root hypersensitivity"
                    ),
                    contraindications = listOf("None")
                )
            ),
            dietaryProtocol = DietaryProtocol(
                doshaBalance = "Apana-Vata Anulomana (Gut-Clearing, Nerve-Nourishing & Anti-Edema)",
                corePrinciple = "In Ayurveda, constipation and pelvic gas directly increase intra-abdominal pressure on the lumbar plexus and aggravate Apana Vata in Gridhrasi. Keep digestion warm, regular, and lubricated while nourishing myelin with B-vitamins and healthy fats.",
                herbalRemedy = "Lashuna Ksheerapaka (2 crushed garlic cloves boiled in ½ cup milk + ½ cup water until reduced to ½ cup) in the evening; Nirgundi & Castor root decoction.",
                healingFoodsToInclude = listOf(
                    DietItem(
                        name = "Garlic, Asafoetida (Hing) & Ajwain Tempered Warm Lentils",
                        rationale = "Prevents colonic gas distension that presses against the lumbar spine and sciatic plexus.",
                        activeCompoundOrQuality = "Vata-Anulomana (Downward Vata Regulation)"
                    ),
                    DietItem(
                        name = "Warm Castor/Sesame Oil & Soaked Figs/Raisins",
                        rationale = "Ensures effortless morning bowel elimination, relieving Apana Vata stagnation in the pelvic basin.",
                        activeCompoundOrQuality = "Mridu Virechana & Nerve Lubrication"
                    ),
                    DietItem(
                        name = "Turmeric, Nutmeg & B12/Folate-Rich Greens",
                        rationale = "Supports myelin sheath regeneration and reduces perineural radicular inflammation.",
                        activeCompoundOrQuality = "Neurotrophic & Anti-Inflammatory"
                    )
                ),
                foodsToAvoid = listOf(
                    DietItem(
                        name = "Dry Chickpeas (Chana), Kidney Beans (Rajma) without Hing/Ginger & Cold Salads",
                        rationale = "Produces colonic Vata gas and worsens lumbar nerve root pressure.",
                        activeCompoundOrQuality = "Vata-Vardhaka"
                    ),
                    DietItem(
                        name = "Cold Drinks, White Bread & Refined Sugar",
                        rationale = "Promotes neuro-inflammation and slows nerve conduction recovery.",
                        activeCompoundOrQuality = "Pro-Inflammatory"
                    )
                ),
                dailyMealSchedule = listOf(
                    MealSlot("07:00 AM • Awakening", "Warm Water with 5 Soaked Black Raisins, 2 Figs & 5 Almonds", "Clears Apana Vata gently and nourishes nerve tissue."),
                    MealSlot("08:30 AM • Breakfast", "Warm Vegetable Daliya (Broken Wheat) or Moong Dal Cheela with Ginger", "High-soluble-fiber, warm, easily digestible breakfast."),
                    MealSlot("01:00 PM • Lunch", "Soft Wheat/Jowar Roti with Ghee, Garlic-Spinach Dal & Bottle Gourd", "Tempered with hing, cumin, and turmeric."),
                    MealSlot("07:30 PM • Dinner", "Light Moong Khichdi + Warm Garlic Milk (Lashuna Ksheerapaka)", "Reduces nighttime sciatic throbbing and morning leg stiffness.")
                )
            ),
            dailyRecoveryTips = listOf(
                "Strictly avoid forward bending with straight knees (such as touching toes or lifting weights off the floor).",
                "Never carry a wallet in your back pocket while sitting—it directly compresses the piriformis muscle onto the sciatic nerve.",
                "Rest in the 90-90 position (calves supported on a chair seat while lying on your back) for 10 minutes after work."
            )
        )
    ) + ExtendedConditionsCatalog.additionalConditions

    fun getConditionById(id: String): DiseaseCondition {
        return conditions.find { it.id == id } ?: conditions.first()
    }
}
