
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


public interface GetModulesQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetModulesQuery.Data,
      GetModulesQuery.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val modules: List<ModulesItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class ModulesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val description: String?,
    val orderIndex: Int
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetModules"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun GetModulesQuery.ref(
  
    courseId: java.util.UUID,
  
  
): com.google.firebase.dataconnect.QueryRef<
    GetModulesQuery.Data,
    GetModulesQuery.Variables
  > =
  ref(
    
      GetModulesQuery.Variables(
        courseId=courseId,
  
      )
    
  )

public suspend fun GetModulesQuery.execute(
  
    courseId: java.util.UUID,
  
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetModulesQuery.Data,
    GetModulesQuery.Variables
  > =
  ref(
    
      courseId=courseId,
  
    
  ).execute()


  public fun GetModulesQuery.flow(
    
      courseId: java.util.UUID,
  
    
    ): kotlinx.coroutines.flow.Flow<GetModulesQuery.Data> =
    ref(
        
          courseId=courseId,
  
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

