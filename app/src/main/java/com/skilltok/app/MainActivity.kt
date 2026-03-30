package com.skilltok.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
        setContent {
            var isDarkMode by remember { mutableStateOf(true) }
            SkillTokTheme(isDarkMode = isDarkMode) {
                MainScreen(isDarkMode = isDarkMode, onThemeToggle = { isDarkMode = it })
            }
        }
    }
}

object AppColors {
    val Background = Color(0xFF020617) // Slate 950
    val Card = Color(0xFF0F172A)       // Slate 900
    val Primary = Color(0xFF6366F1)    // Indigo 500
    val Accent = Color(0xFF8B5CF6)     // Violet 500
    val TextMuted = Color(0xFF94A3B8)  // Slate 400
    val Border = Color(0xFF1E293B)     // Slate 800
    
    val PrimaryGradient = Brush.horizontalGradient(listOf(Primary, Accent))

    // Light Theme
    val LightBackground = Color(0xFFF8FAFC)
    val LightCard = Color(0xFFFFFFFF)
}

@Composable
fun SkillTokTheme(isDarkMode: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = AppColors.Primary,
            secondary = AppColors.Accent,
            background = AppColors.Background,
            surface = AppColors.Card,
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = AppColors.Primary,
            secondary = AppColors.Accent,
            background = AppColors.LightBackground,
            surface = AppColors.LightCard,
            onPrimary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun MainScreen(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser by viewModel.currentUser.collectAsState()
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            isLoggedIn = firebaseAuth.currentUser != null
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route
            
            val hideBottomBar = !isLoggedIn || 
                               currentDestination == "create_course" || 
                               currentDestination?.startsWith("lesson/") == true || 
                               currentDestination?.startsWith("quiz/") == true ||
                               currentDestination?.startsWith("reels/") == true ||
                               currentDestination == "settings"
            
            if (!hideBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    NavigationBarItem(
                        selected = currentDestination == "home",
                        onClick = { navController.navigate("home") },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == "courses",
                        onClick = { navController.navigate("courses") },
                        icon = { Icon(Icons.Default.Book, "Courses") },
                        label = { Text("Courses") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == "notifications",
                        onClick = { navController.navigate("notifications") },
                        icon = { Icon(Icons.Default.Notifications, "Alerts") },
                        label = { Text("Alerts") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "auth",
            modifier = Modifier.padding(padding)
        ) {
            composable("auth") { AuthScreen(onLoginSuccess = { navController.navigate("home") }) }
            composable("home") { HomeFeedScreen(navController, viewModel) }
            composable("courses") { CoursesListScreen(navController, viewModel) }
            composable("notifications") { NotificationsScreen() }
            composable("profile") { 
                ProfileScreen(
                    user = currentUser,
                    onLogout = { 
                        viewModel.logout()
                        navController.navigate("auth") { popUpTo(0) }
                    },
                    onAddCourse = { navController.navigate("create_course") },
                    onSettingsClick = { navController.navigate("settings") }
                ) 
            }
            composable("settings") {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeToggle,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("course_detail/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                CourseDetailPage(courseId, navController, viewModel)
            }
            composable("create_course") {
                CreateCourseScreen(navController, viewModel)
            }
            composable("reels/{courseId}/{lessonId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                val lessonId = backStackEntry.arguments?.getString("lessonId")
                ReelsPlayerScreen(courseId, lessonId, navController, viewModel)
            }
            composable("lesson/{lessonId}") { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                LessonPlayerScreen(lessonId, navController, viewModel)
            }
            composable("quiz/{lessonId}") { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                QuizScreen(lessonId, navController, viewModel)
            }
        }
    }
}

@Composable
fun SettingsScreen(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit, onBack: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
            Text("Appearance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary, modifier = Modifier.padding(bottom = 16.dp))
            
            SettingsToggleItem(
                title = "Dark Mode",
                description = "Enable dark theme across the app",
                isChecked = isDarkMode,
                onCheckedChange = { 
                    onThemeToggle(it)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            var hapticEnabled by remember { mutableStateOf(true) }
            SettingsToggleItem(
                title = "Haptic Feedback",
                description = "Enable tactile vibration on interactions",
                isChecked = hapticEnabled,
                onCheckedChange = { 
                    hapticEnabled = it
                    if (it) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text("Learning Preferences", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary, modifier = Modifier.padding(bottom = 16.dp))
            
            var autoPlay by remember { mutableStateOf(true) }
            SettingsToggleItem(
                title = "Autoplay Reels",
                description = "Automatically play videos in the feed",
                isChecked = autoPlay,
                onCheckedChange = { autoPlay = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            var dataSaver by remember { mutableStateOf(false) }
            SettingsToggleItem(
                title = "Data Saver",
                description = "Optimize video resolution for low data",
                isChecked = dataSaver,
                onCheckedChange = { dataSaver = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionItem(title = "Push Notifications", icon = Icons.Default.Notifications)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionItem(title = "Security & 2FA", icon = Icons.Default.Security)
            
            Spacer(modifier = Modifier.height(40.dp))
            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Delete Account", color = Color.Red.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("SkillTok v1.0.42 (Beta)", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsToggleItem(title: String, description: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) AppColors.Primary.copy(alpha = 0.3f) else AppColors.Border.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsActionItem(title: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthManager = remember { GoogleAuthManager(context) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (isSignUp) "Create Account" else "Welcome Back", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                isLoading = true
                if (isSignUp) auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) onLoginSuccess()
                    else Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                else auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) onLoginSuccess()
                    else Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text(if (isSignUp) "Sign Up" else "Sign In")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = {
                scope.launch {
                    val result = googleAuthManager.signIn()
                    if (result != null) onLoginSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continue with Google", color = MaterialTheme.colorScheme.onBackground)
        }

        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up")
        }
    }
}
