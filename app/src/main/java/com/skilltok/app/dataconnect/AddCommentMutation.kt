
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



public interface AddCommentMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      AddCommentMutation.Data,
      AddCommentMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val reelId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val courseId: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val text: String
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var reelId: java.util.UUID?
        public var courseId: java.util.UUID?
        public var text: String
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          text: String,
          block_: Builder.() -> Unit
        ): Variables {
          var reelId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var courseId: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var text= text
            

          return object : Builder {
            override var reelId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { reelId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var courseId: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { courseId = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var text: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { text = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              reelId=reelId,courseId=courseId,text=text,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comment_insert: CommentKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AddComment"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AddCommentMutation.ref(
  
    text: String,
  
    block_: AddCommentMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    AddCommentMutation.Data,
    AddCommentMutation.Variables
  > =
  ref(
    
      AddCommentMutation.Variables.build(
        text=text,
  
    block_
      )
    
  )

public suspend fun AddCommentMutation.execute(
  
    text: String,
  
    block_: AddCommentMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    AddCommentMutation.Data,
    AddCommentMutation.Variables
  > =
  ref(
    
      text=text,
  
    block_
    
  ).execute()


