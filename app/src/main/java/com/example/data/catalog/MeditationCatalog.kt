package com.example.data.catalog

import com.example.data.model.MeditationTrack

object MeditationCatalog {
    val tracks: List<MeditationTrack> = listOf(
        MeditationTrack(
            id = "med_anxiety_grounding",
            title = "Anxiety Reduction & 5-4-3-2-1 Somatic Grounding",
            subtitle = "Prana Vata Pacification • Safe Nervous System Anchoring",
            targetPurpose = "Calms acute worry, racing thoughts, and sympathetic restlessness.",
            ambientSoundLabel = "Gentle Monsoon Rain & Tanpura Drone",
            writtenGuideSteps = listOf(
                "Feel the solid support of the chair or mat beneath your hips and feet.",
                "Notice 5 calm details in your surroundings, 4 points of physical contact, and 3 gentle sounds.",
                "Inhale for 4 seconds through the nose, and exhale softly for 6 seconds, letting the shoulders drop.",
                "Silently repeat: 'In this moment, my breath is steady and my body is supported.'"
            )
        ),
        MeditationTrack(
            id = "med_body_scan",
            title = "61-Point Progressive Body-Scan Relaxation (Yoga Nidra)",
            subtitle = "Shithilikarana • Deep Myofascial & Paraspinal Release",
            targetPurpose = "Releases unconscious muscle guarding around painful joints and spine.",
            ambientSoundLabel = "Himalayan Singing Bowl Harmonics",
            writtenGuideSteps = listOf(
                "Lie comfortably with a pillow under your knees or sit reclined with neck supported.",
                "Bring warm, effortless awareness to your forehead, jaw, tongue, and both shoulders.",
                "Scan slowly down the right arm, left arm, chest, upper back, lower back, hips, knees, and toes.",
                "With every exhalation, allow protective muscular bracing to soften by 10%."
            )
        ),
        MeditationTrack(
            id = "med_mindfulness_breath",
            title = "Sakshi Bhava (Non-Judgmental Mindfulness & Breath Observation)",
            subtitle = "Antar Mouna • Calm Witness Awareness",
            targetPurpose = "Builds mental clarity and reduces reactivity to stress triggers.",
            ambientSoundLabel = "Soft Bamboo Flute & Forest Breeze",
            writtenGuideSteps = listOf(
                "Sit tall or reclined with your hands resting softly on your lap.",
                "Observe the natural flow of breath at the nostrils without trying to force or change it.",
                "When thoughts arise, label them kindly as 'thinking' and return to the cool inhale and warm exhale."
            )
        ),
        MeditationTrack(
            id = "med_sleep_prep",
            title = "Nidra Sthapana (Evening Sleep Preparation & Unwinding)",
            subtitle = "Chandra-Nadi Activation • Restorative Sleep Transition",
            targetPurpose = "Reduces bedtime hyperarousal and supports deeper restorative sleep.",
            ambientSoundLabel = "Night Crickets & Gentle Stream",
            writtenGuideSteps = listOf(
                "Dim the room lights and settle into your sleeping posture with spine and neck aligned.",
                "Take 6 slow breaths with an extended 6-second exhalation.",
                "Mentally release the tasks of today; allow the weight of the body to sink into the mattress."
            )
        ),
        MeditationTrack(
            id = "med_pain_coping",
            title = "Pain Coping, Softening & Neuro-Somatic Comfort",
            subtitle = "Prana Dharana • Central Pain Gate Modulation",
            targetPurpose = "Helps soften secondary tension around osteoarthritic, spinal, or shoulder discomfort.",
            ambientSoundLabel = "Warm Low-Frequency Om Resonance",
            writtenGuideSteps = listOf(
                "Find your most comfortable supported posture using cushions or a chair.",
                "Notice the area of discomfort without fighting it, then widen your attention to a neutral, comfortable area like your hands.",
                "Breathe soothing warmth in and out, releasing the secondary frustration and muscle clenching around the area."
            )
        ),
        MeditationTrack(
            id = "med_focus_regulation",
            title = "Focus, Emotional Regulation & Equilibrium",
            subtitle = "Sattvic Balance • So-Hum Breath Synchronization",
            targetPurpose = "Restores emotional steadiness and concentration during busy workdays.",
            ambientSoundLabel = "Morning Temple Garden Ambience",
            writtenGuideSteps = listOf(
                "Inhale naturally while mentally hearing the syllable 'So'.",
                "Exhale smoothly while mentally hearing the syllable 'Hum'.",
                "Maintain an even, unhurried rhythm for the duration of the timer."
            )
        ),
        MeditationTrack(
            id = "med_five_min_break",
            title = "5-Minute Desk & Midday Relaxation Reset",
            subtitle = "Micro-Recovery • Eye, Jaw & Shoulder Unloading",
            targetPurpose = "Quick restorative break between work meetings or household tasks.",
            ambientSoundLabel = "Soft Chimes & Silence",
            writtenGuideSteps = listOf(
                "Step away from screens, rub palms together until warm, and cup them gently over closed eyes (Palming).",
                "Unclench your jaw, drop your shoulders away from your ears, and take 8 slow belly breaths.",
                "Open your eyes slowly and gaze at a distant object for 20 seconds."
            )
        )
    )
}
