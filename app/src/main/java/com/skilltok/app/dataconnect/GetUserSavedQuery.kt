
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetUserSavedQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetUserSavedQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val savedContents: List<SavedContentsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SavedContentsItem(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserSaved"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserSavedQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserSavedQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUserSavedQuery.execute(
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetUserSavedQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUserSavedQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUserSavedQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

