package com.skilltok.app

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(viewModel: MainViewModel, onLoginSuccess: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var showVerificationMessage by remember { 
        mutableStateOf(auth.currentUser != null && !auth.currentUser!!.isEmailVerified) 
    }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val isNameValid = isLogin || name.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (showVerificationMessage) "Verify Your Email" else if (isLogin) "Welcome Back" else "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = if (showVerificationMessage) "Check your inbox for a verification link." else if (isLogin) "Sign in to continue learning" else "Join the community of skill seekers",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        if (showVerificationMessage) {
            Button(
                onClick = {
                    auth.currentUser?.reload()?.addOnCompleteListener {
                        if (auth.currentUser?.isEmailVerified == true) {
                            scope.launch {
                                viewModel.syncUserToDatabase(auth.currentUser!!)
                                onLoginSuccess()
                            }
                        } else {
                            Toast.makeText(context, "Email still not verified. Please check your inbox.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("I've Verified My Email")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { 
                auth.signOut()
                showVerificationMessage = false 
            }) {
                Text("Back to Login")
            }
        } else {
            if (!isLogin) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                isError = email.isNotEmpty() && !isEmailValid
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                isError = password.isNotEmpty() && !isPasswordValid
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        try {
                            if (isLogin) {
                                val result = auth.signInWithEmailAndPassword(email, password).await()
                                if (result.user?.isEmailVerified == true) {
                                    viewModel.syncUserToDatabase(result.user!!)
                                    onLoginSuccess()
                                } else {
                                    showVerificationMessage = true
                                    Toast.makeText(context, "Link sent to $email", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val result = auth.createUserWithEmailAndPassword(email, password).await()
                                // Set profile name immediately
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                result.user?.updateProfile(profileUpdates)?.await()
                                
                                result.user?.sendEmailVerification()?.await()
                                showVerificationMessage = true
                                Toast.makeText(context, "Verification link sent to $email", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && isEmailValid && isPasswordValid && isNameValid
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isLogin) "Sign In" else "Sign Up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val googleAuthManager = remember { GoogleAuthManager(context) }
            OutlinedButton(
                onClick = {
                    isLoading = true
                    scope.launch {
                        val result = googleAuthManager.signIn(context as Activity)
                        if (result.isSuccess) {
                            auth.currentUser?.let { viewModel.syncUserToDatabase(it) }
                            onLoginSuccess()
                        } else {
                            val error = result.exceptionOrNull()
                            Toast.makeText(context, "Login failed: ${error?.message ?: "Cancelled"}", Toast.LENGTH_LONG).show()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continue with Google", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { isLogin = !isLogin }) {
                Text(
                    if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Sign In",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
