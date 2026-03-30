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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun HomeFeedScreen(navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(
            "For You", 
            modifier = Modifier.padding(24.dp), 
            fontSize = 28.sp, 
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(courses) { course ->
                CourseFeedItem(course) {
                    navController.navigate("reels/${course.id}/resume")
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
                Text(course.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                    Text(" ${course.rating} • ${course.learnersCount} learners", fontSize = 14.sp, color = AppColors.TextMuted)
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
        Text("Explore", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search skills...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) }
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
                        Text(course.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(course.subject, fontSize = 14.sp, color = AppColors.TextMuted)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.TextMuted)
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen() {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Text("Alerts", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(80.dp), tint = AppColors.TextMuted)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No notifications yet", color = AppColors.TextMuted)
        }
    }
}

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit, onAddCourse: () -> Unit, onSettingsClick: () -> Unit) {
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
                Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Level ${user.level} Learner", color = AppColors.Primary, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, null)
            }
        }

        Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(user.xp.toString(), "Total XP", Modifier.weight(1f))
            StatCard(user.streak.toString(), "Day Streak", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Account", modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Bold, color = AppColors.Primary)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileMenuItem(Icons.Default.School, "My Courses")
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = AppColors.TextMuted)
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
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.TextMuted.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailPage(courseId: String, navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: return
    val modules by viewModel.getCourseModules(courseId).collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(modifier = Modifier.padding(24.dp)) {
                CourseBadge(course.subject)
                Spacer(modifier = Modifier.height(12.dp))
                Text(course.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(course.description, color = AppColors.TextMuted, lineHeight = 24.sp)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Curriculum", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                modules.forEach { module ->
                    ModuleListItem(module, viewModel, navController)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        viewModel.enrollInCourse(course.id, "first")
                        navController.navigate("reels/${course.id}/resume")
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Start Learning", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ModuleListItem(module: Module, viewModel: MainViewModel, navController: NavHostController) {
    val lessons by viewModel.getModuleLessons(module.id).collectAsState(initial = emptyList())
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(module.title, fontWeight = FontWeight.Bold, color = AppColors.Primary)
        Spacer(modifier = Modifier.height(8.dp))
        lessons.forEach { lesson ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("lesson/${lesson.id}") }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayCircleOutline, null, tint = AppColors.TextMuted, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(lesson.title, modifier = Modifier.weight(1f))
                Text("${lesson.durationSeconds / 60}m", fontSize = 12.sp, color = AppColors.TextMuted)
            }
        }
    }
}
