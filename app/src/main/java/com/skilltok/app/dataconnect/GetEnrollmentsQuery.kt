
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


public interface GetEnrollmentsQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      SkilltokConnectorConnector,
      GetEnrollmentsQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val enrollments: List<EnrollmentsItem>
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class EnrollmentsItem(
  
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetEnrollments"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetEnrollmentsQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetEnrollmentsQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetEnrollmentsQuery.execute(
  
  ): com.google.firebase.dataconnect.QueryResult<
    GetEnrollmentsQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetEnrollmentsQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetEnrollmentsQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

