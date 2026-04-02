
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


public interface GetUserLikesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetUserLikesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val likes: List<LikesItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class LikesItem(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserLikes"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserLikesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserLikesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUserLikesQuery.execute(
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetUserLikesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUserLikesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUserLikesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

