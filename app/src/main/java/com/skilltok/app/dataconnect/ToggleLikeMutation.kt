
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



public interface ToggleLikeMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      ToggleLikeMutation.Data,
      ToggleLikeMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val like_upsert: LikeKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ToggleLike"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ToggleLikeMutation.ref(
  
    reelId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    ToggleLikeMutation.Data,
    ToggleLikeMutation.Variables
  > =
  ref(
    
      ToggleLikeMutation.Variables(
        reelId=reelId,
  
      )
    
  )

public suspend fun ToggleLikeMutation.execute(
  
    reelId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    ToggleLikeMutation.Data,
    ToggleLikeMutation.Variables
  > =
  ref(
    
      reelId=reelId,
  
    
  ).execute()


