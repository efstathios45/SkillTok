
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



public interface EnrollInCourseMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      EnrollInCourseMutation.Data,
      EnrollInCourseMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val enrollment_upsert: EnrollmentKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EnrollInCourse"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EnrollInCourseMutation.ref(
  
    courseId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    EnrollInCourseMutation.Data,
    EnrollInCourseMutation.Variables
  > =
  ref(
    
      EnrollInCourseMutation.Variables(
        courseId=courseId,
  
      )
    
  )

public suspend fun EnrollInCourseMutation.execute(
  
    courseId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    EnrollInCourseMutation.Data,
    EnrollInCourseMutation.Variables
  > =
  ref(
    
      courseId=courseId,
  
    
  ).execute()


