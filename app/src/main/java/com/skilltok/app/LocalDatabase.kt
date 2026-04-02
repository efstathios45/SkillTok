package com.skilltok.app

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Entity(tableName = "local_user_profile")
data class LocalUserEntity(
    @PrimaryKey val id: String,
    val firebaseId: String? = null,
    val name: String,
    val email: String,
    val xp: Int,
    val streak: Int,
    val level: Int,
    val interests: String = "",
    val goals: String = "",
    val onboardingCompleted: Boolean = false
)

@Entity(tableName = "local_courses")
data class LocalCourseEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val firebaseId: String? = null, // Business Bridge
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val subject: String,
    val level: String
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

@Entity(tableName = "local_comments")
data class LocalCommentEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: Long
)

@Entity(tableName = "local_saved")
data class LocalSavedEntity(
    @PrimaryKey val id: String, // userId_lessonId
    val userId: String,
    val lessonId: String
)

@Dao
interface SkillTokDao {
    @Query("SELECT * FROM local_user_profile WHERE id = :uid")
    fun getUserProfile(uid: String): Flow<LocalUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: LocalUserEntity)

    @Query("SELECT * FROM local_courses")
    fun getAllCourses(): Flow<List<LocalCourseEntity>>

    @Query("SELECT * FROM local_courses WHERE title = :title AND subject = :subject LIMIT 1")
    suspend fun findByTitleAndSubject(title: String, subject: String): LocalCourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: LocalCourseEntity)

    @Update
    suspend fun updateCourse(course: LocalCourseEntity)

    @Query("SELECT * FROM local_enrollments WHERE userId = :uid")
    fun getEnrollments(uid: String): Flow<List<LocalEnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: LocalEnrollmentEntity)

    @Query("SELECT * FROM local_lesson_completions WHERE userId = :uid")
    fun getCompletions(uid: String): Flow<List<LocalCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: LocalCompletionEntity)

    @Query("SELECT * FROM local_interactions WHERE userId = :uid AND lessonId = :lessonId")
    suspend fun getInteraction(uid: String, lessonId: String): LocalInteractionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: LocalInteractionEntity)

    @Query("SELECT * FROM local_comments WHERE lessonId = :lessonId ORDER BY createdAt DESC")
    fun getComments(lessonId: String): Flow<List<LocalCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: LocalCommentEntity)

    @Query("SELECT lessonId FROM local_saved WHERE userId = :uid")
    fun getSavedIds(uid: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaved(saved: LocalSavedEntity)

    @Query("DELETE FROM local_saved WHERE userId = :uid AND lessonId = :lessonId")
    suspend fun deleteSaved(uid: String, lessonId: String)
}

@Database(
    entities = [LocalUserEntity::class, LocalCourseEntity::class, LocalEnrollmentEntity::class, LocalCompletionEntity::class, LocalInteractionEntity::class, LocalCommentEntity::class, LocalSavedEntity::class], 
    version = 6,
    exportSchema = false
)
abstract class SkillTokDatabase : RoomDatabase() {
    abstract fun dao(): SkillTokDao

    companion object {
        @Volatile
        private var INSTANCE: SkillTokDatabase? = null

        fun getDatabase(context: Context): SkillTokDatabase {
            return INSTANCE ?: synchronized(this) {
                System.loadLibrary("sqlcipher")
                val passphrase = "secure_skilltok_passphrase".toByteArray()
                val factory = SupportOpenHelperFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillTokDatabase::class.java,
                    "skilltok_local_db"
                ).openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
