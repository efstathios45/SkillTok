
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



public interface ToggleSaveMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      ToggleSaveMutation.Data,
      ToggleSaveMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val savedContent_upsert: SavedContentKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ToggleSave"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ToggleSaveMutation.ref(
  
    reelId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.MutationRef<
    ToggleSaveMutation.Data,
    ToggleSaveMutation.Variables
  > =
  ref(
    
      ToggleSaveMutation.Variables(
        reelId=reelId,
  
      )
    
  )

public suspend fun ToggleSaveMutation.execute(
  
    reelId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    ToggleSaveMutation.Data,
    ToggleSaveMutation.Variables
  > =
  ref(
    
      reelId=reelId,
  
    
  ).execute()


