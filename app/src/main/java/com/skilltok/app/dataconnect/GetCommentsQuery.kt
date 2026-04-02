
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


public interface GetCommentsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetCommentsQuery.Data,
      GetCommentsQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comments: List<CommentsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class CommentsItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val text: String,
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
    val user: User
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class User(
  
    val id: String,
    val displayName: String
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComments"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetCommentsQuery.ref(
  
    reelId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.QueryRef<
    GetCommentsQuery.Data,
    GetCommentsQuery.Variables
  > =
  ref(
    
      GetCommentsQuery.Variables(
        reelId=reelId,
  
      )
    
  )

public suspend fun GetCommentsQuery.execute(
  
    reelId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetCommentsQuery.Data,
    GetCommentsQuery.Variables
  > =
  ref(
    
      reelId=reelId,
  
    
  ).execute()


  public fun GetCommentsQuery.flow(
    
      reelId: java.util.UUID,
  
    
    ): kotlinx.coroutines.flow.Flow<GetCommentsQuery.Data> =
    ref(
        
          reelId=reelId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

