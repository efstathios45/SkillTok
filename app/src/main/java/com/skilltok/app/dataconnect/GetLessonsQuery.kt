
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


public interface GetLessonsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetLessonsQuery.Data,
      GetLessonsQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val moduleId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val lessons: List<LessonsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class LessonsItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val description: String,
    val lessonType: String,
    val orderIndex: Int,
    val contentUrl: String
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetLessons"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetLessonsQuery.ref(
  
    moduleId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.QueryRef<
    GetLessonsQuery.Data,
    GetLessonsQuery.Variables
  > =
  ref(
    
      GetLessonsQuery.Variables(
        moduleId=moduleId,
  
      )
    
  )

public suspend fun GetLessonsQuery.execute(
  
    moduleId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetLessonsQuery.Data,
    GetLessonsQuery.Variables
  > =
  ref(
    
      moduleId=moduleId,
  
    
  ).execute()


  public fun GetLessonsQuery.flow(
    
      moduleId: java.util.UUID,
  
    
    ): kotlinx.coroutines.flow.Flow<GetLessonsQuery.Data> =
    ref(
        
          moduleId=moduleId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

