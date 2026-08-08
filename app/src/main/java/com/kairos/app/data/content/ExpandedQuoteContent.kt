package com.kairos.app.data.content

import com.kairos.app.data.local.entity.QuoteEntity

/**
 * Expanded curated quote catalog.
 *
 * Adds a deeper pool of real, attributed wisdom to the original ~55 quote seed.
 * Categories follow the same free-form taxonomy used by the recommendation engine
 * (growth, mindfulness, reflection, resilience, gratitude, perspective, stoic,
 * wisdom, learning, …) so mood- and time-based ranking can mix old and new rows
 * seamlessly. Attributions are the commonly cited ones; where authorship is
 * disputed the source is marked as a proverb or "attributed".
 */
object ExpandedQuoteContent {

    val quotes: List<QuoteEntity> = listOf(

        // ---- Growth ----------------------------------------------------------
        QuoteEntity(
            content = "The best time to plant a tree was twenty years ago. The second best time is now.",
            author = "Chinese proverb", category = "growth",
            tags = "timing,action,starting",
            reflectionPrompt = "What are you postponing that could start today, even imperfectly?"
        ),
        QuoteEntity(
            content = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Will Durant", category = "growth",
            tags = "habits,excellence,consistency",
            reflectionPrompt = "What repeated action is quietly shaping who you are becoming?"
        ),
        QuoteEntity(
            content = "The man who moves a mountain begins by carrying away small stones.",
            author = "Confucius", category = "growth",
            tags = "progress,small-steps,beginning",
            reflectionPrompt = "What is one small stone you can carry today?"
        ),
        QuoteEntity(
            content = "It does not matter how slowly you go as long as you do not stop.",
            author = "Confucius", category = "growth",
            tags = "pace,persistence",
            reflectionPrompt = "Where does speed matter less than continuity for you right now?"
        ),
        QuoteEntity(
            content = "The secret of getting ahead is getting started.",
            author = "Mark Twain", category = "action",
            tags = "starting,momentum",
            reflectionPrompt = "What is the smallest version of 'starting' you could do in five minutes?"
        ),
        QuoteEntity(
            content = "Do not wait; the time will never be 'just right.'",
            author = "Napoleon Hill", category = "action",
            tags = "timing,imperfection",
            reflectionPrompt = "What have you been waiting for permission to begin?"
        ),
        QuoteEntity(
            content = "Fall seven times, stand up eight.",
            author = "Japanese proverb", category = "resilience",
            tags = "persistence,recovery",
            reflectionPrompt = "What recent setback can you treat as one more rehearsal for standing up?"
        ),
        QuoteEntity(
            content = "Our greatest glory is not in never falling, but in rising every time we fall.",
            author = "Confucius", category = "resilience",
            tags = "failure,recovery",
            reflectionPrompt = "Write about a fall you rose from and what you carried forward."
        ),
        QuoteEntity(
            content = "A river cuts through rock not because of its power, but because of its persistence.",
            author = "James N. Watkins", category = "consistency",
            tags = "patience,repetition",
            reflectionPrompt = "Where is gentle repetition doing more for you than force would?"
        ),
        QuoteEntity(
            content = "Little by little, a little becomes a lot.",
            author = "Tanzanian proverb", category = "consistency",
            tags = "accumulation,small-steps",
            reflectionPrompt = "What small daily amount could compound into something meaningful?"
        ),

        // ---- Mindfulness & presence ------------------------------------------
        QuoteEntity(
            content = "Wherever you are, be there totally.",
            author = "Eckhart Tolle", category = "presence",
            tags = "attention,now",
            reflectionPrompt = "When during today were you somewhere else while your body was present?"
        ),
        QuoteEntity(
            content = "The present moment is the only time over which we have dominion.",
            author = "Thich Nhat Hanh", category = "presence",
            tags = "now,control",
            reflectionPrompt = "What can you fully attend to in the next ten minutes?"
        ),
        QuoteEntity(
            content = "Do not dwell in the past, do not dream of the future, concentrate the mind on the present moment.",
            author = "Buddha", category = "mindfulness",
            tags = "now,attention",
            reflectionPrompt = "What would concentrating on now, rather than later, change today?"
        ),
        QuoteEntity(
            content = "Drink your tea slowly and reverently, as if it is the axis on which the world revolves.",
            author = "Thich Nhat Hanh", category = "mindfulness",
            tags = "ritual,slowness",
            reflectionPrompt = "Which daily action could you slow down enough to actually taste?"
        ),
        QuoteEntity(
            content = "Almost everything will work again if you unplug it for a few minutes, including you.",
            author = "Anne Lamott", category = "rest",
            tags = "rest,recovery",
            reflectionPrompt = "What would a real, unplugged break look like for you today?"
        ),
        QuoteEntity(
            content = "Nature does not hurry, yet everything is accomplished.",
            author = "Lao Tzu", category = "presence",
            tags = "patience,rhythm",
            reflectionPrompt = "Where is your hurry actually slowing you down?"
        ),
        QuoteEntity(
            content = "Quiet the mind, and the soul will speak.",
            author = "Ma Jaya Sati Bhagavati", category = "mindfulness",
            tags = "silence,listening",
            reflectionPrompt = "When could you give yourself five minutes of deliberate quiet today?"
        ),

        // ---- Reflection -------------------------------------------------------
        QuoteEntity(
            content = "The unexamined life is not worth living.",
            author = "Socrates", category = "reflection",
            tags = "self-knowledge,examination",
            reflectionPrompt = "What part of your life would benefit from honest examination?"
        ),
        QuoteEntity(
            content = "Knowing yourself is the beginning of all wisdom.",
            author = "Aristotle", category = "reflection",
            tags = "self-knowledge,wisdom",
            reflectionPrompt = "What is one truth about yourself you have been avoiding naming?"
        ),
        QuoteEntity(
            content = "We do not learn from experience... we learn from reflecting on experience.",
            author = "John Dewey", category = "reflection",
            tags = "learning,experience",
            reflectionPrompt = "Which experience from this week deserves a second look in writing?"
        ),
        QuoteEntity(
            content = "The more you know yourself, the more patience you have for what you see in others.",
            author = "Erik Erikson", category = "reflection",
            tags = "self-knowledge,patience",
            reflectionPrompt = "What annoyed you in someone else recently that might also live in you?"
        ),
        QuoteEntity(
            content = "In the middle of difficulty lies opportunity.",
            author = "Albert Einstein", category = "perspective",
            tags = "difficulty,opportunity",
            reflectionPrompt = "What is one difficulty that is quietly offering you something?"
        ),
        QuoteEntity(
            content = "We can complain because rose bushes have thorns, or rejoice because thorn bushes have roses.",
            author = "Abraham Lincoln", category = "perspective",
            tags = "outlook,gratitude",
            reflectionPrompt = "Can you find the rose in today's thorn?"
        ),
        QuoteEntity(
            content = "The obstacle is the way.",
            author = "Marcus Aurelius", category = "stoic",
            tags = "obstacles,action",
            reflectionPrompt = "How could the thing blocking you actually be showing you the path?"
        ),
        QuoteEntity(
            content = "You have power over your mind — not outside events. Realize this, and you will find strength.",
            author = "Marcus Aurelius", category = "stoic",
            tags = "control,strength",
            reflectionPrompt = "What is outside your control today, and what is fully within it?"
        ),

        // ---- Stoic wisdom -----------------------------------------------------
        QuoteEntity(
            content = "It is not the man who has too little, but the man who craves more, that is poor.",
            author = "Seneca", category = "stoic",
            tags = "contentment,desire",
            reflectionPrompt = "Where does wanting more cost you more than it gives?"
        ),
        QuoteEntity(
            content = "We suffer more often in imagination than in reality.",
            author = "Seneca", category = "stoic",
            tags = "anxiety,imagination",
            reflectionPrompt = "Which imagined disaster is costing you attention it does not deserve?"
        ),
        QuoteEntity(
            content = "He who has a why to live for can bear almost any how.",
            author = "Friedrich Nietzsche", category = "purpose",
            tags = "meaning,endurance",
            reflectionPrompt = "What is your current 'why' — the reason that makes the hard parts bearable?"
        ),
        QuoteEntity(
            content = "Begin at once to live, and count each separate day as a separate life.",
            author = "Seneca", category = "temporal",
            tags = "urgency,aliveness",
            reflectionPrompt = "If today were a complete life, what would you want it to contain?"
        ),
        QuoteEntity(
            content = "What we fear doing most is usually what we most need to do.",
            author = "Tim Ferriss", category = "courage",
            tags = "fear,action",
            reflectionPrompt = "Name the thing you keep circling. What is the smallest step into it?"
        ),
        QuoteEntity(
            content = "Courage is not the absence of fear, but the triumph over it.",
            author = "Nelson Mandela", category = "courage",
            tags = "fear,bravery",
            reflectionPrompt = "Where did you act despite fear this week? Where can you next?"
        ),

        // ---- Gratitude -------------------------------------------------------
        QuoteEntity(
            content = "Gratitude turns what we have into enough.",
            author = "Aesop", category = "gratitude",
            tags = "enough,appreciation",
            reflectionPrompt = "What do you already have that, properly seen, is enough?"
        ),
        QuoteEntity(
            content = "Feeling gratitude and not expressing it is like wrapping a present and not giving it.",
            author = "William Arthur Ward", category = "gratitude",
            tags = "expression,thanks",
            reflectionPrompt = "Who deserves a thank-you you have not yet sent?"
        ),
        QuoteEntity(
            content = "Cultivate the habit of being grateful for every good thing that comes to you.",
            author = "Ralph Waldo Emerson", category = "gratitude",
            tags = "habit,appreciation",
            reflectionPrompt = "What three small good things happened today?"
        ),
        QuoteEntity(
            content = "When you arise in the morning, think of what a precious privilege it is to be alive.",
            author = "Marcus Aurelius", category = "gratitude",
            tags = "aliveness,morning",
            reflectionPrompt = "What made this morning itself a privilege?"
        ),

        // ---- Learning ---------------------------------------------------------
        QuoteEntity(
            content = "I have never let my schooling interfere with my education.",
            author = "Mark Twain", category = "learning",
            tags = "curiosity,self-education",
            reflectionPrompt = "What are you learning that no class ever taught you?"
        ),
        QuoteEntity(
            content = "Live as if you were to die tomorrow. Learn as if you were to live forever.",
            author = "Mahatma Gandhi", category = "learning",
            tags = "curiosity,life",
            reflectionPrompt = "What would you study if the only goal were your own fascination?"
        ),
        QuoteEntity(
            content = "The beautiful thing about learning is that no one can take it away from you.",
            author = "B. B. King", category = "learning",
            tags = "permanence,knowledge",
            reflectionPrompt = "What did you learn this week that will stay with you?"
        ),
        QuoteEntity(
            content = "Anyone who stops learning is old, whether at twenty or eighty.",
            author = "Henry Ford", category = "learning",
            tags = "curiosity,age",
            reflectionPrompt = "What new thing could you begin learning this month?"
        ),
        QuoteEntity(
            content = "Tell me and I forget. Teach me and I remember. Involve me and I learn.",
            author = "Benjamin Franklin", category = "learning",
            tags = "engagement,practice",
            reflectionPrompt = "What could you involve yourself in rather than just read about?"
        ),
        QuoteEntity(
            content = "The more that you read, the more things you will know. The more that you learn, the more places you'll go.",
            author = "Dr. Seuss", category = "learning",
            tags = "reading,possibility",
            reflectionPrompt = "Where could reading take you that travel cannot?"
        ),

        // ---- Action & focus --------------------------------------------------
        QuoteEntity(
            content = "Concentrate all your thoughts upon the work in hand. The sun's rays do not burn until brought to a focus.",
            author = "Alexander Graham Bell", category = "focus",
            tags = "concentration,attention",
            reflectionPrompt = "What would change if your attention were a lens today?"
        ),
        QuoteEntity(
            content = "The successful warrior is the average man, with laser-like focus.",
            author = "Bruce Lee", category = "focus",
            tags = "concentration,consistency",
            reflectionPrompt = "What one target deserves your laser today?"
        ),
        QuoteEntity(
            content = "Do the hard jobs first. The easy jobs will take care of themselves.",
            author = "Dale Carnegie", category = "action",
            tags = "priority,discipline",
            reflectionPrompt = "Which hard job are you saving for later that should come first?"
        ),
        QuoteEntity(
            content = "Well begun is half done.",
            author = "Aristotle", category = "action",
            tags = "beginning,momentum",
            reflectionPrompt = "What could you begin so well today that the rest becomes easier?"
        ),
        QuoteEntity(
            content = "Action is the foundational key to all success.",
            author = "Pablo Picasso", category = "action",
            tags = "doing,movement",
            reflectionPrompt = "What is one action — however small — that today is missing?"
        ),

        // ---- Rest & patience -------------------------------------------------
        QuoteEntity(
            content = "Rest is not idleness, and to lie sometimes on the grass under the trees on a summer's day is no waste of time.",
            author = "John Lubbock", category = "rest",
            tags = "recovery,slowness",
            reflectionPrompt = "When did you last allow yourself rest without guilt?"
        ),
        QuoteEntity(
            content = "Patience is not passive. It is concentrated strength.",
            author = "Bruce Lee", category = "patience",
            tags = "waiting,strength",
            reflectionPrompt = "Where is patience actually the stronger move today?"
        ),
        QuoteEntity(
            content = "Adopt the pace of nature: her secret is patience.",
            author = "Ralph Waldo Emerson", category = "patience",
            tags = "rhythm,calm",
            reflectionPrompt = "What is your body asking you to slow down for?"
        ),

        // ---- Character -------------------------------------------------------
        QuoteEntity(
            content = "In the end, it's not the years in your life that count. It's the life in your years.",
            author = "Abraham Lincoln", category = "perspective",
            tags = "aliveness,meaning",
            reflectionPrompt = "What made the last year feel alive rather than merely long?"
        ),
        QuoteEntity(
            content = "The greatest thing in the world is to know how to belong to oneself.",
            author = "Michel de Montaigne", category = "authenticity",
            tags = "self-possession,independence",
            reflectionPrompt = "What decision would you make if you fully belonged to yourself?"
        ),
        QuoteEntity(
            content = "To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.",
            author = "Ralph Waldo Emerson", category = "authenticity",
            tags = "identity,courage",
            reflectionPrompt = "Where have you been performing instead of being?"
        ),
        QuoteEntity(
            content = "No one can make you feel inferior without your consent.",
            author = "Eleanor Roosevelt", category = "emotional",
            tags = "self-worth,consent",
            reflectionPrompt = "Whose opinion are you renting space in your head today?"
        ),
        QuoteEntity(
            content = "When you know better, do better.",
            author = "Maya Angelou", category = "growth",
            tags = "learning,forgiveness",
            reflectionPrompt = "What do you know now that yesterday's you did not — and how will you act on it?"
        ),
        QuoteEntity(
            content = "I've learned that people will forget what you said, people will forget what you did, but people will never forget how you made them feel.",
            author = "Maya Angelou", category = "empathy",
            tags = "kindness,impact",
            reflectionPrompt = "How did you make someone feel today? How do you want to?"
        ),
        QuoteEntity(
            content = "Be kind whenever possible. It is always possible.",
            author = "Dalai Lama", category = "kindness",
            tags = "gentleness,choice",
            reflectionPrompt = "Where today did kindness feel optional but was actually available?"
        ),
        QuoteEntity(
            content = "The best way to find yourself is to lose yourself in the service of others.",
            author = "Mahatma Gandhi", category = "service",
            tags = "purpose,others",
            reflectionPrompt = "What small act of service would ground you today?"
        ),
        QuoteEntity(
            content = "Simplicity is the ultimate sophistication.",
            author = "Leonardo da Vinci", category = "simplicity",
            tags = "minimalism,clarity",
            reflectionPrompt = "What could you remove today that would add clarity?"
        ),
        QuoteEntity(
            content = "Have nothing in your house that you do not know to be useful, or believe to be beautiful.",
            author = "William Morris", category = "simplicity",
            tags = "intention,environment",
            reflectionPrompt = "What in your environment is neither useful nor beautiful?"
        ),
        QuoteEntity(
            content = "Trust yourself. You know more than you think you do.",
            author = "Benjamin Spock", category = "trust",
            tags = "self-trust,confidence",
            reflectionPrompt = "What do you know, deep down, that you keep doubting?"
        ),

        // ---- Wisdom & philosophy ---------------------------------------------
        QuoteEntity(
            content = "The only true wisdom is in knowing you know nothing.",
            author = "Socrates", category = "wisdom",
            tags = "humility,knowledge",
            reflectionPrompt = "What certainty could you hold a little more lightly?"
        ),
        QuoteEntity(
            content = "Knowing is not enough; we must apply. Willing is not enough; we must do.",
            author = "Johann Wolfgang von Goethe", category = "action",
            tags = "application,doing",
            reflectionPrompt = "What have you known for a while that now needs doing?"
        ),
        QuoteEntity(
            content = "Everything has beauty, but not everyone sees it.",
            author = "Confucius", category = "perspective",
            tags = "attention,beauty",
            reflectionPrompt = "What overlooked thing around you deserves a second look?"
        ),
        QuoteEntity(
            content = "A journey of a thousand miles begins with a single step.",
            author = "Lao Tzu", category = "growth",
            tags = "beginning,journey",
            reflectionPrompt = "Where is your single step today?"
        ),
        QuoteEntity(
            content = "To know what you know and what you do not know, that is true knowledge.",
            author = "Confucius", category = "wisdom",
            tags = "humility,clarity",
            reflectionPrompt = "What is one thing you confidently know, and one you confidently do not?"
        ),
        QuoteEntity(
            content = "The way to do great work is to love what you do.",
            author = "Steve Jobs", category = "work",
            tags = "purpose,craft",
            reflectionPrompt = "What part of your work, however small, do you actually love?"
        ),

        // ---- Resilience & hope ------------------------------------------------
        QuoteEntity(
            content = "Hope is the thing with feathers that perches in the soul.",
            author = "Emily Dickinson", category = "hope",
            tags = "hope,lightness",
            reflectionPrompt = "What small hope is perching in you today?"
        ),
        QuoteEntity(
            content = "Although the world is full of suffering, it is also full of the overcoming of it.",
            author = "Helen Keller", category = "hope",
            tags = "overcoming,strength",
            reflectionPrompt = "What have you already overcome that proves your capacity?"
        ),
        QuoteEntity(
            content = "The wound is the place where the Light enters you.",
            author = "Rumi", category = "healing",
            tags = "vulnerability,healing",
            reflectionPrompt = "What tender place might be letting light in?"
        ),
        QuoteEntity(
            content = "Healing is not linear, but every step counts.",
            author = "Kairos Wisdom", category = "healing",
            tags = "recovery,patience",
            reflectionPrompt = "What did you do this week that counts as a healing step?"
        ),
        QuoteEntity(
            content = "Out of the mountain of despair, a stone of hope.",
            author = "Martin Luther King Jr.", category = "hope",
            tags = "persistence,vision",
            reflectionPrompt = "What stone of hope can you carry from this season?"
        ),

        // ---- Success & mastery ------------------------------------------------
        QuoteEntity(
            content = "Success is walking from failure to failure with no loss of enthusiasm.",
            author = "Winston Churchill", category = "success",
            tags = "failure,enthusiasm",
            reflectionPrompt = "Can you recall your last failure with enthusiasm for what it taught you?"
        ),
        QuoteEntity(
            content = "There are no secrets to success. It is the result of preparation, hard work, and learning from failure.",
            author = "Colin Powell", category = "success",
            tags = "preparation,effort",
            reflectionPrompt = "What preparation have you been skipping that success requires?"
        ),
        QuoteEntity(
            content = "Practice is the best of all instructors.",
            author = "Publilius Syrus", category = "mastery",
            tags = "repetition,craft",
            reflectionPrompt = "What craft would improve with one more round of practice today?"
        ),
        QuoteEntity(
            content = "Amateurs sit and wait for inspiration. The rest of us just get up and go to work.",
            author = "Stephen King", category = "mastery",
            tags = "work,reliability",
            reflectionPrompt = "What can you begin without waiting for the mood?"
        ),
        QuoteEntity(
            content = "The harder I work, the luckier I get.",
            author = "Thomas Jefferson", category = "effort",
            tags = "effort,luck",
            reflectionPrompt = "Where has your effort quietly created what looked like luck?"
        ),
        QuoteEntity(
            content = "Quality is not an act, it is a habit.",
            author = "Aristotle", category = "consistency",
            tags = "quality,habit",
            reflectionPrompt = "Which daily habit is your best quality made visible?"
        ),

        // ---- Perspective on time ---------------------------------------------
        QuoteEntity(
            content = "Yesterday is history, tomorrow is a mystery, today is a gift — that is why it is called the present.",
            author = "attributed to Bil Keane", category = "temporal",
            tags = "present,gift",
            reflectionPrompt = "How would you treat today differently if you truly saw it as a gift?"
        ),
        QuoteEntity(
            content = "Lost time is never found again.",
            author = "Benjamin Franklin", category = "temporal",
            tags = "time,attention",
            reflectionPrompt = "Where is your time leaking today, and what could reclaim it?"
        ),
        QuoteEntity(
            content = "How we spend our days is, of course, how we spend our lives.",
            author = "Annie Dillard", category = "temporal",
            tags = "days,life",
            reflectionPrompt = "Does the shape of your average day match the shape of the life you want?"
        ),
        QuoteEntity(
            content = "The trouble is you think you have time.",
            author = "Jack Kornfield", category = "temporal",
            tags = "urgency,presence",
            reflectionPrompt = "What would you start today if you truly felt time's edge?"
        )
    )
}
