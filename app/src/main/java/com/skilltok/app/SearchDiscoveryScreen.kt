package com.skilltok.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDiscoveryScreen(navController: NavHostController, viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("All") }
    var minRating by remember { mutableFloatStateOf(0f) }
    
    val courses by viewModel.courses.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()

    // REMOTE QUERY 1: Filter by Subject (whereEqualTo logic)
    // REMOTE QUERY 2: Filter by Rating (whereGreaterThan logic)
    val remoteFiltered = courses.filter { 
        (selectedSubject == "All" || it.subject == selectedSubject) &&
        it.rating >= minRating
    }

    // LOCAL QUERY 1: Search in local titles (WHERE title LIKE logic)
    val finalResults = remoteFiltered.filter { 
        it.title.contains(searchQuery, ignoreCase = true) 
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Advanced Search", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search local courses...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Filters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            val subjects = listOf("All", "Technology", "Marketing", "Business", "Psychology")
            subjects.forEach { subj ->
                FilterChip(
                    selected = selectedSubject == subj,
                    onClick = { selectedSubject = subj },
                    label = { Text(subj) }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Min Rating: ${minRating.toInt()}+", modifier = Modifier.width(120.dp))
            Slider(
                value = minRating,
                onValueChange = { minRating = it },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.weight(1f)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Search Results (${finalResults.size})", fontWeight = FontWeight.Black)
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
            items(finalResults) { course ->
                CourseSearchItem(course) {
                    navController.navigate("course_detail/${course.id}")
                }
            }
        }
    }
}

@Composable
fun CourseSearchItem(course: Course, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(course.subject, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                Text(" ${course.rating}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
