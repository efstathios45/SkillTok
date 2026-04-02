package com.skilltok.app

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController, viewModel: MainViewModel) {
    var step by remember { mutableIntStateOf(1) }
    val interests = listOf("Leadership", "Marketing", "Technology", "Communication", "Human Resources", "Psychology", "Business", "Science")
    val goals = listOf("Career Growth", "Personal Development", "Skill Certification", "Entrepreneurship", "Academic Support")
    
    val selectedInterests = remember { mutableStateListOf<String>() }
    val selectedGoals = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { step.toFloat() / 2 },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (step == 1) {
            Text("What are you interested in?", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text("Select at least 3 topics to personalize your feed", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                interests.forEach { item ->
                    val isSelected = selectedInterests.contains(item)
                    FilterChip(
                        selected = isSelected,
                        onClick = { if (isSelected) selectedInterests.remove(item) else selectedInterests.add(item) },
                        label = { Text(item, modifier = Modifier.padding(8.dp)) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            Button(
                onClick = { step = 2 },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedInterests.size >= 3,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Next")
            }
        } else {
            Text("What are your learning goals?", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text("Help us find the right path for you", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                goals.forEach { item ->
                    val isSelected = selectedGoals.contains(item)
                    Surface(
                        onClick = { if (isSelected) selectedGoals.remove(item) else selectedGoals.add(item) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(item, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            
            Button(
                onClick = { 
                    viewModel.completeOnboarding(selectedInterests.toList(), selectedGoals.toList())
                    navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedGoals.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Get Started")
            }
        }
    }
}

@Composable
fun HomeFeedScreen(navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val pagerState = rememberPagerState(pageCount = { courses.size })
    
    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) { page ->
        val course = courses[page]
        Box(modifier = Modifier.fillMaxSize().clickable {
            navController.navigate("reels/${course.id}/resume")
        }) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient Overlay
            Box(modifier = Modifier.fillMaxSize().background(
                androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
            ))
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
                    .padding(bottom = 80.dp)
            ) {
                CourseBadge(course.subject)
                Spacer(modifier = Modifier.height(12.dp))
                Text(course.title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(course.description, color = Color.White.copy(alpha = 0.8f), maxLines = 3, overflow = TextOverflow.Ellipsis)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { navController.navigate("course_detail/${course.id}") },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("View Course", fontWeight = FontWeight.Bold)
                    }
                    
                    IconButton(
                        onClick = { /* share logic */ },
                        modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CourseFeedItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                    CourseBadge(course.subject)
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = course.title, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                    Text(
                        text = " ${course.rating} • ${course.learnersCount} learners", 
                        fontSize = 14.sp, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CoursesListScreen(navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Text("Explore", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search skills...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(courses.filter { it.title.contains(searchQuery, ignoreCase = true) || it.subject.contains(searchQuery, ignoreCase = true) }) { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("course_detail/${course.id}") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.title, 
                            fontWeight = FontWeight.Bold, 
                            maxLines = 1, 
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = course.subject, 
                            fontSize = 14.sp, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen() {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Text("Alerts", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No notifications yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit, onAddCourse: () -> Unit, onSettingsClick: () -> Unit, navController: NavHostController) {
    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                Text(user.name.take(1), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("Level ${user.level} Learner", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(user.xp.toString(), "Total XP", Modifier.weight(1f))
            StatCard(user.streak.toString(), "Day Streak", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Account", 
            modifier = Modifier.padding(horizontal = 24.dp), 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileMenuItem(Icons.Default.School, "My Courses", onClick = { navController.navigate("my_courses") })
        ProfileMenuItem(Icons.Default.WorkspacePremium, "Certificates")
        ProfileMenuItem(Icons.Default.AddBox, "Create a Course", onClick = onAddCourse)
        ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Sign Out", color = Color.Red, onClick = onLogout)
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, color: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontWeight = FontWeight.Medium, color = color)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailPage(courseId: String, navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: return
    val modules by viewModel.getCourseModules(courseId).collectAsState(initial = emptyList())
    val isEnrolled = viewModel.isEnrolled(courseId)
    val enrollmentState by viewModel.enrollmentState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(enrollmentState) {
        when (enrollmentState) {
            is EnrollmentState.Success -> {
                snackbarHostState.showSnackbar("Successfully enrolled in ${course.title}")
                viewModel.resetEnrollmentState()
            }
            is EnrollmentState.Error -> {
                snackbarHostState.showSnackbar((enrollmentState as EnrollmentState.Error).message)
                viewModel.resetEnrollmentState()
            }
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { scaffoldPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
            ) {
                // Immersive hero — full-bleed image with gradient overlay + back button + badges
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay (darker at top for nav, darker at bottom for text)
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                    )

                    // Back button (top-start, above status bar)
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    }

                    // Title & metadata at bottom of hero
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CourseBadge(course.subject)
                            CourseBadge(course.level)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            course.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                            Text(
                                " ${course.rating}  •  ${course.learnersCount} learners",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // REELVIEW / Enroll button floating at top-end when enrolled
                    if (isEnrolled) {
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Button(
                                onClick = { navController.navigate("reels/${course.id}/resume") },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Watch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // Course body
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        course.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Stats row
                    val lessonCount = MockData.lessons.count { it.courseId == courseId }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CourseStatChip(Icons.Default.School, "${modules.size} modules", Modifier.weight(1f))
                        CourseStatChip(Icons.Default.AccessTime, "$lessonCount lessons", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Curriculum", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    modules.forEach { module ->
                        ModuleListItem(module, viewModel, navController)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (!isEnrolled) {
                        Button(
                            onClick = { viewModel.enrollInCourse(course.id) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = enrollmentState !is EnrollmentState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (enrollmentState is EnrollmentState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.School, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Enroll Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = { navController.navigate("reels/${course.id}/resume") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Continue Learning", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CourseStatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ModuleListItem(module: Module, viewModel: MainViewModel, navController: NavHostController) {
    val lessons by viewModel.getModuleLessons(module.id).collectAsState(initial = emptyList())
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(module.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        lessons.forEach { lesson ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("lesson/${lesson.id}") }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayCircleOutline, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(lesson.title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                Text("${lesson.durationSeconds / 60}m", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MyCoursesScreen(navController: NavHostController, viewModel: MainViewModel) {
    val enrollments by viewModel.enrollments.collectAsState()
    val allCourses by viewModel.courses.collectAsState()
    val myCourses = allCourses.filter { course -> enrollments.any { it.courseId == course.id } }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("My Learning", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (myCourses.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("You haven't enrolled in any courses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(myCourses) { course ->
                    CourseFeedItem(course) {
                        navController.navigate("course_detail/${course.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun SavedVideosScreen(navController: NavHostController, viewModel: MainViewModel) {
    val savedIds by viewModel.savedVideos.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("Saved Reels", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        if (savedIds.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text("No saved videos yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Text("You have ${savedIds.size} saved videos.", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
