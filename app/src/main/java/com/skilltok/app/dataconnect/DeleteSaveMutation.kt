
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



public interface DeleteSaveMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      DeleteSaveMutation.Data,
      DeleteSaveMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val savedContent_delete: SavedContentKey?
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "DeleteSave"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun DeleteSaveMutation.ref(
  
    reelId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    DeleteSaveMutation.Data,
    DeleteSaveMutation.Variables
  > =
  ref(
    
      DeleteSaveMutation.Variables(
        reelId=reelId,
  
      )
    
  )

public suspend fun DeleteSaveMutation.execute(
  
    reelId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    DeleteSaveMutation.Data,
    DeleteSaveMutation.Variables
  > =
  ref(
    
      reelId=reelId,
  
    
  ).execute()


