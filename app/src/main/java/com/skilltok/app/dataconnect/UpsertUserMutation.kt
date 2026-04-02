
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



public interface UpsertUserMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val displayName: String,
    val email: String,
    val photoUrl: String,
    val interests: com.google.firebase.dataconnect.OptionalVariable<String?>,
    val goals: com.google.firebase.dataconnect.OptionalVariable<String?>,
    val onboardingCompleted: com.google.firebase.dataconnect.OptionalVariable<Boolean?>
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var displayName: String
        public var email: String
        public var photoUrl: String
        public var interests: String?
        public var goals: String?
        public var onboardingCompleted: Boolean?
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          displayName: String, email: String, photoUrl: String,
          block_: Builder.() -> Unit
        ): Variables {
          var interests: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var goals: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var onboardingCompleted: com.google.firebase.dataconnect.OptionalVariable<Boolean?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var displayName= displayName
            var email= email
            var photoUrl= photoUrl
            

          return object : Builder {
            override var displayName: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { displayName = value_ }
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            override var photoUrl: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { photoUrl = value_ }
              
            override var interests: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { interests = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var goals: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { goals = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var onboardingCompleted: Boolean?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { onboardingCompleted = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            
          }.apply(block_)
          .let {
            Variables(
              displayName=displayName,email=email,photoUrl=photoUrl,interests=interests,goals=goals,onboardingCompleted=onboardingCompleted,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_upsert: UserKey
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "UpsertUser"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun UpsertUserMutation.ref(
  
    displayName: String,email: String,photoUrl: String,
  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      UpsertUserMutation.Variables.build(
        displayName=displayName,email=email,photoUrl=photoUrl,
  
    block_
      )
    
  )

public suspend fun UpsertUserMutation.execute(
  
    displayName: String,email: String,photoUrl: String,
  
    block_: UpsertUserMutation.Variables.Builder.() -> Unit = {}
  
  ): com.google.firebase.dataconnect.MutationResult<
    UpsertUserMutation.Data,
    UpsertUserMutation.Variables
  > =
  ref(
    
      displayName=displayName,email=email,photoUrl=photoUrl,
  
    block_
    
  ).execute()

