package com.skilltok.app

object MockData {
    val courses = listOf(
        Course(
            id = "c1", 
            title = "Modern Leadership Mastery", 
            subject = "Leadership", 
            level = "Advanced", 
            learnersCount = 1250, 
            rating = 4.9, 
            thumbnailUrl = "https://images.unsplash.com/photo-1507679799987-c7377f5da5b2?w=800", 
            description = "Master the art of leadership. Learn to inspire your team, build trust, and lead with a 'Start with Why' mindset inspired by Simon Sinek and Jocko Willink.",
            createdAt = "2024-01-01T00:00:00.000Z"
        ),
        Course(
            id = "c2", 
            title = "Digital Marketing & SEO", 
            subject = "Marketing", 
            level = "Intermediate", 
            learnersCount = 3400, 
            rating = 4.8, 
            thumbnailUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800", 
            description = "A comprehensive guide to digital marketing. Master SEO, content strategy, and data-driven growth strategies to scale any business.",
            createdAt = "2024-01-02T00:00:00.000Z"
        ),
        Course(
            id = "c3", 
            title = "Data Science Foundations", 
            subject = "Technology", 
            level = "Beginner", 
            learnersCount = 4200, 
            rating = 4.9, 
            thumbnailUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800", 
            description = "Start your journey in Data Science. Learn Python, statistical analysis, and how to derive actionable insights from complex data sets.",
            createdAt = "2024-01-03T00:00:00.000Z"
        ),
        Course(
            id = "c4", 
            title = "The Art of Public Speaking", 
            subject = "Communication", 
            level = "Beginner", 
            learnersCount = 1500, 
            rating = 4.8, 
            thumbnailUrl = "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800", 
            description = "Transform your communication skills. Learn to speak with confidence, clarity, and impact in any professional situation.",
            createdAt = "2024-01-04T00:00:00.000Z"
        ),
        Course(
            id = "c5", 
            title = "Advanced HR Management", 
            subject = "Human Resources", 
            level = "Advanced", 
            learnersCount = 950, 
            rating = 4.7, 
            thumbnailUrl = "https://images.unsplash.com/photo-1521737711867-e3b97375f902?w=800", 
            description = "Deep dive into workforce planning, recruitment, and employee relations. Perfect for HR professionals aiming for the executive level.",
            createdAt = "2024-01-05T00:00:00.000Z"
        ),
        Course(
            id = "c6", 
            title = "Emotional Intelligence Mastery", 
            subject = "Psychology", 
            level = "Intermediate", 
            learnersCount = 2800, 
            rating = 4.9, 
            thumbnailUrl = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=800", 
            description = "Unlock the power of EQ. Understand your emotions and learn to influence others through empathy, social awareness, and self-regulation.",
            createdAt = "2024-01-06T00:00:00.000Z"
        ),
        Course(
            id = "c7", 
            title = "Negotiation & Persuasion", 
            subject = "Business", 
            level = "Advanced", 
            learnersCount = 1100, 
            rating = 4.8, 
            thumbnailUrl = "https://images.unsplash.com/photo-1556761175-b413da4baf72?w=800", 
            description = "Learn the science of persuasion and the art of negotiation. Master tactical empathy and the BATNA principle for win-win outcomes.",
            createdAt = "2024-01-07T00:00:00.000Z"
        ),
        Course(
            id = "c8", 
            title = "Intro to Biological Sciences", 
            subject = "Science", 
            level = "Beginner", 
            learnersCount = 3100, 
            rating = 4.6, 
            thumbnailUrl = "https://images.unsplash.com/photo-1530026405186-ed1f139313f8?w=800", 
            description = "A fascinating journey through life. Explore cell biology, genetics, and ecology in this comprehensive introduction to the biological sciences.",
            createdAt = "2024-01-08T00:00:00.000Z"
        )
    )

    val modules = listOf(
        // C1: Leadership
        Module("c1_m1", "c1", "The Leadership Mindset", "Building a foundation of trust and visionary thinking.", 0),
        Module("c1_m2", "c1", "Inspiring Team Action", "Strategies for organizational culture and cooperation.", 1),
        // C2: Marketing
        Module("c2_m1", "c2", "Search Optimization", "How search engines work and how to rank higher.", 0),
        Module("c2_m2", "c2", "Strategic Marketing", "Digital marketing frameworks and growth hacking.", 1),
        // C3: Tech
        Module("c3_m1", "c3", "Introduction to Python", "Core programming concepts for data analysis.", 0),
        Module("c3_m2", "c3", "Data Science Ecosystem", "Navigating the world of data analytics and ML.", 1),
        // C4: Communication
        Module("c4_m1", "c4", "Foundations of Speaking", "Conquering fear and owning the room.", 0),
        Module("c4_m2", "c4", "Impactful Delivery", "Advanced techniques for public presentations.", 1),
        // C5: HRM
        Module("c5_m1", "c5", "Strategic HR Planning", "Aligning human capital with business goals.", 0),
        Module("c5_m2", "c5", "Employee Experience", "Designing a workplace where talent thrives.", 1),
        // C6: EQ
        Module("c6_m1", "c6", "Self-Awareness", "The first pillar of emotional intelligence.", 0),
        Module("c6_m2", "c6", "Empathy & Social Skills", "Understanding and influencing the social dynamic.", 1),
        // C7: Negotiation
        Module("c7_m1", "c7", "Tactical Empathy", "The secret weapon of master negotiators.", 0),
        Module("c7_m2", "c7", "Closing the Deal", "Frameworks for final stage agreements.", 1),
        // C8: Science
        Module("c8_m1", "c8", "The Building Blocks of Life", "Exploring cells and their incredible complexity.", 0),
        Module("c8_m2", "c8", "Ecosystems & Life", "Understanding how life interacts on a global scale.", 1)
    )

    val lessons = listOf(
        // C1: Leadership
        Lesson("c1_m1_l1", "c1_m1", "c1", "Leaders Eat Last", "Understanding the biological basis of trust.", "ReRcHdeUG9Y", 60, 0, "reel", true),
        Lesson("c1_m1_l2", "c1_m1", "c1", "Start With Why", "How great leaders inspire action.", "u4ZoJKF_VuA", 55, 1, "reel", false),
        Lesson("c1_m2_l1", "c1_m2", "c1", "Extreme Ownership", "Leadership masterclass on accountability.", "ljqra3BcqWM", 1200, 0, "deep_dive", true),
        
        // C2: Marketing
        Lesson("c2_m1_l1", "c2_m1", "c2", "How Google Works", "Introduction to search algorithms.", "BNHR6IQJGZs", 50, 0, "reel", true),
        Lesson("c2_m1_l2", "c2_m1", "c2", "SEO for Beginners", "Core search optimization principles.", "DvwS7gtrPyo", 65, 1, "reel", false),
        Lesson("c2_m2_l1", "c2_m2", "c2", "Digital Marketing Course", "Comprehensive digital marketing strategy guide.", "nU-IIXBWlS4", 1500, 0, "deep_dive", true),

        // C3: Technology
        Lesson("c3_m1_l1", "c3_m1", "c3", "Python in 100 Seconds", "Why Python is the most popular language.", "x7X9w_GIm1s", 40, 0, "reel", true),
        Lesson("c3_m1_l2", "c3_m1", "c3", "What is Data Science?", "Defining the data-driven future.", "X3paOmcrTjQ", 55, 1, "reel", false),
        Lesson("c3_m2_l1", "c3_m2", "c3", "Python Data Science Course", "Full masterclass on Python for data analysis.", "rfscVS0vtbw", 1800, 0, "deep_dive", true),

        // C4: Communication
        Lesson("c4_m1_l1", "c4_m1", "c4", "Speaking so People Listen", "Captivate your audience with your voice.", "eIho2S0ZahI", 45, 0, "reel", true),
        Lesson("c4_m1_l2", "c4_m1", "c4", "Body Language Mastery", "Using your posture to build confidence.", "Ks-_Mh1QhMc", 60, 1, "reel", false),
        Lesson("c4_m2_l1", "c4_m2", "c4", "Public Speaking Techniques", "Advanced masterclass from the best TED speakers.", "8li7-it8OkM", 1100, 0, "deep_dive", true),

        // C5: HRM
        Lesson("c5_m1_l1", "c5_m1", "c5", "Strategic HRM", "Intro to Strategic Human Resource Management.", "m6pGfA8qCsk", 50, 0, "reel", true),
        Lesson("c5_m1_l2", "c5_m1", "c5", "Workforce Planning", "Identifying future talent needs.", "fW8amMCVAJQ", 65, 1, "reel", false),
        Lesson("c5_m2_l1", "c5_m2", "c5", "HR Management Class", "The complete guide to HR management.", "9I_Y6Sj7p6Q", 1400, 0, "deep_dive", true),

        // C6: EQ
        Lesson("c6_m1_l1", "c6_m1", "c6", "Daniel Goleman on EQ", "The father of EQ explains the concept.", "Y7m9eNoB3NU", 60, 0, "reel", true),
        Lesson("c6_m1_l2", "c6_m1", "c6", "EQ vs IQ", "Why emotional intelligence matters more.", "ReRcHdeUG9Y", 55, 1, "reel", false),
        Lesson("c6_m2_l1", "c6_m2", "c6", "Mastering Social Skills", "Advanced emotional intelligence masterclass.", "L9m-7_60XWA", 1300, 0, "deep_dive", true),

        // C7: Negotiation
        Lesson("c7_m1_l1", "c7_m1", "c7", "Tactical Empathy", "Negotiation lessons from Chris Voss.", "qp0HIF3SfI4", 50, 0, "reel", true),
        Lesson("c7_m1_l2", "c7_m1", "c7", "Never Split Difference", "Quick tips for better negotiations.", "guS9pU_U_z0", 65, 1, "reel", false),
        Lesson("c7_m2_l1", "c7_m2", "c7", "Negotiation Mastery", "The art and science of closing deals.", "h95cQkEWBx0", 1500, 0, "deep_dive", true),

        // C8: Science
        Lesson("c8_m1_l1", "c8_m1", "c8", "Biological Molecules", "Intro to the chemistry of life.", "PYH63o10iTE", 40, 0, "reel", true),
        Lesson("c8_m1_l2", "c8_m1", "c8", "The Cell", "A quick tour of the living cell.", "8li7-it8OkM", 55, 1, "reel", false),
        Lesson("c8_m2_l1", "c8_m2", "c8", "Biology Full Course", "Crash course in the biological sciences.", "ua-CiDNNj30", 1800, 0, "deep_dive", true)
    )

    val quizQuestions = mutableListOf<QuizQuestion>().apply {
        lessons.filter { it.hasQuiz }.forEach { lesson ->
            add(QuizQuestion(
                id = "${lesson.id}_q1", referenceId = lesson.id, lessonId = lesson.id,
                questionText = when(lesson.courseId) {
                    "c1" -> "According to Simon Sinek, what is the core of trust in leadership?"
                    "c2" -> "What is the primary goal of Search Engine Optimization?"
                    "c3" -> "Why is Python widely used in Data Science?"
                    "c4" -> "What is an effective way to improve stage presence?"
                    "c5" -> "What is the focus of Strategic HRM?"
                    "c6" -> "Which of these is a pillar of Emotional Intelligence?"
                    "c7" -> "What does BATNA stand for in negotiation?"
                    "c8" -> "What is the primary unit of life in biological science?"
                    else -> "What is the key takeaway from this lesson?"
                },
                options = listOf("Correct Answer (as per video)", "Option B", "Option C", "Option D"),
                correctAnswerIndexes = listOf(0), 
                explanation = "This core concept was highlighted in the video as the foundation for mastery in this field."
            ))
        }
    }

    val currentUser = User("u1", "Student Alex", "alex@example.com", "learner", xp = 450, streak = 5, level = 3)
}
