
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


public interface GetUserProfileQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetUserProfileQuery.Data,
      GetUserProfileQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: String
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user: User?
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class User(
  
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?,
    val bio: String?,
    val role: String
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUserProfile"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUserProfileQuery.ref(
  
    id: String,
  
  
): com.google.firebase.dataconnect.QueryRef<
    GetUserProfileQuery.Data,
    GetUserProfileQuery.Variables
  > =
  ref(
    
      GetUserProfileQuery.Variables(
        id=id,
  
      )
    
  )

public suspend fun GetUserProfileQuery.execute(
  
    id: String,
  
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetUserProfileQuery.Data,
    GetUserProfileQuery.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


  public fun GetUserProfileQuery.flow(
    
      id: String,
  
    
    ): kotlinx.coroutines.flow.Flow<GetUserProfileQuery.Data> =
    ref(
        
          id=id,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

