
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


public interface ListCoursesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      ListCoursesQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val courses: List<CoursesItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class CoursesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val subject: String,
    val level: String,
    val createdAt: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
    val owner: Owner
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Owner(
  
    val displayName: String
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "ListCourses"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun ListCoursesQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    ListCoursesQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun ListCoursesQuery.execute(
  
  ): com.google.firebase.dataconnect.QueryResult<
    ListCoursesQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun ListCoursesQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<ListCoursesQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

