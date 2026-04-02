
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



public interface CreateModuleMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      CreateModuleMutation.Data,
      CreateModuleMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val courseId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val orderIndex: Int
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID?
        public var courseId: java.util.UUID
        public var title: String
        public var orderIndex: Int
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          courseId: java.util.UUID,title: String,orderIndex: Int,
          block_: Builder.() -> Unit
        ): Variables {
          var id: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var courseId= courseId
            var title= title
            var orderIndex= orderIndex
            

          return object : Builder {
            override var id: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var courseId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { courseId = value_ }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var orderIndex: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { orderIndex = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,courseId=courseId,title=title,orderIndex=orderIndex,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val module_insert: ModuleKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateModule"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateModuleMutation.ref(
  
    courseId: java.util.UUID,title: String,orderIndex: Int,
  
    block_: CreateModuleMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateModuleMutation.Data,
    CreateModuleMutation.Variables
  > =
  ref(
    
      CreateModuleMutation.Variables.build(
        courseId=courseId,title=title,orderIndex=orderIndex,
  
    block_
      )
    
  )

public suspend fun CreateModuleMutation.execute(
  
    courseId: java.util.UUID,title: String,orderIndex: Int,
  
    block_: CreateModuleMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    CreateModuleMutation.Data,
    CreateModuleMutation.Variables
  > =
  ref(
    
      courseId=courseId,title=title,orderIndex=orderIndex,
  
    block_
    
  ).execute()


