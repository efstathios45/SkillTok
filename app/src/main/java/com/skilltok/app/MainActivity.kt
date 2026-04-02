package com.skilltok.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            SkillTokTheme(isDarkMode = isDarkMode) {
                MainScreen(isDarkMode = isDarkMode, onThemeToggle = { isDarkMode = it })
            }
        }
    }
}

@Composable
fun SkillTokTheme(isDarkMode: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF8B5CF6),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF8B5CF6),
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
fun MainScreen(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser by viewModel.userProfile.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(auth.currentUser, auth.currentUser?.isEmailVerified, currentUser, currentRoute) {
        val user = auth.currentUser
        val profile = currentUser
        if (user == null || !user.isEmailVerified) {
            if (currentRoute != "auth") {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (profile != null && profile.id == user.uid) {
            if (!profile.onboardingCompleted && currentRoute != "onboarding") {
                navController.navigate("onboarding") { popUpTo(0) }
            } else if (profile.onboardingCompleted && (currentRoute == "auth" || currentRoute == "onboarding")) {
                navController.navigate("home") { popUpTo(0) }
            }
        }
    }

    Scaffold(
        bottomBar = {
            val showBottomBar = auth.currentUser != null && 
                               auth.currentUser?.isEmailVerified == true &&
                               currentRoute != "onboarding" &&
                               currentRoute != "auth" &&
                               currentRoute != "create_course" &&
                               currentRoute?.startsWith("lesson/") == false &&
                               currentRoute?.startsWith("quiz/") == false &&
                               currentRoute?.startsWith("reels/") == false
            
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "courses",
                        onClick = { navController.navigate("courses") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Book, "Explore") },
                        label = { Text("Explore") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (auth.currentUser?.isEmailVerified == true) "home" else "auth",
            modifier = Modifier.padding(padding)
        ) {
            composable("auth") { 
                AuthScreen(viewModel = viewModel, onLoginSuccess = {
                    // Navigation after auth is handled centrally via auth/profile observer.
                }) 
            }
            composable("onboarding") { OnboardingScreen(navController, viewModel) }
            composable("home") { HomeFeedScreen(navController, viewModel) }
            composable("courses") { CoursesListScreen(navController, viewModel) }
            composable("profile") { 
                ProfileScreen(
                    user = currentUser,
                    onLogout = { 
                        auth.signOut()
                        navController.navigate("auth") { popUpTo(0) }
                    },
                    onAddCourse = { navController.navigate("create_course") },
                    onSettingsClick = { navController.navigate("settings") },
                    navController = navController
                ) 
            }
            composable("settings") { SettingsScreen(isDarkMode, onThemeToggle) { navController.popBackStack() } }
            composable("my_courses") { MyCoursesScreen(navController, viewModel) }
            composable("saved_videos") { SavedVideosScreen(navController, viewModel) }
            composable("course_detail/{courseId}") { CourseDetailPage(it.arguments?.getString("courseId") ?: "", navController, viewModel) }
            composable("create_course") { CreateCourseScreen(navController, viewModel) }
            composable("reels/{courseId}/{lessonId}") { ReelsPlayerScreen(it.arguments?.getString("courseId") ?: "", it.arguments?.getString("lessonId"), navController, viewModel) }
            composable("lesson/{lessonId}") { LessonPlayerScreen(it.arguments?.getString("lessonId") ?: "", navController, viewModel) }
            composable("quiz/{lessonId}") { QuizScreen(it.arguments?.getString("lessonId") ?: "", navController, viewModel) }
        }
    }
}
