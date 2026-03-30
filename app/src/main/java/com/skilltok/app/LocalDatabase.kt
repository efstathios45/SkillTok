package com.skilltok.app

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Entity(tableName = "local_user_profile")
data class LocalUserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val xp: Int,
    val streak: Int,
    val level: Int
)

@Entity(tableName = "local_enrollments")
data class LocalEnrollmentEntity(
    @PrimaryKey val id: String, // userId_courseId
    val userId: String,
    val courseId: String,
    val status: String,
    val progressPercent: Int,
    val currentLessonId: String
)

@Entity(tableName = "local_lesson_completions")
data class LocalCompletionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val userId: String,
    val lessonId: String,
    val completedAt: String,
    val synced: Boolean = false
)

@Entity(tableName = "local_interactions")
data class LocalInteractionEntity(
    @PrimaryKey val id: String, // userId_lessonId
    val userId: String,
    val lessonId: String,
    val isLiked: Boolean,
    val synced: Boolean = false
)

@Dao
interface SkillTokDao {
    @Query("SELECT * FROM local_user_profile WHERE id = :uid")
    fun getUserProfile(uid: String): Flow<LocalUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: LocalUserEntity)

    @Query("SELECT * FROM local_enrollments WHERE userId = :uid")
    fun getEnrollments(uid: String): Flow<List<LocalEnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: LocalEnrollmentEntity)

    @Query("SELECT * FROM local_lesson_completions WHERE userId = :uid")
    fun getCompletions(uid: String): Flow<List<LocalCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: LocalCompletionEntity)

    @Query("SELECT * FROM local_lesson_completions WHERE synced = 0")
    suspend fun getUnsyncedCompletions(): List<LocalCompletionEntity>

    @Update
    suspend fun updateCompletion(completion: LocalCompletionEntity)

    @Query("SELECT * FROM local_interactions WHERE userId = :uid AND lessonId = :lessonId")
    suspend fun getInteraction(uid: String, lessonId: String): LocalInteractionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: LocalInteractionEntity)

    @Query("SELECT * FROM local_interactions WHERE synced = 0")
    suspend fun getUnsyncedInteractions(): List<LocalInteractionEntity>

    @Update
    suspend fun updateInteraction(interaction: LocalInteractionEntity)
}

@Database(entities = [LocalUserEntity::class, LocalEnrollmentEntity::class, LocalCompletionEntity::class, LocalInteractionEntity::class], version = 2)
abstract class SkillTokDatabase : RoomDatabase() {
    abstract fun dao(): SkillTokDao

    companion object {
        @Volatile
        private var INSTANCE: SkillTokDatabase? = null

        fun getDatabase(context: Context): SkillTokDatabase {
            return INSTANCE ?: synchronized(this) {
                // Load SQLCipher libraries
                SQLiteDatabase.loadLibs(context)
                
                // In a production app, the passphrase should be stored securely (e.g. in KeyStore)
                val passphrase = SQLiteDatabase.getBytes("secure_skilltok_passphrase".toCharArray())
                val factory = SupportFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillTokDatabase::class.java,
                    "skilltok_local_db"
                ).openHelperFactory(factory)
                .fallbackToDestructiveMigration() // For development simplicity
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
