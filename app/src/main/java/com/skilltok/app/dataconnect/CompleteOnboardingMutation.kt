
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



public interface CompleteOnboardingMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      SkilltokConnectorConnector,
      CompleteOnboardingMutation.Data,
      CompleteOnboardingMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val interests: String,
    val goals: String
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val user_update: UserKey?
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CompleteOnboarding"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CompleteOnboardingMutation.ref(
  
    interests: String, goals: String,
  
  
): com.google.firebase.dataconnect.MutationRef<
    CompleteOnboardingMutation.Data,
    CompleteOnboardingMutation.Variables
  > =
  ref(
    
      CompleteOnboardingMutation.Variables(
        interests=interests, goals=goals,
  
      )
    
  )

public suspend fun CompleteOnboardingMutation.execute(
  
    interests: String, goals: String,
  
  
  ): com.google.firebase.dataconnect.MutationResult<
    CompleteOnboardingMutation.Data,
    CompleteOnboardingMutation.Variables
  > =
  ref(
    
      interests=interests, goals=goals,
  
    
  ).execute()
