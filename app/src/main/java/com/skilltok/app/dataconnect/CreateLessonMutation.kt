
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



public interface CreateLessonMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      CreateLessonMutation.Data,
      CreateLessonMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val moduleId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
    val title: String,
    val description: String,
    val lessonType: String,
    val orderIndex: Int,
    val contentUrl: String
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID?
        public var moduleId: java.util.UUID
        public var title: String
        public var description: String
        public var lessonType: String
        public var orderIndex: Int
        public var contentUrl: String
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          moduleId: java.util.UUID,title: String,description: String,lessonType: String,orderIndex: Int,contentUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var moduleId= moduleId
            var title= title
            var description= description
            var lessonType= lessonType
            var orderIndex= orderIndex
            var contentUrl= contentUrl
            

          return object : Builder {
            override var id: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var moduleId: java.util.UUID
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { moduleId = value_ }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var description: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = value_ }
              
            override var lessonType: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { lessonType = value_ }
              
            override var orderIndex: Int
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { orderIndex = value_ }
              
            override var contentUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { contentUrl = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,moduleId=moduleId,title=title,description=description,lessonType=lessonType,orderIndex=orderIndex,contentUrl=contentUrl,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val lesson_insert: LessonKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateLesson"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateLessonMutation.ref(
  
    moduleId: java.util.UUID,title: String,description: String,lessonType: String,orderIndex: Int,contentUrl: String,
  
    block_: CreateLessonMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateLessonMutation.Data,
    CreateLessonMutation.Variables
  > =
  ref(
    
      CreateLessonMutation.Variables.build(
        moduleId=moduleId,title=title,description=description,lessonType=lessonType,orderIndex=orderIndex,contentUrl=contentUrl,
  
    block_
      )
    
  )

public suspend fun CreateLessonMutation.execute(
  
    moduleId: java.util.UUID,title: String,description: String,lessonType: String,orderIndex: Int,contentUrl: String,
  
    block_: CreateLessonMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    CreateLessonMutation.Data,
    CreateLessonMutation.Variables
  > =
  ref(
    
      moduleId=moduleId,title=title,description=description,lessonType=lessonType,orderIndex=orderIndex,contentUrl=contentUrl,
  
    block_
    
  ).execute()


