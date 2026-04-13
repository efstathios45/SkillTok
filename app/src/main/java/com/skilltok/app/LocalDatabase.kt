package com.skilltok.app

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File

@Entity(tableName = "local_user_profile")
data class LocalUserEntity(
    @PrimaryKey val id: String,
    val firebaseId: String? = null, // Sync link
    val name: String,
    val email: String,
    val role: String = "learner",
    val xp: Int,
    val streak: Int,
    val level: Int
)

@Entity(tableName = "local_courses")
data class LocalCourseEntity(
    @PrimaryKey val id: String, // Use stable ID (Firebase or Mock)
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val subject: String,
    val level: String
)

@Entity(
    tableName = "local_enrollments",
    foreignKeys = [
        ForeignKey(
            entity = LocalUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocalCourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("courseId")]
)
data class LocalEnrollmentEntity(
    @PrimaryKey val id: String, // userId_courseId
    val userId: String,
    val courseId: String,
    val status: String,
    val progressPercent: Int,
    val currentLessonId: String
)

@Entity(
    tableName = "local_lesson_completions",
    foreignKeys = [
        ForeignKey(
            entity = LocalUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class LocalCompletionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val userId: String,
    val lessonId: String,
    val completedAt: String,
    val synced: Boolean = false
)

@Entity(
    tableName = "local_interactions",
    foreignKeys = [
        ForeignKey(
            entity = LocalUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class LocalInteractionEntity(
    @PrimaryKey val id: String, // userId_lessonId
    val userId: String,
    val lessonId: String,
    val isLiked: Boolean,
    val synced: Boolean = false
)

@Entity(
    tableName = "local_comments",
    foreignKeys = [
        ForeignKey(
            entity = LocalUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class LocalCommentEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: Long
)

@Entity(
    tableName = "local_saved",
    foreignKeys = [
        ForeignKey(
            entity = LocalUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class LocalSavedEntity(
    @PrimaryKey val id: String, // userId_lessonId
    val userId: String,
    val lessonId: String
)

@Dao
interface SkillTokDao {
    // LOCAL QUERY 1: Simple Select with WHERE
    @Query("SELECT * FROM local_user_profile WHERE id = :uid")
    fun getUserProfile(uid: String): Flow<LocalUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: LocalUserEntity)

    @Query("SELECT * FROM local_courses")
    fun getAllCourses(): Flow<List<LocalCourseEntity>>

    // LOCAL QUERY 2: Use of AND operator
    @Query("SELECT * FROM local_courses WHERE title = :title AND subject = :subject LIMIT 1")
    suspend fun findByTitleAndSubject(title: String, subject: String): LocalCourseEntity?

    // LOCAL QUERY 3: Use of LIKE operator for search
    @Query("SELECT * FROM local_courses WHERE title LIKE :query OR description LIKE :query")
    fun searchCourses(query: String): Flow<List<LocalCourseEntity>>

    // LOCAL QUERY 4: Use of IN operator
    @Query("SELECT * FROM local_courses WHERE id IN (:ids)")
    fun getCoursesByIds(ids: List<String>): Flow<List<LocalCourseEntity>>

    // LOCAL QUERY 5: Use of Comparison operator (>)
    @Query("SELECT * FROM local_enrollments WHERE userId = :uid AND progressPercent > :minProgress")
    fun getAdvancedProgress(uid: String, minProgress: Int): Flow<List<LocalEnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: LocalCourseEntity)

    @Update
    suspend fun updateCourse(course: LocalCourseEntity)

    @Query("SELECT * FROM local_enrollments WHERE userId = :uid")
    fun getEnrollments(uid: String): Flow<List<LocalEnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: LocalEnrollmentEntity)

    @Query("DELETE FROM local_enrollments WHERE userId = :uid AND courseId = :courseId")
    suspend fun deleteEnrollment(uid: String, courseId: String)

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
    version = 10, // Bumping version for more structural queries
    exportSchema = false
)
abstract class SkillTokDatabase : RoomDatabase() {
    abstract fun dao(): SkillTokDao

    companion object {
        private const val DB_NAME = "skilltok_local_db"
        @Volatile
        private var INSTANCE: SkillTokDatabase? = null

        fun getDatabase(context: Context): SkillTokDatabase {
            return INSTANCE ?: synchronized(this) {
                System.loadLibrary("sqlcipher")
                
                val passphrase = SecurityUtils.getDatabasePassphrase(context)
                val dbFile = context.getDatabasePath(DB_NAME)
                if (dbFile.exists()) {
                    try {
                        SQLiteDatabase.openDatabase(
                            dbFile.absolutePath, 
                            String(passphrase), 
                            null, 
                            SQLiteDatabase.OPEN_READONLY,
                            null,
                            null
                        ).close()
                    } catch (e: Exception) {
                        Log.e("SkillTokDatabase", "Encryption mismatch or schema change. Wiping local cache to recover.", e)
                        context.deleteDatabase(DB_NAME)
                    }
                }

                val factory = SupportOpenHelperFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillTokDatabase::class.java,
                    DB_NAME
                ).openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
