
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



public interface CreateReelMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      CreateReelMutation.Data,
      CreateReelMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val courseId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val title: String,
    val description: String,
    val videoUrl: String
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID
        public var courseId: java.util.UUID?
        public var title: String
        public var description: String
        public var videoUrl: String
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          id: java.util.UUID,title: String,description: String,videoUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id= id
            var courseId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var title= title
            var description= description
            var videoUrl= videoUrl
            

          return object : Builder {
            override var id: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = value_ }
              
            override var courseId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { courseId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var description: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = value_ }
              
            override var videoUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { videoUrl = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,courseId=courseId,title=title,description=description,videoUrl=videoUrl,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val reel_insert: ReelKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateReel"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateReelMutation.ref(
  
    id: java.util.UUID,title: String,description: String,videoUrl: String,
  
    block_: CreateReelMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateReelMutation.Data,
    CreateReelMutation.Variables
  > =
  ref(
    
      CreateReelMutation.Variables.build(
        id=id,title=title,description=description,videoUrl=videoUrl,
  
    block_
      )
    
  )

public suspend fun CreateReelMutation.execute(
  
    id: java.util.UUID,title: String,description: String,videoUrl: String,
  
    block_: CreateReelMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    CreateReelMutation.Data,
    CreateReelMutation.Variables
  > =
  ref(
    
      id=id,title=title,description=description,videoUrl=videoUrl,
  
    block_
    
  ).execute()


