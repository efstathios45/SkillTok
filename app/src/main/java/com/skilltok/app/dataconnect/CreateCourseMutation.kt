
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



public interface CreateCourseMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      CreateCourseMutation.Data,
      CreateCourseMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: com.google.firebase.dataconnect.OptionalVariable<@kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID?>,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val subject: String,
    val level: String
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var id: java.util.UUID?
        public var title: String
        public var description: String
        public var thumbnailUrl: String
        public var subject: String
        public var level: String
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          title: String,description: String,thumbnailUrl: String,subject: String,level: String,
          block_: Builder.() -> Unit
        ): Variables {
          var id: com.google.firebase.dataconnect.OptionalVariable<java.util.UUID?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var title= title
            var description= description
            var thumbnailUrl= thumbnailUrl
            var subject= subject
            var level= level
            

          return object : Builder {
            override var id: java.util.UUID?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { id = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var title: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { title = value_ }
              
            override var description: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { description = value_ }
              
            override var thumbnailUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { thumbnailUrl = value_ }
              
            override var subject: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { subject = value_ }
              
            override var level: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { level = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              id=id,title=title,description=description,thumbnailUrl=thumbnailUrl,subject=subject,level=level,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val course_insert: CourseKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CreateCourse"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CreateCourseMutation.ref(
  
    title: String,description: String,thumbnailUrl: String,subject: String,level: String,
  
    block_: CreateCourseMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    CreateCourseMutation.Data,
    CreateCourseMutation.Variables
  > =
  ref(
    
      CreateCourseMutation.Variables.build(
        title=title,description=description,thumbnailUrl=thumbnailUrl,subject=subject,level=level,
  
    block_
      )
    
  )

public suspend fun CreateCourseMutation.execute(
  
    title: String,description: String,thumbnailUrl: String,subject: String,level: String,
  
    block_: CreateCourseMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    CreateCourseMutation.Data,
    CreateCourseMutation.Variables
  > =
  ref(
    
      title=title,description=description,thumbnailUrl=thumbnailUrl,subject=subject,level=level,
  
    block_
    
  ).execute()


