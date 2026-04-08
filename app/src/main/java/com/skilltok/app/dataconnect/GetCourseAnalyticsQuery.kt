
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "LocalVariableName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "LocalVariableName",
  "unused",
)

package com.skilltok.app.dataconnect

public interface GetCourseAnalyticsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetCourseAnalyticsQuery.Data,
      GetCourseAnalyticsQuery.Variables
    >
{
  @kotlinx.serialization.Serializable
  public data class Variables(
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  )

  @kotlinx.serialization.Serializable
  public data class Data(
    val enrollments: List<Enrollment>,
    val progresses: List<Progress>
  ) {
    @kotlinx.serialization.Serializable
    public data class Enrollment(
      val user: User
    ) {
      @kotlinx.serialization.Serializable
      public data class User(
        val id: String,
        val displayName: String,
        val email: String
      )
    }

    @kotlinx.serialization.Serializable
    public data class Progress(
      val userId: String,
      val isCompleted: Boolean
    )
  }

  public companion object {
    public val operationName: String = "GetCourseAnalytics"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCourseAnalyticsQuery.ref(
  courseId: java.util.UUID,
): com.google.firebase.dataconnect.QueryRef<
    GetCourseAnalyticsQuery.Data,
    GetCourseAnalyticsQuery.Variables
  > =
  ref(
    GetCourseAnalyticsQuery.Variables(
      courseId=courseId,
    )
  )

public suspend fun GetCourseAnalyticsQuery.execute(
  courseId: java.util.UUID,
): com.google.firebase.dataconnect.QueryResult<
    GetCourseAnalyticsQuery.Data,
    GetCourseAnalyticsQuery.Variables
  > =
  ref(
    courseId=courseId,
  ).execute()
