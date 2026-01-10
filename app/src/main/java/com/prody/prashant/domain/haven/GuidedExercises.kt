package com.prody.prashant.domain.haven

/**
 * Complete implementations of all guided therapeutic exercises
 * Each exercise is designed to be self-contained and easy to follow
 */
object GuidedExercises {

    /**
     * Get a guided exercise by type
     */
    fun getExercise(type: ExerciseType): GuidedExercise {
        return when (type) {
            ExerciseType.BOX_BREATHING -> boxBreathing()
            ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> fourSevenEightBreathing()
            ExerciseType.GROUNDING_54321 -> grounding54321()
            ExerciseType.BODY_SCAN -> bodyScan()
            ExerciseType.THOUGHT_RECORD -> thoughtRecord()
            ExerciseType.EMOTION_WHEEL -> emotionWheel()
            ExerciseType.GRATITUDE_MOMENT -> gratitudeMoment()
            ExerciseType.PROGRESSIVE_RELAXATION -> progressiveRelaxation()
            ExerciseType.LOVING_KINDNESS -> lovingKindness()
        }
    }

    /**
     * Box Breathing (4-4-4-4)
     * Navy SEAL technique for calm and focus
     */
    private fun boxBreathing(): GuidedExercise {
        val pattern = BreathingPattern(
            inhaleSeconds = 4,
            holdInSeconds = 4,
            exhaleSeconds = 4,
            holdOutSeconds = 4,
            cycles = 6
        )

        return GuidedExercise(
            type = ExerciseType.BOX_BREATHING,
            title = "Box Breathing",
            introMessage = "Box breathing is a powerful technique used by Navy SEALs to stay calm under pressure. We'll breathe in a square pattern: inhale for 4, hold for 4, exhale for 4, hold for 4. Let's begin.",
            durationSeconds = 240,
            steps = listOf(
                ExerciseStep(
                    instruction = "Find a comfortable position and close your eyes if that feels safe.",
                    durationSeconds = 10,
                    audioGuide = "Take a moment to settle in. You're safe here."
                ),
                ExerciseStep(
                    instruction = "Breathe in slowly through your nose for 4 seconds",
                    durationSeconds = 4,
                    breathingPattern = pattern,
                    visualCue = VisualCue.ExpandingCircle,
                    audioGuide = "Breathe in... 2... 3... 4"
                ),
                ExerciseStep(
                    instruction = "Hold your breath gently for 4 seconds",
                    durationSeconds = 4,
                    visualCue = VisualCue.StaticCircle,
                    audioGuide = "Hold... 2... 3... 4"
                ),
                ExerciseStep(
                    instruction = "Exhale slowly through your mouth for 4 seconds",
                    durationSeconds = 4,
                    visualCue = VisualCue.ContractingCircle,
                    audioGuide = "Breathe out... 2... 3... 4"
                ),
                ExerciseStep(
                    instruction = "Hold empty for 4 seconds",
                    durationSeconds = 4,
                    visualCue = VisualCue.StaticCircle,
                    audioGuide = "Hold... 2... 3... 4"
                ),
                ExerciseStep(
                    instruction = "Continue this pattern for 6 cycles",
                    durationSeconds = 192, // 4*4*6 = 96 seconds per full cycle x 2
                    breathingPattern = pattern
                ),
                ExerciseStep(
                    instruction = "Return to natural breathing. Notice how you feel.",
                    durationSeconds = 20,
                    audioGuide = "Take a moment to notice any shifts in your body and mind."
                )
            ),
            completionMessage = "Beautiful work. Your nervous system is now calmer. You can return to this breath pattern anytime you need grounding."
        )
    }

    /**
     * 4-7-8 Breathing
     * Dr. Andrew Weil's relaxation technique
     */
    private fun fourSevenEightBreathing(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.FOUR_SEVEN_EIGHT_BREATHING,
            title = "4-7-8 Breathing",
            introMessage = "This technique, developed by Dr. Andrew Weil, is incredibly effective for relaxation and sleep. You'll inhale for 4, hold for 7, and exhale for 8. The long exhale is key for calming.",
            durationSeconds = 300,
            steps = listOf(
                ExerciseStep(
                    instruction = "Place the tip of your tongue against the ridge behind your upper front teeth. Keep it there throughout.",
                    durationSeconds = 10
                ),
                ExerciseStep(
                    instruction = "Exhale completely through your mouth, making a whoosh sound",
                    durationSeconds = 8,
                    visualCue = VisualCue.ContractingCircle
                ),
                ExerciseStep(
                    instruction = "Close your mouth and inhale quietly through your nose for 4 counts",
                    durationSeconds = 4,
                    visualCue = VisualCue.ExpandingCircle,
                    audioGuide = "Breathe in... 2... 3... 4"
                ),
                ExerciseStep(
                    instruction = "Hold your breath for 7 counts",
                    durationSeconds = 7,
                    visualCue = VisualCue.StaticCircle,
                    audioGuide = "Hold... 2... 3... 4... 5... 6... 7"
                ),
                ExerciseStep(
                    instruction = "Exhale completely through your mouth for 8 counts, making a whoosh sound",
                    durationSeconds = 8,
                    visualCue = VisualCue.ContractingCircle,
                    audioGuide = "Breathe out... 2... 3... 4... 5... 6... 7... 8"
                ),
                ExerciseStep(
                    instruction = "This completes one cycle. Repeat for 4 total cycles.",
                    durationSeconds = 228 // (4+7+8) * 4 cycles * 3 repetitions
                ),
                ExerciseStep(
                    instruction = "Return to normal breathing and notice the calm.",
                    durationSeconds = 30,
                    audioGuide = "You've activated your body's natural relaxation response."
                )
            ),
            completionMessage = "Excellent. This breath pattern becomes even more powerful with practice. Use it before sleep or during stressful moments."
        )
    }

    /**
     * 5-4-3-2-1 Grounding Technique
     * Engages all five senses to anchor in the present
     */
    private fun grounding54321(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.GROUNDING_54321,
            title = "5-4-3-2-1 Grounding",
            introMessage = "When anxiety pulls you into the future or past, this technique brings you back to now. We'll engage each of your five senses, one at a time.",
            durationSeconds = 300,
            steps = listOf(
                ExerciseStep(
                    instruction = "Take three slow, deep breaths to begin.",
                    durationSeconds = 15,
                    audioGuide = "Breathe with me. In... and out. Again. One more time."
                ),
                ExerciseStep(
                    instruction = "5 THINGS YOU CAN SEE\n\nLook around and name 5 things you can see right now. Out loud or in your mind. Take your time.",
                    durationSeconds = 40,
                    audioGuide = "Name five things you can see. Maybe a color, an object, a shadow. Whatever catches your eye."
                ),
                ExerciseStep(
                    instruction = "4 THINGS YOU CAN TOUCH\n\nNotice 4 things you can physically feel. The chair beneath you. Your clothes on your skin. Your feet on the ground. The temperature of the air.",
                    durationSeconds = 40,
                    audioGuide = "What can you feel right now? Notice the textures and temperatures around you."
                ),
                ExerciseStep(
                    instruction = "3 THINGS YOU CAN HEAR\n\nPause and listen. What three sounds can you hear? Maybe your breath. A distant sound. The silence between sounds.",
                    durationSeconds = 40,
                    audioGuide = "Listen closely. What sounds are present in this moment?"
                ),
                ExerciseStep(
                    instruction = "2 THINGS YOU CAN SMELL\n\nWhat can you smell? If nothing is obvious, notice the neutral smell of the air. Move to find scents if needed.",
                    durationSeconds = 30,
                    audioGuide = "Tune into your sense of smell. Even subtle scents count."
                ),
                ExerciseStep(
                    instruction = "1 THING YOU CAN TASTE\n\nNotice any taste in your mouth. Or take a sip of water if you have it. What do you taste?",
                    durationSeconds = 20,
                    audioGuide = "What taste is present on your tongue right now?"
                ),
                ExerciseStep(
                    instruction = "Take a final deep breath. You are here. You are safe. You are grounded in this moment.",
                    durationSeconds = 30,
                    audioGuide = "You've returned to the present moment. You are here."
                )
            ),
            completionMessage = "You did it. This exercise can be done anytime, anywhere. It's your anchor to the present moment."
        )
    }

    /**
     * Body Scan Meditation
     * Progressive awareness of physical sensations
     */
    private fun bodyScan(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.BODY_SCAN,
            title = "Body Scan Meditation",
            introMessage = "We'll move through your body slowly, bringing gentle awareness to each part. This isn't about changing anything—just noticing.",
            durationSeconds = 600,
            steps = listOf(
                ExerciseStep(
                    instruction = "Lie down or sit comfortably. Close your eyes if that feels safe.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "Take three deep breaths. Feel your body supported beneath you.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "Bring your attention to your feet. Notice any sensations—warmth, coolness, tingling, or nothing at all. All are okay.",
                    durationSeconds = 40,
                    audioGuide = "Notice your feet. Whatever you feel, or don't feel, is perfect."
                ),
                ExerciseStep(
                    instruction = "Move awareness to your lower legs and knees. Breathe into any tension you find.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Scan your thighs and hips. Notice where your body contacts the surface beneath you.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Bring attention to your lower back and abdomen. Feel your breath moving here.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Notice your chest and upper back. Feel your heartbeat. Feel your lungs expanding.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Scan your shoulders, often where we hold stress. Imagine them softening.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Move down your arms to your hands. Wiggle your fingers. Feel the energy there.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Bring awareness to your neck and throat. Notice any tightness or ease.",
                    durationSeconds = 30
                ),
                ExerciseStep(
                    instruction = "Scan your face—jaw, mouth, eyes, forehead. Let any tension melt away.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Finally, expand awareness to your whole body at once. You are complete. You are present.",
                    durationSeconds = 60,
                    audioGuide = "Feel your body as a whole. Complete and present."
                ),
                ExerciseStep(
                    instruction = "Begin to deepen your breath. Wiggle your fingers and toes. When you're ready, open your eyes.",
                    durationSeconds = 40
                )
            ),
            completionMessage = "You've reconnected with your body. This practice helps you notice tension before it builds and grounds you in physical reality."
        )
    }

    /**
     * Thought Record (CBT Worksheet)
     * Examining and reframing automatic thoughts
     */
    private fun thoughtRecord(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.THOUGHT_RECORD,
            title = "Thought Record",
            introMessage = "This is a core CBT tool. We'll examine a difficult thought, look at the evidence, and find a more balanced perspective. You'll need something to write on.",
            durationSeconds = 600,
            steps = listOf(
                ExerciseStep(
                    instruction = "SITUATION\n\nDescribe the situation that triggered the difficult thought. Just the facts—who, what, when, where. No interpretation yet.",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "AUTOMATIC THOUGHT\n\nWhat thought went through your mind? Write it exactly as it appeared. This is the thought we'll examine.",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "EMOTIONS\n\nWhat emotions did this thought create? Name them and rate their intensity (1-10).\n\nFor example: Anxious (8), Sad (6), Ashamed (4)",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "EVIDENCE FOR\n\nWhat evidence supports this thought? Be honest. What facts make this thought seem true?",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "EVIDENCE AGAINST\n\nWhat evidence contradicts this thought? Think of times this thought hasn't been true. What would a friend say?",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "ALTERNATIVE THOUGHTS\n\nBased on all the evidence, what are 2-3 alternative ways to think about this situation?",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "BALANCED THOUGHT\n\nNow craft one balanced thought that acknowledges both sides. This is your new perspective.",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "RE-RATE EMOTIONS\n\nWith this balanced thought in mind, re-rate your emotions. How intense are they now?",
                    durationSeconds = 60
                )
            ),
            completionMessage = "You've just done cognitive restructuring—one of the most powerful tools in therapy. Keep practicing this, and it becomes automatic."
        )
    }

    /**
     * Emotion Wheel
     * Identifying emotions with precision
     */
    private fun emotionWheel(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.EMOTION_WHEEL,
            title = "Emotion Wheel Exploration",
            introMessage = "Sometimes we feel 'bad' or 'upset,' but emotions are much more specific. Let's find the precise words for what you're feeling.",
            durationSeconds = 300,
            steps = listOf(
                ExerciseStep(
                    instruction = "Start broad: Are you feeling something positive, negative, or neutral right now?",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "Let's go deeper. If negative, is it more:\n• Anger\n• Fear\n• Sadness\n• Disgust\n• Surprise\n• Anticipation",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Now even more specific. For example:\n\nIf Anger, is it: Frustrated? Annoyed? Resentful? Furious?\n\nFind the most accurate word.",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "Where do you feel this emotion in your body? Your chest? Stomach? Throat? Face?",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "What triggered this emotion? What happened just before you felt it?",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Is there a secondary emotion underneath? Sometimes anger masks fear. Anxiety masks sadness. Keep digging.",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "Now that you've named it precisely, how does that feel? Does naming it change anything?",
                    durationSeconds = 40
                )
            ),
            completionMessage = "Research shows that precisely naming emotions (affect labeling) reduces their intensity. You've just used neuroscience to help yourself."
        )
    }

    /**
     * Gratitude Moment
     * Quick gratitude practice for mood shift
     */
    private fun gratitudeMoment(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.GRATITUDE_MOMENT,
            title = "Gratitude Moment",
            introMessage = "Gratitude is a fast track to feeling better. Let's take a moment to notice what's working in your life, even if it's small.",
            durationSeconds = 180,
            steps = listOf(
                ExerciseStep(
                    instruction = "Take a deep breath and think of three things you're grateful for right now.",
                    durationSeconds = 30
                ),
                ExerciseStep(
                    instruction = "1st Thing: What's one small thing in this moment you appreciate? Maybe your breath. A comfortable place to sit. A person who cares.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Really feel it. Don't just list it—let yourself feel the warmth of appreciation.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "2nd Thing: What's something in your life you often take for granted but truly value?",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Imagine life without it. Now appreciate that you have it.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "3rd Thing: Who is someone you're grateful for? What have they given you?",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Consider telling them. Expressed gratitude multiplies its power.",
                    durationSeconds = 20
                )
            ),
            completionMessage = "That's it. Three minutes that rewired your brain slightly toward positivity. Do this daily, and watch what happens."
        )
    }

    /**
     * Progressive Muscle Relaxation
     * Systematic tension and release
     */
    private fun progressiveRelaxation(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.PROGRESSIVE_RELAXATION,
            title = "Progressive Muscle Relaxation",
            introMessage = "We'll tense and release muscle groups systematically. This teaches your body the difference between tension and relaxation.",
            durationSeconds = 900,
            steps = listOf(
                ExerciseStep(
                    instruction = "Find a comfortable position. We'll work from feet to head.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "Curl your toes tightly. Hold the tension... 5... 4... 3... 2... 1... Release. Feel the difference.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Flex your feet, pointing toes toward your knees. Hold... Release completely.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Tighten your calf muscles. Hold... Notice the tension... Release.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Squeeze your thighs together. Hold... Release and feel them soften.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Tighten your buttocks. Hold... Release.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Tighten your stomach, as if bracing for a punch. Hold... Release and let it soften.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Take a deep breath and hold it, tensing your chest. Hold... Exhale and release.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Shrug your shoulders up to your ears. Hold the tension... Drop them completely.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Make fists with both hands. Squeeze tight... Hold... Release and let fingers spread.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Tense your arms, making muscles tight. Hold... Let them go completely limp.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Scrunch your face—eyes, nose, mouth. Hold... Release and let your face be smooth.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Raise your eyebrows, wrinkling your forehead. Hold... Release.",
                    durationSeconds = 40
                ),
                ExerciseStep(
                    instruction = "Now scan your entire body. Notice how relaxed you feel. Breathe into any remaining tension.",
                    durationSeconds = 60
                ),
                ExerciseStep(
                    instruction = "Rest here for a moment, enjoying the deep relaxation you've created.",
                    durationSeconds = 60
                )
            ),
            completionMessage = "Your body knows how to relax. You've just reminded it. Use this anytime stress builds up physically."
        )
    }

    /**
     * Loving-Kindness Meditation
     * Cultivating compassion for self and others
     */
    private fun lovingKindness(): GuidedExercise {
        return GuidedExercise(
            type = ExerciseType.LOVING_KINDNESS,
            title = "Loving-Kindness Meditation",
            introMessage = "This ancient practice cultivates compassion—first for yourself, then radiating outward to others. It's especially powerful when you're being hard on yourself.",
            durationSeconds = 600,
            steps = listOf(
                ExerciseStep(
                    instruction = "Sit comfortably and close your eyes. Take three deep breaths.",
                    durationSeconds = 20
                ),
                ExerciseStep(
                    instruction = "FOR YOURSELF\n\nPlace a hand on your heart. Repeat these phrases silently or aloud:\n\n'May I be happy.\nMay I be healthy.\nMay I be safe.\nMay I be at peace.'",
                    durationSeconds = 90,
                    audioGuide = "Feel compassion for yourself. You deserve kindness."
                ),
                ExerciseStep(
                    instruction = "Really feel these wishes for yourself. You deserve happiness, health, safety, and peace.",
                    durationSeconds = 30
                ),
                ExerciseStep(
                    instruction = "FOR SOMEONE YOU LOVE\n\nBring to mind someone you care about. Picture them clearly. Now offer them these wishes:\n\n'May you be happy.\nMay you be healthy.\nMay you be safe.\nMay you be at peace.'",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "FOR SOMEONE NEUTRAL\n\nThink of someone you see but don't know well—a barista, a neighbor. They too want to be happy. Offer them the same wishes.",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "FOR SOMEONE DIFFICULT\n\nThis is advanced. If you're ready, think of someone you have difficulty with. They too suffer. They too want happiness. Offer what you can:\n\n'May you be happy...'",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "FOR ALL BEINGS\n\nNow expand to everyone:\n\n'May all beings be happy.\nMay all beings be healthy.\nMay all beings be safe.\nMay all beings be at peace.'",
                    durationSeconds = 90
                ),
                ExerciseStep(
                    instruction = "Return to yourself. Notice how you feel. Sit with whatever is present.",
                    durationSeconds = 60
                )
            ),
            completionMessage = "Compassion is a skill. You've just practiced it. Research shows this meditation rewires your brain for kindness and connection."
        )
    }
}
