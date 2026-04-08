
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



public interface DeleteEnrollmentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      DeleteEnrollmentMutation.Data,
      DeleteEnrollmentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val enrollment_delete: EnrollmentKey?
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteEnrollment"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteEnrollmentMutation.ref(
  
    courseId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteEnrollmentMutation.Data,
    DeleteEnrollmentMutation.Variables
  > =
  ref(
    
      DeleteEnrollmentMutation.Variables(
        courseId=courseId,
  
      )
    
  )

public suspend fun DeleteEnrollmentMutation.execute(
  
    courseId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    DeleteEnrollmentMutation.Data,
    DeleteEnrollmentMutation.Variables
  > =
  ref(
    
      courseId=courseId,
  
    
  ).execute()
