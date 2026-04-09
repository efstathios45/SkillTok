package com.skilltok.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
            
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
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
    
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().padding(horizontal = 24.dp)) {
        Text("Explore", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(courses.filter { it.title.contains(searchQuery, ignoreCase = true) || it.subject.contains(searchQuery, ignoreCase = true) }) { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("course_detail/${course.id}") },
                    verticalAlignment = Alignment.CenterVertically) {
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
fun ProfileScreen(user: User?, onLogout: () -> Unit, onAddCourse: () -> Unit, onSettingsClick: () -> Unit, navController: NavHostController) {
    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(72.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                Text(user.name.take(1), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("Level ${user.level} Learner", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
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
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ProfileMenuItem(Icons.Default.School, "My Courses", onClick = { navController.navigate("my_courses") })
        ProfileMenuItem(Icons.Default.Bookmark, "Saved Reels", onClick = { navController.navigate("saved_videos") })
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
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, color: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontWeight = FontWeight.Medium, color = color, fontSize = 15.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
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
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.loadReviews(courseId) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(280.dp),
                contentScale = ContentScale.Crop
            )
            
            Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(
                Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.3f)))
            ))

            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { /* share */ },
                    modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 240.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    CourseBadge(course.subject)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(course.level, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (isEnrolled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { navController.navigate("reels/${course.id}/resume") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("REELVIEW", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.unenrollFromCourse(course.id) },
                                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.PersonRemove, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(course.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(18.dp))
                    Text(" ${course.rating} • ${course.learnersCount} enrolled", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }

                if (isEnrolled) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate("course_management/${course.id}") },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Comment, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Discussion Forum", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("About", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Text(course.description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(28.dp))
                Text("Curriculum", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                
                modules.forEach { module ->
                    ModuleListItem(module, viewModel, navController)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Reviews", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isEnrolled) {
                        TextButton(onClick = { showReviewDialog = true }) {
                            Text("Write Review", fontSize = 13.sp)
                        }
                    }
                }
                
                if (courseReviews.isEmpty()) {
                    Text("No reviews yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                } else {
                    courseReviews.forEach { review ->
                        ReviewItem(review)
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }

            if (!isEnrolled) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { viewModel.enrollInCourse(course.id) },
                        modifier = Modifier.fillMaxWidth().padding(20.dp).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = enrollmentState !is EnrollmentState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (enrollmentState is EnrollmentState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Enroll for Free", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(28.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                Text(review.userName.take(1).uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, 
                            null, 
                            tint = if (index < review.rating) Color(0xFFFFB800) else Color.LightGray, 
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
        Text(review.comment, modifier = Modifier.padding(top = 6.dp, start = 38.dp), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    }
}

@Composable
fun AddReviewDialog(onDismiss: () -> Unit, onConfirm: (Int, String) -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                Icons.Default.Star, 
                                null, 
                                tint = if (index < rating) Color(0xFFFFB800) else Color.LightGray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(rating, comment) }, shape = RoundedCornerShape(10.dp)) {
                Text("Submit")
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
    
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(module.title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        lessons.forEach { lesson ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { navController.navigate("lesson/${lesson.id}") }
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayCircleOutline, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text(lesson.title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("${lesson.durationSeconds / 60}m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MyCoursesScreen(navController: NavHostController, viewModel: MainViewModel) {
    val enrollments by viewModel.enrollments.collectAsState()
    val allCourses by viewModel.courses.collectAsState()
    val myCourses = allCourses.filter { course -> enrollments.any { it.courseId == course.id } }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("My Learning", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        if (myCourses.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No courses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
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
    val savedLessons = MockData.lessons.filter { savedIds.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saved Reels", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        
        if (savedLessons.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.BookmarkBorder, 
                    null, 
                    modifier = Modifier.size(64.dp), 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No saved reels yet", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
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
                        Icon(
                            Icons.Default.PlayArrow, 
                            null, 
                            modifier = Modifier.align(Alignment.BottomStart).padding(6.dp).size(14.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
