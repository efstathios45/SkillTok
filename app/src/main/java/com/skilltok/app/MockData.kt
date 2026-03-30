package com.skilltok.app

object MockData {
    val COURSE_IMAGES = mapOf(
        "hrm" to "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=1080",
        "emotionalIntelligence" to "https://images.unsplash.com/photo-1630406866478-a2fca6070d25?w=1080",
        "leadership" to "https://images.unsplash.com/photo-1770271359908-a0e5e2214f8e?w=1080",
        "marketing" to "https://images.unsplash.com/photo-1533750349088-cd871a92f312?w=1080",
        "publicSpeaking" to "https://images.unsplash.com/photo-1772419216340-fd8abb4f55de?w=1080",
        "analytics" to "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=1080",
        "negotiation" to "https://images.unsplash.com/photo-1745847768380-2caeadbb3b71?w=1080",
        "science" to "https://images.unsplash.com/photo-1532094349884-543bc11b234d?w=1080"
    )

    val courses = listOf(
        Course(id = "c1", title = "Complete Human Resource Management", description = "A comprehensive, end-to-end HRM course covering recruitment, talent management, compensation & benefits, employee relations, HR analytics, legal compliance, performance management, and strategic HR.", subject = "Human Resources", level = "Intermediate", thumbnailUrl = COURSE_IMAGES["hrm"]!!, rating = 4.9, learnersCount = 3847),
        Course(id = "c2", title = "Emotional Intelligence Mastery", description = "Develop self-awareness, empathy, emotional regulation, and social skills. Learn to navigate complex interpersonal dynamics in both personal and professional settings with science-backed EQ frameworks.", subject = "Emotional Intelligence", level = "Beginner", thumbnailUrl = COURSE_IMAGES["emotionalIntelligence"]!!, rating = 4.8, learnersCount = 2156),
        Course(id = "c3", title = "Leadership & Management Essentials", description = "From situational leadership to transformational management. Learn how to inspire teams, make strategic decisions, manage conflict, and lead through change with real-world case studies.", subject = "Leadership", level = "Intermediate", thumbnailUrl = COURSE_IMAGES["leadership"]!!, rating = 4.7, learnersCount = 1892),
        Course(id = "c4", title = "Digital Marketing Strategy", description = "Master SEO, social media marketing, content strategy, email campaigns, and paid advertising. Build data-driven marketing plans that convert.", subject = "Marketing", level = "Beginner", thumbnailUrl = COURSE_IMAGES["marketing"]!!, rating = 4.6, learnersCount = 1543),
        Course(id = "c5", title = "Public Speaking & Presentation Skills", description = "Overcome stage fright, structure compelling narratives, use body language effectively, and deliver presentations that captivate any audience.", subject = "Public Speaking", level = "Beginner", thumbnailUrl = COURSE_IMAGES["publicSpeaking"]!!, rating = 4.8, learnersCount = 2801),
        Course(id = "c6", title = "Business Analytics Fundamentals", description = "Learn to collect, analyze, and interpret business data. Cover KPIs, dashboards, A/B testing, regression analysis, and data-driven decision-making.", subject = "Data Analytics", level = "Intermediate", thumbnailUrl = COURSE_IMAGES["analytics"]!!, rating = 4.5, learnersCount = 987),
        Course(id = "c7", title = "Negotiation & Conflict Resolution", description = "Master BATNA, principled negotiation, and win-win strategies. Handle difficult conversations, resolve workplace conflicts, and negotiate salaries, contracts, and deals.", subject = "Negotiation", level = "Advanced", thumbnailUrl = COURSE_IMAGES["negotiation"]!!, rating = 4.7, learnersCount = 1234),
        Course(id = "c8", title = "Introduction to Science & Scientific Thinking", description = "Explore the foundations of physics, chemistry, and biology. Learn the scientific method, critical thinking, and how science shapes the modern world.", subject = "Science", level = "Beginner", thumbnailUrl = COURSE_IMAGES["science"]!!, rating = 4.8, learnersCount = 1567)
    )

    val modules = listOf(
        Module("m1", "c1", "Module 1: Introduction to HRM", 0),
        Module("m2", "c1", "Module 2: Workforce Planning & Recruitment", 1),
        Module("m9", "c2", "Understanding Emotions", 0),
        Module("m12", "c3", "Foundations of Leadership", 0),
        Module("m15", "c4", "Marketing Fundamentals", 0),
        Module("m17", "c5", "Overcoming Fear", 0),
        Module("m20", "c6", "Data Fundamentals", 0),
        Module("m22", "c7", "Negotiation Principles", 0),
        Module("m24", "c8", "The Scientific Method", 0)
    )

    val lessons = listOf(
        // Human Resources (c1)
        Lesson("l1", "m1", "c1", "What is HRM?", "Define Human Resource Management and its scope within modern organizations.", "bI9RZjF-538", 90, 0, true),
        Lesson("l2", "m1", "c1", "Evolution of HR", "Trace the history from administrative personnel management to strategic business partner.", "A2HFusWQIeE", 80, 1, true),
        Lesson("l5", "m2", "c1", "Workforce Planning Basics", "Learn demand forecasting, supply analysis, and gap analysis.", "y8Kvn6oEX7Q", 90, 0, true),
        
        // Emotional Intelligence (c2)
        Lesson("l36", "m9", "c2", "What is Emotional Intelligence?", "Goleman's EQ framework: self-awareness, self-regulation, motivation, empathy, social skills.", "Y7m9eNoB3NU", 75, 0, true),
        Lesson("l37", "m9", "c2", "The Science of Emotions", "How the brain processes emotions: amygdala hijack, prefrontal cortex, and neuroplasticity.", "n9h8fG1DKhA", 80, 1, true),
        
        // Leadership (c3)
        Lesson("l44", "m12", "c3", "Leadership vs. Management", "Key differences, why both matter, and how to develop your leadership identity.", "qp0HIF3SfI4", 75, 0, true),
        Lesson("l45", "m12", "c3", "Leadership Styles", "Autocratic, democratic, laissez-faire, transformational, servant, and situational leadership.", "ReRcHdeUG9Y", 90, 1, true),
        
        // Marketing (c4)
        Lesson("l51", "m15", "c4", "Marketing Mix & Strategy", "The 4Ps and 7Ps, STP framework, market research, and competitive analysis.", "h95cQkEWBx0", 90, 0, true),
        
        // Public Speaking (c5)
        Lesson("l55", "m17", "c5", "Understanding Stage Fright", "Why we fear public speaking, the physiology of anxiety, and reframing nervousness.", "-FOCpMAww28", 70, 0, true),
        
        // Data Analytics (c6)
        Lesson("l61", "m20", "c6", "What is Business Analytics?", "Types of analytics, the data value chain, and the analyst mindset.", "rGx1QNdYzvs", 75, 0, true),
        
        // Negotiation (c7)
        Lesson("l65", "m22", "c7", "Negotiation Fundamentals", "Distributive vs. integrative negotiation, BATNA, ZOPA, and reservation price.", "MjhDkNmtjy0", 90, 0, true),
        
        // Science (c8)
        Lesson("l69", "m24", "c8", "What is the Scientific Method?", "Observation, hypothesis, experiment, analysis, and conclusion.", "HEfHFsfGXjs", 75, 0, true)
    )
    
    val currentUser = User(id = "u1", name = "Alex", email = "alex@skilltok.com", streak = 15, xp = 3200, level = 10)
}
