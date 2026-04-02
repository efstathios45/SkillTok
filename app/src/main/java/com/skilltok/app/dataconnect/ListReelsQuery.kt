
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


public interface ListReelsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      ListReelsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val reels: List<ReelsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ReelsItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val description: String,
    val videoUrl: String,
    val creator: Creator
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Creator(
  
    val displayName: String,
    val photoUrl: String?
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListReels"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListReelsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListReelsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListReelsQuery.execute(
  
  ): com.google.firebase.dataconnect.QueryResult<
    ListReelsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListReelsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListReelsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

