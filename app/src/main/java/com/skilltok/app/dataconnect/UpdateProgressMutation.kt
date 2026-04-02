
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



public interface UpdateProgressMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      UpdateProgressMutation.Data,
      UpdateProgressMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val lessonId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val isCompleted: Boolean
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val progress_upsert: ProgressKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpdateProgress"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpdateProgressMutation.ref(
  
    courseId: java.util.UUID,lessonId: java.util.UUID,isCompleted: Boolean,
  
  
): com.google.firebase.dataconnect.MutationRef<
    UpdateProgressMutation.Data,
    UpdateProgressMutation.Variables
  > =
  ref(
    
      UpdateProgressMutation.Variables(
        courseId=courseId,lessonId=lessonId,isCompleted=isCompleted,
  
      )
    
  )

public suspend fun UpdateProgressMutation.execute(
  
    courseId: java.util.UUID,lessonId: java.util.UUID,isCompleted: Boolean,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    UpdateProgressMutation.Data,
    UpdateProgressMutation.Variables
  > =
  ref(
    
      courseId=courseId,lessonId=lessonId,isCompleted=isCompleted,
  
    
  ).execute()


