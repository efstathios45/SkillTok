@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
package com.skilltok.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import java.util.UUID

// --- Data Structures for Professor Tools ---
data class ModuleData(val id: String = UUID.randomUUID().toString(), val title: String, val lessons: List<LessonData> = emptyList())
data class LessonData(val id: String = UUID.randomUUID().toString(), val title: String = "", val description: String = "", val videoUrl: String = "", val quiz: List<QuizQuestionData> = emptyList())
data class QuizQuestionData(val id: String = UUID.randomUUID().toString(), val question: String = "", val options: List<String> = listOf("", "", "", ""), val correctIndexes: List<Int> = listOf(0), val isMultipleChoice: Boolean = false)

@Composable
fun CreateCourseScreen(navController: NavHostController, viewModel: MainViewModel) {
    var step by remember { mutableStateOf("info") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Technology") }
    var level by remember { mutableStateOf("Beginner") }
    
    val subjects = listOf("Technology", "Design", "Business", "Marketing", "Science", "Psychology")
    val levels = listOf("Beginner", "Intermediate", "Advanced")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Design Your Course", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (step == "info") {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    if (thumbnailUrl.startsWith("http")) {
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                Text("Preview Image (Paste URL below)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = thumbnailUrl,
                    onValueChange = { thumbnailUrl = it },
                    label = { Text("Image URL (Unsplash/Web)") },
                    placeholder = { Text("https://images.unsplash.com/photo...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Link, null) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    subjects.forEach { s ->
                        FilterChip(
                            selected = subject == s,
                            onClick = { subject = s },
                            label = { Text(s) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { step = "modules" },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = title.isNotBlank() && thumbnailUrl.startsWith("http")
                ) {
                    Text("Next: Add Curriculum", fontWeight = FontWeight.Bold)
                }
            } else {
                ModulesStepScreen(
                    onBack = { step = "info" },
                    onPublish = { modules ->
                        viewModel.createCourse(title, description, subject, level, modules)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun ModulesStepScreen(onBack: () -> Unit, onPublish: (List<ModuleData>) -> Unit) {
    val modules = remember { mutableStateListOf(ModuleData(title = "Getting Started")) }

    Column {
        Text("Curriculum Architect", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Add modules, lessons, and craft deep quizzes", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(24.dp))

        modules.forEachIndexed { mIndex, module ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = module.title,
                            onValueChange = { modules[mIndex] = module.copy(title = it) },
                            label = { Text("Module Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        IconButton(onClick = { if (modules.size > 1) modules.removeAt(mIndex) }) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    module.lessons.forEachIndexed { lIndex, lesson ->
                        LessonEditItem(
                            lesson = lesson,
                            onUpdate = { updated ->
                                val lessons = module.lessons.toMutableList()
                                lessons[lIndex] = updated
                                modules[mIndex] = module.copy(lessons = lessons)
                            },
                            onRemove = {
                                val lessons = module.lessons.toMutableList()
                                lessons.removeAt(lIndex)
                                modules[mIndex] = module.copy(lessons = lessons)
                            }
                        )
                    }
                    
                    TextButton(onClick = {
                        val lessons = module.lessons.toMutableList()
                        lessons.add(LessonData(title = "New Lesson"))
                        modules[mIndex] = module.copy(lessons = lessons)
                    }) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Text("Add Reel (Lesson)")
                    }
                }
            }
        }

        Button(
            onClick = { modules.add(ModuleData(title = "Module ${modules.size + 1}")) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add Another Module")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Back") }
            Button(onClick = { onPublish(modules) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Publish Live") }
        }
    }
}

@Composable
fun LessonEditItem(lesson: LessonData, onUpdate: (LessonData) -> Unit, onRemove: () -> Unit) {
    var showQuizEditor by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                TextField(
                    value = lesson.title,
                    onValueChange = { onUpdate(lesson.copy(title = it)) },
                    placeholder = { Text("Lesson Name") },
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
                TextField(
                    value = lesson.videoUrl,
                    onValueChange = { onUpdate(lesson.copy(videoUrl = it)) },
                    placeholder = { Text("YouTube ID (e.g. dQw4w9WgXcQ)") },
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }
            IconButton(onClick = onRemove) { Icon(Icons.Default.Close, null, tint = Color.Gray) }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { showQuizEditor = !showQuizEditor }) {
                Icon(if(lesson.quiz.isEmpty()) Icons.Default.Quiz else Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = if(lesson.quiz.isNotEmpty()) Color(0xFF10B981) else MaterialTheme.colorScheme.primary)
                Text(if(lesson.quiz.isEmpty()) "Craft Quiz" else "Quiz Crafted (${lesson.quiz.size} Questions)")
            }
        }

        if (showQuizEditor) {
            QuizArchitect(
                questions = lesson.quiz,
                onUpdate = { onUpdate(lesson.copy(quiz = it)) }
            )
        }
    }
}

@Composable
fun QuizArchitect(questions: List<QuizQuestionData>, onUpdate: (List<QuizQuestionData>) -> Unit) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        questions.forEachIndexed { qIdx, q ->
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Question ${qIdx + 1}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        
                        Text("Multi-Correct", fontSize = 11.sp)
                        Switch(
                            checked = q.isMultipleChoice,
                            onCheckedChange = { 
                                val newList = questions.toMutableList()
                                newList[qIdx] = q.copy(isMultipleChoice = it, correctIndexes = listOf(0))
                                onUpdate(newList)
                            },
                            modifier = Modifier.scale(0.7f)
                        )

                        IconButton(onClick = {
                            val newList = questions.toMutableList()
                            newList.removeAt(qIdx)
                            onUpdate(newList)
                        }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.Red) }
                    }
                    OutlinedTextField(
                        value = q.question,
                        onValueChange = { 
                            val newList = questions.toMutableList()
                            newList[qIdx] = q.copy(question = it)
                            onUpdate(newList)
                        },
                        placeholder = { Text("Question Text") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Options (Mark the Correct ones)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    q.options.forEachIndexed { oIdx, option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (q.isMultipleChoice) {
                                Checkbox(
                                    checked = q.correctIndexes.contains(oIdx),
                                    onCheckedChange = { isChecked ->
                                        val newIndexes = q.correctIndexes.toMutableList()
                                        if (isChecked) newIndexes.add(oIdx) else newIndexes.remove(oIdx)
                                        val newList = questions.toMutableList()
                                        newList[qIdx] = q.copy(correctIndexes = newIndexes)
                                        onUpdate(newList)
                                    }
                                )
                            } else {
                                RadioButton(
                                    selected = q.correctIndexes.contains(oIdx),
                                    onClick = {
                                        val newList = questions.toMutableList()
                                        newList[qIdx] = q.copy(correctIndexes = listOf(oIdx))
                                        onUpdate(newList)
                                    }
                                )
                            }
                            TextField(
                                value = option,
                                onValueChange = {
                                    val newOptions = q.options.toMutableList()
                                    newOptions[oIdx] = it
                                    val newList = questions.toMutableList()
                                    newList[qIdx] = q.copy(options = newOptions)
                                    onUpdate(newList)
                                },
                                placeholder = { Text("Option ${oIdx + 1}") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = { 
                val newList = questions.toMutableList()
                newList.add(QuizQuestionData())
                onUpdate(newList)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Another Question")
        }
    }
}

@Composable
fun ProfessorDashboard(viewModel: MainViewModel, navController: NavHostController) {
    val user by viewModel.userProfile.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val myCourses = courses.filter { it.createdByUserId == user?.id }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().padding(24.dp)) {
        Text("Professor Command Center", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text("Manage your academy and track students", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (myCourses.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AutoStories, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ready to share your knowledge?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { navController.navigate("create_course") }, modifier = Modifier.padding(top = 24.dp), shape = RoundedCornerShape(16.dp)) {
                        Text("Create Your First Course")
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(myCourses) { course ->
                    ProfessorCourseCard(course, onClick = { 
                        navController.navigate("course_management/${course.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun ProfessorCourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${course.learnersCount} Enrolled", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun CourseManagementScreen(courseId: String, viewModel: MainViewModel, navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Analytics", "Forum", "Announcements")
    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp, containerColor = MaterialTheme.colorScheme.background) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }
            
            when (selectedTab) {
                0 -> AnalyticsTab(courseId, viewModel)
                1 -> ForumTab(courseId, viewModel)
                2 -> AnnouncementsTab(courseId, viewModel)
            }
        }
    }
}

@Composable
fun AnalyticsTab(courseId: String, viewModel: MainViewModel) {
    val participants by viewModel.participants.collectAsState()
    val courseParticipants = participants[courseId] ?: emptyList()

    LaunchedEffect(courseId) { viewModel.loadCourseManagementData(courseId) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Active Students", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (courseParticipants.isEmpty()) {
            Text("No students enrolled yet.", color = Color.Gray)
        } else {
            LazyColumn {
                items(courseParticipants) { student ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Text(student.userId.take(1).uppercase(), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Student ${student.userId.take(5)}", fontWeight = FontWeight.Bold)
                                LinearProgressIndicator(progress = { student.progressPercent.toFloat() / 100 }, modifier = Modifier.fillMaxWidth().height(4.dp))
                            }
                            Text("${student.progressPercent}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForumTab(courseId: String, viewModel: MainViewModel) {
    val topics by viewModel.forumTopics.collectAsState()
    val courseTopics = topics[courseId] ?: emptyList()
    var showCreateTopic by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.loadCourseManagementData(courseId) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Discussion Threads", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (courseTopics.isEmpty()) {
            Text("No topics yet. Start a discussion!", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(courseTopics) { topic ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(topic.title, fontWeight = FontWeight.Bold)
                            Text(topic.content, fontSize = 13.sp, color = Color.Gray)
                            Text("By ${topic.userName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        
        Button(onClick = { showCreateTopic = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Create New Topic")
        }
    }

    if (showCreateTopic) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateTopic = false },
            title = { Text("New Topic") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                }
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.createForumTopic(courseId, title, content)
                    showCreateTopic = false 
                }) { Text("Post") }
            }
        )
    }
}

@Composable
fun AnnouncementsTab(courseId: String, viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Broadcast Message") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Button(onClick = { 
            viewModel.sendClassNotification(courseId, "Class Announcement", text)
            text = ""
        }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Send Announcement")
        }
    }
}
