package com.skilltok.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { step.toFloat() / 2 },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        AnimatedContent(targetState = step, label = "onboarding_steps") { currentStep ->
            if (currentStep == 1) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    }
}

@Composable
fun HomeFeedScreen(navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    if (courses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val pagerState = rememberPagerState(pageCount = { courses.size })
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) { page ->
            val course = courses[page]
            val isEnrolled = enrollments.any { it.courseId == course.id }
            
            Box(modifier = Modifier.fillMaxSize().clickable {
                navController.navigate("reels/${course.id}/resume")
            }) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0.4f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.9f)
                    )
                ))
                
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                        .padding(bottom = if (isLandscape) 0.dp else 80.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(if (isLandscape) 0.65f else 1f)) {
                        CourseBadge(course.subject, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(course.title, color = Color.White, fontSize = if (isLandscape) 24.sp else 32.sp, fontWeight = FontWeight.Black, lineHeight = if (isLandscape) 28.sp else 38.sp)
                        if (!isLandscape) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(course.description, color = Color.White.copy(alpha = 0.8f), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 15.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { navController.navigate("course_detail/${course.id}") },
                                modifier = Modifier.height(if (isLandscape) 44.dp else 52.dp).weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(if (isEnrolled) Icons.Default.PlayArrow else Icons.Default.AutoStories, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (isEnrolled) "Continue" else "Unlock", fontWeight = FontWeight.Bold)
                            }
                            
                            IconButton(
                                onClick = { /* share logic */ },
                                modifier = Modifier.size(if (isLandscape) 44.dp else 52.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(Icons.Default.Share, null, tint = Color.White)
                            }
                        }
                    }
                    if (isLandscape) {
                        Spacer(Modifier.width(24.dp))
                        Text(
                            course.description, 
                            color = Color.White.copy(alpha = 0.7f), 
                            modifier = Modifier.weight(0.35f).padding(bottom = 8.dp),
                            fontSize = 13.sp,
                            maxLines = 5
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseFeedItem(course: Course, onClick: () -> Unit) {
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isLandscape) {
            Row(modifier = Modifier.height(140.dp)) {
                Box(modifier = Modifier.width(200.dp).fillMaxHeight()) {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = course.subject, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                    Text(text = course.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                        Text(text = " ${course.rating}  •  ${course.learnersCount} learners", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            Column {
                Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = course.subject, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = course.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                        Text(text = " ${course.rating}  •  ${course.learnersCount} learners", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CoursesListScreen(navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.recommendedCourses.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val categories = remember(courses) { listOf("All") + courses.map { it.subject }.distinct().sorted() }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (!isLandscape) {
                Text("Discover Skills", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search skills...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
                if (isLandscape) {
                    Spacer(Modifier.width(16.dp))
                    Text("Skills", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
        
        val filteredCourses = courses.filter { 
            (selectedCategory == "All" || it.subject == selectedCategory) &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.subject.contains(searchQuery, ignoreCase = true))
        }

        if (filteredCourses.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No courses found matching your criteria.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = if (isLandscape) GridCells.Fixed(2) else GridCells.Fixed(1),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredCourses) { course ->
                    CourseFeedItem(course) {
                        navController.navigate("course_detail/${course.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit, onAddCourse: () -> Unit, onSettingsClick: () -> Unit, navController: NavHostController) {
    if (user == null) return
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                        Text(user.name.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
                Spacer(Modifier.width(24.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Level ${user.level} ${user.role.replaceFirstChar { it.uppercase() }}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                IconButton(onClick = onSettingsClick, modifier = Modifier.background(Color.Black.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Default.Settings, null)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(AppColors.PrimaryGradient))
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(110.dp).background(MaterialTheme.colorScheme.surface, CircleShape).padding(4.dp)) {
                        Box(modifier = Modifier.fillMaxSize().background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.name.take(1).uppercase(), fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Level ${user.level} ${user.role.replaceFirstChar { it.uppercase() }}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                IconButton(onClick = onSettingsClick, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Default.Settings, null, tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(user.xp.toString(), "Total XP", Modifier.weight(1f))
            StatCard(user.streak.toString(), "Streak", Modifier.weight(1f))
            StatCard(user.role.replaceFirstChar { it.uppercase() }, "Role", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Learning Hub", modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuItem(Icons.Default.School, "My Courses", onClick = { navController.navigate("my_courses") })
                ProfileMenuItem(Icons.Default.Bookmark, "Saved Reels", onClick = { navController.navigate("saved_videos") })
                ProfileMenuItem(Icons.Default.EmojiEvents, "Global Leaderboard", onClick = { navController.navigate("leaderboard") })
                if (user.role == "professor") {
                    ProfileMenuItem(Icons.Default.AddBox, "Instructor Tools", onClick = { navController.navigate("professor_dashboard") })
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
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
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, color: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontWeight = FontWeight.SemiBold, color = color, fontSize = 15.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun LeaderboardScreen(navController: NavHostController, viewModel: MainViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentUser by viewModel.userProfile.collectAsState()
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().background(AppColors.PrimaryGradient).padding(if (isLandscape) 12.dp else 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Text("Rankings", color = Color.White, fontSize = if (isLandscape) 20.sp else 24.sp, fontWeight = FontWeight.Black)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(leaderboard) { index, user ->
                val isMe = user.id == currentUser?.id
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMe) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isMe) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = when(index) {
                                0 -> Color(0xFFFFD700)
                                1 -> Color(0xFFC0C0C0)
                                2 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.width(32.dp)
                        )
                        
                        Box(modifier = Modifier.size(40.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Level ${user.level}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        Text(
                            "${user.xp} XP", 
                            fontWeight = FontWeight.ExtraBold, 
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
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
    val reviews by viewModel.reviews.collectAsState()
    val courseReviews = reviews[courseId] ?: emptyList()
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.loadReviews(courseId) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (!isLandscape) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxWidth().height(320.dp).background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent, Color.Black.copy(alpha = 0.4f)))
                ))
            }

            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { /* share */ },
                    modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (isLandscape) 60.dp else 280.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                if (isLandscape) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = course.thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                            Text(course.subject, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    CourseBadge(course.subject)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(course.level, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (isEnrolled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { navController.navigate("reels/${course.id}/resume") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CONTINUE", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.unenrollFromCourse(course.id) },
                                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.PersonRemove, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
                
                if (!isLandscape) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(course.title, fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, lineHeight = 34.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(Icons.Default.Star, null, tint = if (index < 4) Color(0xFFFFB800) else Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                    Text("  ${course.rating}  •  ${course.learnersCount} enrolled", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                if (isEnrolled) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp).clickable { navController.navigate("course_management/${course.id}") }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Comment, null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Student Community", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Join discussions and ask questions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("About this course", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                Text(course.description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp, fontSize = 15.sp)
                
                Spacer(modifier = Modifier.height(32.dp))
                Text("Curriculum", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                
                modules.forEach { module ->
                    ModuleListItem(module, viewModel, navController)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Student Reviews", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isEnrolled) {
                        TextButton(onClick = { showReviewDialog = true }) {
                            Text("Write Review", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                if (courseReviews.isEmpty()) {
                    Text("No reviews yet. Be the first to review!", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    courseReviews.forEach { review ->
                        ReviewItem(review)
                    }
                }
                
                Spacer(modifier = Modifier.height(120.dp))
            }

            if (!isEnrolled) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { viewModel.enrollInCourse(course.id) },
                        modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = enrollmentState !is EnrollmentState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (enrollmentState is EnrollmentState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Enroll Now • Free", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (showReviewDialog) {
            AddReviewDialog(
                onDismiss = { showReviewDialog = false },
                onConfirm = { rating, comment ->
                    viewModel.addReview(courseId, rating, comment)
                    showReviewDialog = false
                }
            )
        }
    }
}

@Composable
fun ReviewItem(review: CourseReview) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                Text(review.userName.take(1).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, 
                            null, 
                            tint = if (index < review.rating) Color(0xFFFFB800) else Color.LightGray, 
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
        Text(review.comment, modifier = Modifier.padding(top = 8.dp, start = 48.dp), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    }
}

@Composable
fun AddReviewDialog(onDismiss: () -> Unit, onConfirm: (Int, String) -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate your experience", fontSize = 20.sp, fontWeight = FontWeight.Black) },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(44.dp)) {
                            Icon(
                                Icons.Default.Star, 
                                null, 
                                tint = if (index < rating) Color(0xFFFFB800) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("What did you learn from this course?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(rating, comment) }, shape = RoundedCornerShape(12.dp)) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ModuleListItem(module: Module, viewModel: MainViewModel, navController: NavHostController) {
    val lessons by viewModel.getModuleLessons(module.id).collectAsState(initial = emptyList())
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.padding(vertical = 6.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(module.title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                    Text("${lessons.size} reels", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    lessons.forEach { lesson ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { navController.navigate("lesson/${lesson.id}") }
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayCircleOutline, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(lesson.title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${lesson.durationSeconds / 60}m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyCoursesScreen(navController: NavHostController, viewModel: MainViewModel) {
    val enrollments by viewModel.enrollments.collectAsState()
    val allCourses by viewModel.courses.collectAsState()
    val myCourses = allCourses.filter { course -> enrollments.any { it.courseId == course.id } }

    if (myCourses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.School, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You haven't enrolled in any courses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = { navController.navigate("courses") }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Explore Courses")
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(myCourses) { course ->
                val enrollment = enrollments.find { it.courseId == course.id }
                EnrolledCourseCard(course, enrollment?.progressPercent ?: 0) {
                    navController.navigate("course_detail/${course.id}")
                }
            }
        }
    }
}

@Composable
fun EnrolledCourseCard(course: Course, progress: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.toFloat() / 100 },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("$progress% completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SavedVideosScreen(navController: NavHostController, viewModel: MainViewModel) {
    val savedLessons by viewModel.savedLessons.collectAsState()

    if (savedLessons.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No saved reels yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(horizontal = 1.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(savedLessons) { lesson ->
                Box(
                    modifier = Modifier
                        .aspectRatio(9f/16f)
                        .clickable { navController.navigate("reels/${lesson.courseId}/${lesson.id}") }
                ) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/${lesson.videoUrl}/hqdefault.jpg",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)))))
                    Icon(
                        Icons.Default.PlayArrow, 
                        null, 
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
