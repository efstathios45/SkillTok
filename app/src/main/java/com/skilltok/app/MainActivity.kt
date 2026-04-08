package com.skilltok.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser by viewModel.userProfile.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(auth.currentUser, auth.currentUser?.isEmailVerified, currentUser) {
        val user = auth.currentUser
        val profile = currentUser
        if (user == null || !user.isEmailVerified) {
            if (currentRoute != "auth") {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (currentRoute == "auth") {
            if (profile != null && profile.id == user.uid) {
                if (profile.onboardingCompleted == false) {
                    navController.navigate("onboarding") { popUpTo(0) }
                } else {
                    navController.navigate("home") { popUpTo(0) }
                }
            }
        }
    }

    val isLoggedIn = auth.currentUser != null && auth.currentUser?.isEmailVerified == true
    val isAppReady = isLoggedIn && currentRoute != "onboarding" && currentRoute != "auth"
    
    val showBars = isAppReady && 
                      currentRoute != "create_course" &&
                      currentRoute?.startsWith("course_management/") == false &&
                      currentRoute?.startsWith("lesson/") == false &&
                      currentRoute?.startsWith("quiz/") == false &&
                      currentRoute?.startsWith("reels/") == false

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAppReady,
        drawerContent = {
            if (isAppReady) {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(280.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))) {
                        Column(modifier = Modifier.padding(24.dp).statusBarsPadding()) {
                            Text("SkillTok", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text("Your Learning Journey", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text("Home", fontSize = 14.sp) },
                        selected = currentRoute == "home",
                        onClick = { 
                            navController.navigate("home") { launchSingleTop = true }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Explore", fontSize = 14.sp) },
                        selected = currentRoute == "courses",
                        onClick = { 
                            navController.navigate("courses") { launchSingleTop = true }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Explore, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    
                    if (currentUser?.role == "professor") {
                        Text(
                            "Instructor Tools", 
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        NavigationDrawerItem(
                            label = { Text("Professor Dashboard", fontSize = 14.sp) },
                            selected = currentRoute == "professor_dashboard",
                            onClick = { 
                                navController.navigate("professor_dashboard")
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Dashboard, null, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Create Course", fontSize = 14.sp) },
                            selected = currentRoute == "create_course",
                            onClick = { 
                                navController.navigate("create_course")
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.AddBox, null, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(24.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }

                    NavigationDrawerItem(
                        label = { Text("My Courses", fontSize = 14.sp) },
                        selected = currentRoute == "my_courses",
                        onClick = { 
                            navController.navigate("my_courses")
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.School, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Saved Reels", fontSize = 14.sp) },
                        selected = currentRoute == "saved_videos",
                        onClick = { 
                            navController.navigate("saved_videos")
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Bookmark, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings", fontSize = 14.sp) },
                        selected = currentRoute == "settings",
                        onClick = { 
                            navController.navigate("settings")
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Settings, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = { Text("Sign Out", color = Color.Red, fontSize = 14.sp) },
                        selected = false,
                        onClick = { 
                            auth.signOut()
                            navController.navigate("auth") { popUpTo(0) }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.padding(12.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (showBars) {
                    Box(modifier = Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp)) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            },
            bottomBar = {
                if (showBars) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(80.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == "home",
                            onClick = { navController.navigate("home") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(24.dp)) },
                            label = { Text("Home", fontSize = 11.sp) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "courses",
                            onClick = { navController.navigate("courses") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(24.dp)) },
                            label = { Text("Explore", fontSize = 11.sp) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = { navController.navigate("profile") { launchSingleTop = true } },
                            icon = { Icon(Icons.Default.PersonOutline, null, modifier = Modifier.size(24.dp)) },
                            label = { Text("Profile", fontSize = 11.sp) }
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
                        auth.currentUser?.let { user ->
                            if (user.isEmailVerified) {
                                if (currentUser?.onboardingCompleted == false) navController.navigate("onboarding")
                                else navController.navigate("home")
                            }
                        }
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
                composable("professor_dashboard") { ProfessorDashboard(viewModel, navController) }
                composable("course_management/{courseId}") { CourseManagementScreen(it.arguments?.getString("courseId") ?: "", viewModel, navController) }
                composable("reels/{courseId}/{lessonId}") { 
                    ReelsPlayerScreen(
                        it.arguments?.getString("courseId") ?: "", 
                        it.arguments?.getString("lessonId"), 
                        navController, 
                        viewModel,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    ) 
                }
                composable("lesson/{lessonId}") { LessonPlayerScreen(it.arguments?.getString("lessonId") ?: "", navController, viewModel) }
                composable("quiz/{lessonId}") { QuizScreen(it.arguments?.getString("lessonId") ?: "", navController, viewModel) }
            }
        }
    }
}
