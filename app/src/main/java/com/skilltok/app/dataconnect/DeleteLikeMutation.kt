
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



public interface DeleteLikeMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      DeleteLikeMutation.Data,
      DeleteLikeMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val like_delete: LikeKey?
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteLike"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteLikeMutation.ref(
  
    reelId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteLikeMutation.Data,
    DeleteLikeMutation.Variables
  > =
  ref(
    
      DeleteLikeMutation.Variables(
        reelId=reelId,
  
      )
    
  )

public suspend fun DeleteLikeMutation.execute(
  
    reelId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    DeleteLikeMutation.Data,
    DeleteLikeMutation.Variables
  > =
  ref(
    
      reelId=reelId,
  
    
  ).execute()


