package com.skilltok.app

import java.util.UUID

object MockData {
    val courses = listOf(
        Course(
            id = "550e8400-e29b-41d4-a716-446655440000", 
            title = "Modern Leadership Mastery", 
            subject = "Leadership", 
            level = "Advanced", 
            learnersCount = 1250, 
            rating = 4.9, 
            thumbnailUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=800",
            description = "Master the art of leadership. Learn to inspire your team, build trust, and lead with a 'Start with Why' mindset inspired by Simon Sinek and Jocko Willink.",
            createdAt = "2024-01-01T00:00:00.000Z"
        ),
        Course(
            id = "550e8400-e29b-41d4-a716-446655440001", 
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
            id = "550e8400-e29b-41d4-a716-446655440002", 
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
            id = "550e8400-e29b-41d4-a716-446655440003", 
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
            id = "550e8400-e29b-41d4-a716-446655440004", 
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
            id = "550e8400-e29b-41d4-a716-446655440005", 
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
            id = "550e8400-e29b-41d4-a716-446655440006", 
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
            id = "550e8400-e29b-41d4-a716-446655440007", 
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
        Module("550e8400-e29b-41d4-a716-446655440100", "550e8400-e29b-41d4-a716-446655440000", "The Leadership Mindset", "Building a foundation of trust and visionary thinking.", 0),
        Module("550e8400-e29b-41d4-a716-446655440101", "550e8400-e29b-41d4-a716-446655440000", "Inspiring Team Action", "Strategies for organizational culture and cooperation.", 1),
        // C2: Marketing
        Module("550e8400-e29b-41d4-a716-446655440102", "550e8400-e29b-41d4-a716-446655440001", "Search Optimization", "How search engines work and how to rank higher.", 0),
        Module("550e8400-e29b-41d4-a716-446655440103", "550e8400-e29b-41d4-a716-446655440001", "Strategic Marketing", "Digital marketing frameworks and growth hacking.", 1),
        // C3: Tech
        Module("550e8400-e29b-41d4-a716-446655440104", "550e8400-e29b-41d4-a716-446655440002", "Introduction to Python", "Core programming concepts for data analysis.", 0),
        Module("550e8400-e29b-41d4-a716-446655440105", "550e8400-e29b-41d4-a716-446655440002", "Data Science Ecosystem", "Navigating the world of data analytics and ML.", 1),
        // C4: Communication
        Module("550e8400-e29b-41d4-a716-446655440106", "550e8400-e29b-41d4-a716-446655440003", "Foundations of Speaking", "Conquering fear and owning the room.", 0),
        Module("550e8400-e29b-41d4-a716-446655440107", "550e8400-e29b-41d4-a716-446655440003", "Impactful Delivery", "Advanced techniques for public presentations.", 1),
        // C5: HRM
        Module("550e8400-e29b-41d4-a716-446655440108", "550e8400-e29b-41d4-a716-446655440004", "Strategic HR Planning", "Aligning human capital with business goals.", 0),
        Module("550e8400-e29b-41d4-a716-446655440109", "550e8400-e29b-41d4-a716-446655440004", "Employee Experience", "Designing a workplace where talent thrives.", 1),
        // C6: EQ
        Module("550e8400-e29b-41d4-a716-446655440110", "550e8400-e29b-41d4-a716-446655440005", "Self-Awareness", "The first pillar of emotional intelligence.", 0),
        Module("550e8400-e29b-41d4-a716-446655440111", "550e8400-e29b-41d4-a716-446655440005", "Empathy & Social Skills", "Understanding and influencing the social dynamic.", 1),
        // C7: Negotiation
        Module("550e8400-e29b-41d4-a716-446655440112", "550e8400-e29b-41d4-a716-446655440006", "Tactical Empathy", "The secret weapon of master negotiators.", 0),
        Module("550e8400-e29b-41d4-a716-446655440113", "550e8400-e29b-41d4-a716-446655440006", "Closing the Deal", "Frameworks for final stage agreements.", 1),
        // C8: Science
        Module("550e8400-e29b-41d4-a716-446655440114", "550e8400-e29b-41d4-a716-446655440007", "The Building Blocks of Life", "Exploring cells and their incredible complexity.", 0),
        Module("550e8400-e29b-41d4-a716-446655440115", "550e8400-e29b-41d4-a716-446655440007", "Ecosystems & Life", "Understanding how life interacts on a global scale.", 1)
    )

    val lessons = listOf(
        // C1: Leadership
        Lesson("550e8400-e29b-41d4-a716-446655440200", "550e8400-e29b-41d4-a716-446655440100", "550e8400-e29b-41d4-a716-446655440000", "Leaders Eat Last", "Understanding the biological basis of trust.", "ReRcHdeUG9Y", "video", 60, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440201", "550e8400-e29b-41d4-a716-446655440100", "550e8400-e29b-41d4-a716-446655440000", "Start With Why", "How great leaders inspire action.", "u4ZoJKF_VuA", "video", 55, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440202", "550e8400-e29b-41d4-a716-446655440101", "550e8400-e29b-41d4-a716-446655440000", "Extreme Ownership", "Leadership masterclass on accountability.", "ljqra3BcqWM", "video", 1200, 0, "deep_dive", true),
        
        // C2: Marketing
        Lesson("550e8400-e29b-41d4-a716-446655440203", "550e8400-e29b-41d4-a716-446655440102", "550e8400-e29b-41d4-a716-446655440001", "How Google Works", "Introduction to search algorithms.", "BNHR6IQJGZs", "video", 50, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440204", "550e8400-e29b-41d4-a716-446655440102", "550e8400-e29b-41d4-a716-446655440001", "SEO for Beginners", "Core search optimization principles.", "DvwS7gtrPyo", "video", 65, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440205", "550e8400-e29b-41d4-a716-446655440103", "550e8400-e29b-41d4-a716-446655440001", "Digital Marketing Course", "Comprehensive digital marketing strategy guide.", "nU-IIXBWlS4", "video", 1500, 0, "deep_dive", true),

        // C3: Tech
        Lesson("550e8400-e29b-41d4-a716-446655440206", "550e8400-e29b-41d4-a716-446655440104", "550e8400-e29b-41d4-a716-446655440002", "Python in 100 Seconds", "Why Python is the most popular language.", "x7X9w_GIm1s", "video", 40, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440207", "550e8400-e29b-41d4-a716-446655440104", "550e8400-e29b-41d4-a716-446655440002", "What is Data Science?", "Defining the data-driven future.", "X3paOmcrTjQ", "video", 55, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440208", "550e8400-e29b-41d4-a716-446655440105", "550e8400-e29b-41d4-a716-446655440002", "Python Data Science Course", "Full masterclass on Python for data analysis.", "rfscVS0vtbw", "video", 1800, 0, "deep_dive", true),

        // C4: Communication
        Lesson("550e8400-e29b-41d4-a716-446655440209", "550e8400-e29b-41d4-a716-446655440106", "550e8400-e29b-41d4-a716-446655440003", "Speaking so People Listen", "Captivate your audience with your voice.", "eIho2S0ZahI", "video", 45, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440210", "550e8400-e29b-41d4-a716-446655440106", "550e8400-e29b-41d4-a716-446655440003", "Body Language Mastery", "Using your posture to build confidence.", "Ks-_Mh1QhMc", "video", 60, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440211", "550e8400-e29b-41d4-a716-446655440107", "550e8400-e29b-41d4-a716-446655440003", "Public Speaking Techniques", "Advanced masterclass from the best TED speakers.", "8li7-it8OkM", "video", 1100, 0, "deep_dive", true),

        // C5: HRM
        Lesson("550e8400-e29b-41d4-a716-446655440212", "550e8400-e29b-41d4-a716-446655440108", "550e8400-e29b-41d4-a716-446655440004", "Strategic HRM", "Intro to Strategic Human Resource Management.", "m6pGfA8qCsk", "video", 50, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440213", "550e8400-e29b-41d4-a716-446655440108", "550e8400-e29b-41d4-a716-446655440004", "Workforce Planning", "Identifying future talent needs.", "fW8amMCVAJQ", "video", 65, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440214", "550e8400-e29b-41d4-a716-446655440109", "550e8400-e29b-41d4-a716-446655440004", "HR Management Class", "The complete guide to HR management.", "9I_Y6Sj7p6Q", "video", 1400, 0, "deep_dive", true),

        // C6: EQ
        Lesson("550e8400-e29b-41d4-a716-446655440215", "550e8400-e29b-41d4-a716-446655440110", "550e8400-e29b-41d4-a716-446655440005", "Daniel Goleman on EQ", "The father of EQ explains the concept.", "Y7m9eNoB3NU", "video", 60, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440216", "550e8400-e29b-41d4-a716-446655440110", "550e8400-e29b-41d4-a716-446655440005", "EQ vs IQ", "Why emotional intelligence matters more.", "ReRcHdeUG9Y", "video", 55, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440217", "550e8400-e29b-41d4-a716-446655440111", "550e8400-e29b-41d4-a716-446655440005", "Mastering Social Skills", "Advanced emotional intelligence masterclass.", "L9m-7_60XWA", "video", 1300, 0, "deep_dive", true),

        // C7: Negotiation
        Lesson("550e8400-e29b-41d4-a716-446655440218", "550e8400-e29b-41d4-a716-446655440112", "550e8400-e29b-41d4-a716-446655440006", "Tactical Empathy", "Negotiation lessons from Chris Voss.", "qp0HIF3SfI4", "video", 50, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440219", "550e8400-e29b-41d4-a716-446655440112", "550e8400-e29b-41d4-a716-446655440006", "Never Split Difference", "Quick tips for better negotiations.", "guS9pU_U_z0", "video", 65, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440220", "550e8400-e29b-41d4-a716-446655440113", "550e8400-e29b-41d4-a716-446655440006", "Negotiation Mastery", "The art and science of closing deals.", "h95cQkEWBx0", "video", 1500, 0, "deep_dive", true),

        // C8: Science
        Lesson("550e8400-e29b-41d4-a716-446655440221", "550e8400-e29b-41d4-a716-446655440114", "550e8400-e29b-41d4-a716-446655440007", "Biological Molecules", "Intro to the chemistry of life.", "PYH63o10iTE", "video", 40, 0, "reel", true),
        Lesson("550e8400-e29b-41d4-a716-446655440222", "550e8400-e29b-41d4-a716-446655440114", "550e8400-e29b-41d4-a716-446655440007", "The Cell", "A quick tour of the living cell.", "8li7-it8OkM", "video", 55, 1, "reel", false),
        Lesson("550e8400-e29b-41d4-a716-446655440223", "550e8400-e29b-41d4-a716-446655440115", "550e8400-e29b-41d4-a716-446655440007", "Biology Full Course", "Crash course in the biological sciences.", "ua-CiDNNj30", "video", 1800, 0, "deep_dive", true)
    )

    val quizQuestions = mutableListOf<QuizQuestion>().apply {
        lessons.filter { it.hasQuiz }.forEach { lesson ->
            add(QuizQuestion(
                id = "${lesson.id}_q1", referenceId = lesson.id, lessonId = lesson.id,
                questionText = when(lesson.courseId) {
                    "550e8400-e29b-41d4-a716-446655440000" -> "According to Simon Sinek, what is the core of trust in leadership?"
                    "550e8400-e29b-41d4-a716-446655440001" -> "What is the primary goal of Search Engine Optimization?"
                    "550e8400-e29b-41d4-a716-446655440002" -> "Why is Python widely used in Data Science?"
                    "550e8400-e29b-41d4-a716-446655440003" -> "What is an effective way to improve stage presence?"
                    "550e8400-e29b-41d4-a716-446655440004" -> "What is the focus of Strategic HRM?"
                    "550e8400-e29b-41d4-a716-446655440005" -> "Which of these is a pillar of Emotional Intelligence?"
                    "550e8400-e29b-41d4-a716-446655440006" -> "What does BATNA stand for in negotiation?"
                    "550e8400-e29b-41d4-a716-446655440007" -> "What is the primary unit of life in biological science?"
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
