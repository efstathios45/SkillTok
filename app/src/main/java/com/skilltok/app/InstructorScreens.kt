package com.skilltok.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(navController: NavHostController, viewModel: MainViewModel) {
    var step by remember { mutableStateOf("info") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Beginner") }
    
    val subjects = listOf("Human Resources", "Emotional Intelligence", "Leadership", "Marketing", "Public Speaking", "Data Analytics", "Negotiation", "Business Strategy")
    val levels = listOf("Beginner", "Intermediate", "Advanced")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Course", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (step == "info") {
                Text("Course Info", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Subject", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    subjects.forEach { s ->
                        FilterChip(
                            selected = subject == s,
                            onClick = { subject = s },
                            label = { Text(s) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Level", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { l ->
                        ElevatedFilterChip(
                            selected = level == l,
                            onClick = { level = l },
                            label = { Text(l, modifier = Modifier.weight(1f)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { step = "modules" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = title.isNotBlank() && subject.isNotBlank()
                ) {
                    Text("Next: Add Modules")
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
    val modules = remember { mutableStateListOf(ModuleData(title = "Module 1")) }

    Column {
        modules.forEachIndexed { index, module ->
            ModuleItem(
                module = module,
                onTitleChange = { modules[index] = module.copy(title = it) },
                onRemove = { if (modules.size > 1) modules.removeAt(index) },
                onAddLesson = { 
                    val lessons = module.lessons.toMutableList()
                    lessons.add(LessonData())
                    modules[index] = module.copy(lessons = lessons)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = { modules.add(ModuleData(title = "Module ${modules.size + 1}")) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Text("Add Module")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Text("Back")
            }
            Button(onClick = { onPublish(modules) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Text("Publish Course")
            }
        }
    }
}

@Composable
fun ModuleItem(
    module: ModuleData,
    onTitleChange: (String) -> Unit,
    onRemove: () -> Unit,
    onAddLesson: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = module.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                }
            }

            module.lessons.forEach { lesson ->
                Text("• ${if(lesson.title.isBlank()) "New Lesson" else lesson.title}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
            }

            TextButton(onClick = onAddLesson) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Text("Add Lesson")
            }
        }
    }
}

data class ModuleData(val title: String, val lessons: List<LessonData> = emptyList())
data class LessonData(val title: String = "", val description: String = "", val videoUrl: String = "")
