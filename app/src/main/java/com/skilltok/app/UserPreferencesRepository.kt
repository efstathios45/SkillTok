package com.skilltok.app

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class UserLearningPreferences(
    val interests: List<String> = emptyList(),
    val goals: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false
)

class UserPreferencesRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun docRef(uid: String) =
        firestore.collection("users").document(uid).collection("settings").document("learning")

    fun observeLearningPreferences(uid: String): Flow<UserLearningPreferences> = callbackFlow {
        val registration = docRef(uid).addSnapshotListener { snap, e ->
            if (e != null) {
                Log.w("UserPrefs", "listen failed", e)
                trySend(UserLearningPreferences())
                return@addSnapshotListener
            }
            if (snap == null || !snap.exists()) {
                trySend(UserLearningPreferences())
                return@addSnapshotListener
            }
            val interests = (snap.get("interests") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            val goals = (snap.get("goals") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            val done = snap.getBoolean("onboardingCompleted") == true
            trySend(UserLearningPreferences(interests, goals, done))
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveLearningPreferences(uid: String, prefs: UserLearningPreferences) {
        val data = mapOf(
            "interests" to prefs.interests,
            "goals" to prefs.goals,
            "onboardingCompleted" to prefs.onboardingCompleted
        )
        docRef(uid).set(data, SetOptions.merge()).await()
    }

    suspend fun getLearningPreferencesOnce(uid: String): UserLearningPreferences? {
        val snap = docRef(uid).get().await()
        if (!snap.exists()) return null
        val interests = (snap.get("interests") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val goals = (snap.get("goals") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val done = snap.getBoolean("onboardingCompleted") == true
        return UserLearningPreferences(interests, goals, done)
    }
}
