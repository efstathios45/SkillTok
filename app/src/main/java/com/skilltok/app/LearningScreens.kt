@file:OptIn(ExperimentalMaterial3Api::class)
package com.skilltok.app

import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Seek Overlay State ──────────────────────────────────────────────────────

private enum class SeekDirection { BACK, FORWARD, NONE }

@Composable
fun LessonPlayerScreen(lessonId: String, navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val courses by viewModel.courses.collectAsState()
    val lesson = MockData.lessons.find { it.id == lessonId }
    val course = courses.find { it.id == lesson?.courseId }
    val mod = MockData.modules.find { it.id == lesson?.moduleId }
    
    if (lesson == null || course == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(course.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(mod?.title ?: "", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            YouTubePlayer(videoId = lesson.videoUrl, isMutedGlobal = false, onMuteToggle = {})

            Column(modifier = Modifier.padding(20.dp)) {
                Text(lesson.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CourseBadge(course.subject)
                    CourseBadge("${lesson.durationSeconds / 60}m")
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(lesson.description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp, fontSize = 15.sp)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Key Takeaways", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("• Master the core concepts explained in this video.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Apply these insights to real-world scenarios for better retention.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { 
                        viewModel.completeLesson(lesson.id)
                        SoundManager.playComplete()
                        Toast.makeText(context, "Great! +25 XP", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Complete Lesson", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                if (lesson.hasQuiz) {
                    OutlinedButton(
                        onClick = { navController.navigate("quiz/${lesson.id}") },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Take Quiz", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ReelsPlayerScreen(courseId: String, startLessonId: String?, navController: NavHostController, viewModel: MainViewModel) {
    val courses by viewModel.courses.collectAsState()
    val course = courses.find { it.id == courseId } ?: return
    
    val lessonsFlow = remember(courseId) { viewModel.getModuleLessonsForReels(courseId) }
    val lessons by lessonsFlow.collectAsState(initial = emptyList())
    
    if (lessons.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val initialPage = if (startLessonId != null && startLessonId != "first" && startLessonId != "resume") {
        lessons.indexOfFirst { it.id == startLessonId }.coerceAtLeast(0)
    } else 0
    
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { lessons.size })
    var isMutedGlobal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            val lesson = lessons[page]
            val isCurrentPage = pagerState.currentPage == page
            ReelItem(
                lesson = lesson, 
                course = course, 
                navController = navController, 
                viewModel = viewModel, 
                isPlaying = isCurrentPage,
                isMutedGlobal = isMutedGlobal,
                onMuteToggle = { isMutedGlobal = it }
            )
        }

        // Immersive Top Header with course info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        course.title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                    Text(
                        "${pagerState.currentPage + 1} / ${lessons.size} lessons",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / lessons.size },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = AppColors.Primary,
                trackColor = Color.White.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun ReelItem(
    lesson: Lesson, 
    course: Course, 
    navController: NavHostController, 
    viewModel: MainViewModel, 
    isPlaying: Boolean,
    isMutedGlobal: Boolean,
    onMuteToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    val likes by viewModel.likes.collectAsState()
    val isLiked = likes.contains(lesson.id)
    
    val savedVideos by viewModel.savedVideos.collectAsState()
    val isSaved = savedVideos.contains(lesson.id)
    
    var showHeartAnim by remember { mutableStateOf(false) }
    var isManuallyPaused by remember { mutableStateOf(false) }
    
    val enrollments by viewModel.enrollments.collectAsState()
    val isEnrolled = enrollments.any { it.courseId == course.id }
    
    val comments by viewModel.comments.collectAsState()
    val lessonComments = comments[lesson.id] ?: emptyList()
    var showComments by remember { mutableStateOf(false) }

    // Seek overlay state
    var seekDirection by remember { mutableStateOf(SeekDirection.NONE) }
    var seekTapCount by remember { mutableIntStateOf(0) }

    // Exposed WebView reference for seeking
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "discRotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        YouTubePlayer(
            videoId = lesson.videoUrl,
            modifier = Modifier.fillMaxSize(),
            isReel = true,
            isPlaying = isPlaying && !isManuallyPaused && !showComments,
            isMutedGlobal = isMutedGlobal,
            onMuteToggle = onMuteToggle,
            onWebViewReady = { webViewRef.value = it }
        )

        // YouTube-style tap zones overlay
        Row(modifier = Modifier.fillMaxSize()) {
            // Left zone — seek back 10s
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                webViewRef.value?.evaluateJavascript("seek(-10)", null)
                                SoundManager.playSeek()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                seekDirection = SeekDirection.BACK
                                seekTapCount++
                                scope.launch { delay(800); if (seekDirection == SeekDirection.BACK) { seekDirection = SeekDirection.NONE; seekTapCount = 0 } }
                            },
                            onDoubleTap = {
                                webViewRef.value?.evaluateJavascript("seek(-10)", null)
                                SoundManager.playSeek()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                seekDirection = SeekDirection.BACK
                                seekTapCount = (seekTapCount + 1).coerceAtMost(5)
                                scope.launch { delay(800); if (seekDirection == SeekDirection.BACK) { seekDirection = SeekDirection.NONE; seekTapCount = 0 } }
                            },
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isManuallyPaused = true
                            }
                        )
                    }
            )

            // Center zone — tap to pause/play, double-tap to like
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isManuallyPaused = !isManuallyPaused
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDoubleTap = {
                                if (!isLiked) {
                                    viewModel.toggleLike(lesson.id)
                                    SoundManager.playLike()
                                }
                                showHeartAnim = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch { delay(800); showHeartAnim = false }
                            },
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isManuallyPaused = true
                            }
                        )
                    }
            )

            // Right zone — seek forward 10s
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                webViewRef.value?.evaluateJavascript("seek(10)", null)
                                SoundManager.playSeek()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                seekDirection = SeekDirection.FORWARD
                                seekTapCount++
                                scope.launch { delay(800); if (seekDirection == SeekDirection.FORWARD) { seekDirection = SeekDirection.NONE; seekTapCount = 0 } }
                            },
                            onDoubleTap = {
                                webViewRef.value?.evaluateJavascript("seek(10)", null)
                                SoundManager.playSeek()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                seekDirection = SeekDirection.FORWARD
                                seekTapCount = (seekTapCount + 1).coerceAtMost(5)
                                scope.launch { delay(800); if (seekDirection == SeekDirection.FORWARD) { seekDirection = SeekDirection.NONE; seekTapCount = 0 } }
                            },
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isManuallyPaused = true
                            }
                        )
                    }
            )
        }

        // Left seek animation (YouTube style ripple)
        AnimatedVisibility(
            visible = seekDirection == SeekDirection.BACK,
            enter = fadeIn(tween(80)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            SeekIndicator(forward = false, tapCount = seekTapCount)
        }

        // Right seek animation
        AnimatedVisibility(
            visible = seekDirection == SeekDirection.FORWARD,
            enter = fadeIn(tween(80)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SeekIndicator(forward = true, tapCount = seekTapCount)
        }

        // Center double-tap heart
        AnimatedVisibility(
            visible = showHeartAnim,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(110.dp))
        }

        // Pause indicator
        AnimatedVisibility(
            visible = isManuallyPaused,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(50.dp))
            }
        }

        // Bottom gradient overlay
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                0.55f to Color.Transparent,
                0.82f to Color.Black.copy(alpha = 0.45f),
                1.0f to Color.Black.copy(alpha = 0.92f)
            )
        ))

        // Bottom-left info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth(0.82f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Box(modifier = Modifier.size(40.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                        Text(course.subject.take(1), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    if (!isEnrolled) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .background(Color.White, CircleShape)
                                .clickable {
                                    viewModel.enrollInCourse(course.id, lesson.id)
                                    SoundManager.playEnroll()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, null, tint = AppColors.Primary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(course.subject, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(lesson.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(lesson.description, color = Color.White.copy(alpha = 0.88f), fontSize = 15.sp, maxLines = 2, lineHeight = 22.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { 
                        viewModel.completeLesson(lesson.id)
                        SoundManager.playComplete()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "Lesson Completed! +25 XP", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete", fontWeight = FontWeight.Bold)
                }
                
                if (lesson.hasQuiz) {
                    Button(
                        onClick = { navController.navigate("quiz/${lesson.id}") },
                        modifier = Modifier.height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Quiz, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quiz", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Interaction Sidebar
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            InteractionItem(
                icon = Icons.Default.Favorite, 
                label = if (isLiked) "Liked" else "Like", 
                isActive = isLiked, 
                activeColor = Color.Red,
                onClick = { 
                    viewModel.toggleLike(lesson.id)
                    SoundManager.playLike()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            InteractionItem(
                icon = Icons.AutoMirrored.Filled.Comment, 
                label = if (lessonComments.isEmpty()) "Chat" else lessonComments.size.toString(),
                onClick = { 
                    showComments = true 
                    SoundManager.playComment()
                    viewModel.loadComments(lesson.id)
                }
            )
            InteractionItem(
                icon = if (isMutedGlobal) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                label = if (isMutedGlobal) "Muted" else "Mute",
                isActive = isMutedGlobal,
                activeColor = AppColors.Primary,
                onClick = { onMuteToggle(!isMutedGlobal) }
            )
            InteractionItem(
                icon = Icons.Default.Bookmark, 
                label = if (isSaved) "Saved" else "Save", 
                isActive = isSaved, 
                activeColor = Color(0xFFFFD700),
                onClick = { 
                    viewModel.toggleSave(lesson.id)
                    SoundManager.playSave()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            InteractionItem(
                icon = Icons.Default.Share, 
                label = "Share",
                onClick = { 
                    Toast.makeText(context, "Shared!", Toast.LENGTH_SHORT).show()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            
            // Rotating Course Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .rotate(discRotation)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(AppColors.PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        if (showComments) {
            CommentsBottomSheet(
                lessonId = lesson.id,
                comments = lessonComments,
                onDismiss = { showComments = false },
                onSendComment = { 
                    viewModel.addComment(lesson.id, it)
                    SoundManager.playComment()
                }
            )
        }
    }
}

// ─── YouTube-style seek overlay ──────────────────────────────────────────────

@Composable
private fun SeekIndicator(forward: Boolean, tapCount: Int) {
    val seconds = tapCount * 10
    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 140.dp)
            .clip(
                if (forward)
                    RoundedCornerShape(topStart = 120.dp, bottomStart = 120.dp)
                else
                    RoundedCornerShape(topEnd = 120.dp, bottomEnd = 120.dp)
            )
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (forward) Icons.Default.Forward10 else Icons.Default.Replay10,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (seconds > 0) "$seconds sec" else "${if (forward) "+" else "-"}10 sec",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CommentsBottomSheet(
    lessonId: String,
    comments: List<ReelComment>,
    onDismiss: () -> Unit,
    onSendComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.6f).padding(16.dp)) {
            Text("Comments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(comments) { comment ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Box(modifier = Modifier.size(32.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
                            Text(comment.userName.ifBlank { "U" }.take(1).uppercase(), color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(comment.userName.ifBlank { "Learner" }, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(comment.text, fontSize = 14.sp)
                        }
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { 
                    if (commentText.isNotBlank()) {
                        onSendComment(commentText)
                        commentText = ""
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InteractionItem(
    icon: ImageVector, 
    label: String, 
    isActive: Boolean = false, 
    activeColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = label, 
                tint = if (isActive) activeColor else Color.White, 
                modifier = Modifier.size(28.dp).scale(if (isActive) 1.1f else 1.0f)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun YouTubePlayer(
    videoId: String, 
    modifier: Modifier = Modifier,
    isReel: Boolean = false, 
    isPlaying: Boolean = true,
    isMutedGlobal: Boolean = false,
    onMuteToggle: (Boolean) -> Unit = {},
    onWebViewReady: (WebView) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isReady by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableFloatStateOf(0f) }
    
    val html = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body { margin: 0; background-color: black; overflow: hidden; }
                .container { position: relative; width: 100vw; height: 100vh; }
                iframe { width: 100%; height: 100%; border: none; pointer-events: none; }
            </style>
            <script src="https://www.youtube.com/iframe_api"></script>
        </head>
        <body>
            <div class="container">
                <iframe id="player"
                    src="https://www.youtube-nocookie.com/embed/$videoId?autoplay=0&mute=0&controls=0&modestbranding=1&rel=0&showinfo=0&loop=1&playlist=$videoId&playsinline=1&enablejsapi=1&origin=https://www.youtube-nocookie.com&iv_load_policy=3" 
                    allow="autoplay; encrypted-media" 
                    allowfullscreen>
                </iframe>
            </div>
            <script>
                var player;
                var ready = false;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        events: {
                            'onReady': function(event) {
                                ready = true;
                                window.Android.onReady();
                                if ($isMutedGlobal) event.target.mute();
                                setInterval(updateProgress, 1000);
                            },
                            'onStateChange': function(event) {
                                if (event.data == YT.PlayerState.PLAYING) { window.Android.onReady(); }
                            }
                        }
                    });
                }
                function updateProgress() {
                    if (ready && player && player.getDuration) {
                        var current = player.getCurrentTime();
                        var duration = player.getDuration();
                        if (duration > 0) { window.Android.onProgress(current / duration); }
                    }
                }
                function setMute(mute) {
                    if (!ready) return;
                    if (mute) player.mute(); else player.unMute();
                }
                function seek(seconds) {
                    if (ready) player.seekTo(player.getCurrentTime() + seconds, true);
                }
                function setPlayback(play) {
                    if (!ready) return;
                    if (play) player.playVideo(); else player.pauseVideo();
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    val webView = remember(videoId) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36"
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            
            addJavascriptInterface(object {
                @JavascriptInterface fun onReady() { isReady = true }
                @JavascriptInterface fun onProgress(progress: Float) { videoProgress = progress }
            }, "Android")

            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null)
        }
    }

    LaunchedEffect(webView) {
        onWebViewReady(webView)
    }

    DisposableEffect(lifecycleOwner, webView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webView.evaluateJavascript("setPlayback(false)", null)
                Lifecycle.Event.ON_RESUME -> if (isPlaying) webView.evaluateJavascript("setPlayback(true)", null)
                Lifecycle.Event.ON_DESTROY -> { webView.loadUrl("about:blank"); webView.destroy() }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isReady, isPlaying) {
        if (isReady) {
            webView.evaluateJavascript("setPlayback($isPlaying)", null)
        }
    }
    
    LaunchedEffect(isReady, isMutedGlobal) {
        if (isReady) {
            webView.evaluateJavascript("setMute($isMutedGlobal)", null)
        }
    }

    Box(modifier = modifier.then(if (isReel) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)))) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 1f }
        )

        if (!isReady && isPlaying) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White.copy(alpha = 0.5f))
        }

        if (isPlaying && !isReel) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = { onMuteToggle(!isMutedGlobal) },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(if (isMutedGlobal) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp, null, tint = Color.White)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { webView.evaluateJavascript("seek(-10)", null) }, modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.Default.Replay10, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                    IconButton(onClick = { webView.evaluateJavascript("seek(10)", null) }, modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
                        Icon(Icons.Default.Forward10, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }

            LinearProgressIndicator(
                progress = { videoProgress },
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(2.dp),
                color = Color.White.copy(alpha = 0.8f),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }

        if (isPlaying && isReel) {
            LinearProgressIndicator(
                progress = { videoProgress },
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(2.dp),
                color = AppColors.Primary.copy(alpha = 0.9f),
                trackColor = Color.White.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun QuizScreen(lessonId: String, navController: NavHostController, viewModel: MainViewModel) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedOptionIndexes = remember { mutableStateListOf<Int>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val questions = MockData.quizQuestions.filter { it.lessonId == lessonId }

    if (questions.isEmpty()) return

    if (isFinished) {
        QuizResultScreen(score = score, total = questions.size) {
            navController.popBackStack()
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background).padding(24.dp)
        ) {
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + (if(isSubmitted) 1 else 0)).toFloat() / questions.size },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            Text(currentQuestion.questionText, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(32.dp))
            
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndexes.contains(index)
                val isCorrect = currentQuestion.correctAnswerIndexes.contains(index)
                
                val borderColor = when {
                    isSubmitted && isCorrect -> Color(0xFF10B981)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }

                Surface(
                    onClick = { 
                        if (!isSubmitted) {
                            selectedOptionIndexes.clear()
                            selectedOptionIndexes.add(index)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(24.dp).border(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, CircleShape).padding(4.dp)
                        ) {
                            if (isSelected) Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(option, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)), shape = RoundedCornerShape(16.dp)) {
                    Text(currentQuestion.explanation, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!isSubmitted) {
                        isSubmitted = true
                        if (currentQuestion.correctAnswerIndexes.all { selectedOptionIndexes.contains(it) }) score++
                    } else {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++; selectedOptionIndexes.clear(); isSubmitted = false
                        } else {
                            isFinished = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = selectedOptionIndexes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (!isSubmitted) "Submit Answer" else "Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(120.dp).background(AppColors.PrimaryGradient, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(60.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Great Job!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text("${(score.toFloat()/total * 100).toInt()}% Success Rate", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Back to Lesson", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun CourseBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
