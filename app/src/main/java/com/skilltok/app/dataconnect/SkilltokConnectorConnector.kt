
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

import com.google.firebase.dataconnect.getInstance as _fdcGetInstance
import kotlin.time.Duration.Companion.milliseconds as _milliseconds

public interface SkilltokConnectorConnector : com.google.firebase.dataconnect.generated.GeneratedConnector<SkilltokConnectorConnector> {
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect

  
    public val addComment: AddCommentMutation
  
    public val createCourse: CreateCourseMutation
  
    public val createLesson: CreateLessonMutation
  
    public val createModule: CreateModuleMutation
  
    public val createReel: CreateReelMutation
  
    public val deleteLike: DeleteLikeMutation
  
    public val deleteSave: DeleteSaveMutation
  
    public val enrollInCourse: EnrollInCourseMutation
  
    public val getComments: GetCommentsQuery
  
    public val getEnrollments: GetEnrollmentsQuery
  
    public val getLessons: GetLessonsQuery
  
    public val getModules: GetModulesQuery
  
    public val getUserLikes: GetUserLikesQuery
  
    public val getUserProfile: GetUserProfileQuery
  
    public val getUserSaved: GetUserSavedQuery
  
    public val listCourses: ListCoursesQuery
  
    public val listReels: ListReelsQuery
  
    public val toggleLike: ToggleLikeMutation
  
    public val toggleSave: ToggleSaveMutation
  
    public val updateProgress: UpdateProgressMutation
  
    public val upsertUser: UpsertUserMutation
  

  public companion object {
    @Suppress("MemberVisibilityCanBePrivate")
    public val config: com.google.firebase.dataconnect.ConnectorConfig = com.google.firebase.dataconnect.ConnectorConfig(
      connector = "skilltok-connector",
      location = "europe-west4",
      serviceId = "skilltokfinal-2-service",
    )

    public fun getInstance(
      dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
    ):SkilltokConnectorConnector = synchronized(instances) {
      instances.getOrPut(dataConnect) {
        SkilltokConnectorConnectorImpl(dataConnect)
      }
    }

    private val instances = java.util.WeakHashMap<com.google.firebase.dataconnect.FirebaseDataConnect, SkilltokConnectorConnectorImpl>()

    
  }
}

public val SkilltokConnectorConnector.Companion.instance:SkilltokConnectorConnector
  get() = getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(
    config
  ))

public fun SkilltokConnectorConnector.Companion.getInstance(
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):SkilltokConnectorConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(config, settings))

public fun SkilltokConnectorConnector.Companion.getInstance(
  app: com.google.firebase.FirebaseApp,
  settings: com.google.firebase.dataconnect.DataConnectSettings = com.google.firebase.dataconnect.DataConnectSettings()
):SkilltokConnectorConnector =
  getInstance(com.google.firebase.dataconnect.FirebaseDataConnect._fdcGetInstance(app, config, settings))

private class SkilltokConnectorConnectorImpl(
  override val dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect
) : SkilltokConnectorConnector {
  
    override val addComment by lazy(LazyThreadSafetyMode.PUBLICATION) {
      AddCommentMutationImpl(this)
    }
  
    override val createCourse by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateCourseMutationImpl(this)
    }
  
    override val createLesson by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateLessonMutationImpl(this)
    }
  
    override val createModule by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateModuleMutationImpl(this)
    }
  
    override val createReel by lazy(LazyThreadSafetyMode.PUBLICATION) {
      CreateReelMutationImpl(this)
    }
  
    override val deleteLike by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteLikeMutationImpl(this)
    }
  
    override val deleteSave by lazy(LazyThreadSafetyMode.PUBLICATION) {
      DeleteSaveMutationImpl(this)
    }
  
    override val enrollInCourse by lazy(LazyThreadSafetyMode.PUBLICATION) {
      EnrollInCourseMutationImpl(this)
    }
  
    override val getComments by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetCommentsQueryImpl(this)
    }
  
    override val getEnrollments by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetEnrollmentsQueryImpl(this)
    }
  
    override val getLessons by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetLessonsQueryImpl(this)
    }
  
    override val getModules by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetModulesQueryImpl(this)
    }
  
    override val getUserLikes by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserLikesQueryImpl(this)
    }
  
    override val getUserProfile by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserProfileQueryImpl(this)
    }
  
    override val getUserSaved by lazy(LazyThreadSafetyMode.PUBLICATION) {
      GetUserSavedQueryImpl(this)
    }
  
    override val listCourses by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListCoursesQueryImpl(this)
    }
  
    override val listReels by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ListReelsQueryImpl(this)
    }
  
    override val toggleLike by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ToggleLikeMutationImpl(this)
    }
  
    override val toggleSave by lazy(LazyThreadSafetyMode.PUBLICATION) {
      ToggleSaveMutationImpl(this)
    }
  
    override val updateProgress by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpdateProgressMutationImpl(this)
    }
  
    override val upsertUser by lazy(LazyThreadSafetyMode.PUBLICATION) {
      UpsertUserMutationImpl(this)
    }
  

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun operations(): List<com.google.firebase.dataconnect.generated.GeneratedOperation<SkilltokConnectorConnector, *, *>> =
    queries() + mutations()

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun mutations(): List<com.google.firebase.dataconnect.generated.GeneratedMutation<SkilltokConnectorConnector, *, *>> =
    listOf(
      addComment,
        createCourse,
        createLesson,
        createModule,
        createReel,
        deleteLike,
        deleteSave,
        enrollInCourse,
        toggleLike,
        toggleSave,
        updateProgress,
        upsertUser,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun queries(): List<com.google.firebase.dataconnect.generated.GeneratedQuery<SkilltokConnectorConnector, *, *>> =
    listOf(
      getComments,
        getEnrollments,
        getLessons,
        getModules,
        getUserLikes,
        getUserProfile,
        getUserSaved,
        listCourses,
        listReels,
        
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(dataConnect: com.google.firebase.dataconnect.FirebaseDataConnect) =
    SkilltokConnectorConnectorImpl(dataConnect)

  override fun equals(other: Any?): Boolean =
    other is SkilltokConnectorConnectorImpl &&
    other.dataConnect == dataConnect

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "SkilltokConnectorConnectorImpl",
      dataConnect,
    )

  override fun toString(): String =
    "SkilltokConnectorConnectorImpl(dataConnect=$dataConnect)"
}



private open class SkilltokConnectorConnectorGeneratedQueryImpl<Data, Variables>(
  override val connector: SkilltokConnectorConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedQuery<SkilltokConnectorConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: SkilltokConnectorConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    SkilltokConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    SkilltokConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    SkilltokConnectorConnectorGeneratedQueryImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is SkilltokConnectorConnectorGeneratedQueryImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "SkilltokConnectorConnectorGeneratedQueryImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "SkilltokConnectorConnectorGeneratedQueryImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}

private open class SkilltokConnectorConnectorGeneratedMutationImpl<Data, Variables>(
  override val connector: SkilltokConnectorConnector,
  override val operationName: String,
  override val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
  override val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
) : com.google.firebase.dataconnect.generated.GeneratedMutation<SkilltokConnectorConnector, Data, Variables> {

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun copy(
    connector: SkilltokConnectorConnector,
    operationName: String,
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data>,
    variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables>,
  ) =
    SkilltokConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewVariables> withVariablesSerializer(
    variablesSerializer: kotlinx.serialization.SerializationStrategy<NewVariables>
  ) =
    SkilltokConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  @com.google.firebase.dataconnect.ExperimentalFirebaseDataConnect
  override fun <NewData> withDataDeserializer(
    dataDeserializer: kotlinx.serialization.DeserializationStrategy<NewData>
  ) =
    SkilltokConnectorConnectorGeneratedMutationImpl(
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun equals(other: Any?): Boolean =
    other is SkilltokConnectorConnectorGeneratedMutationImpl<*,*> &&
    other.connector == connector &&
    other.operationName == operationName &&
    other.dataDeserializer == dataDeserializer &&
    other.variablesSerializer == variablesSerializer

  override fun hashCode(): Int =
    java.util.Objects.hash(
      "SkilltokConnectorConnectorGeneratedMutationImpl",
      connector, operationName, dataDeserializer, variablesSerializer
    )

  override fun toString(): String =
    "SkilltokConnectorConnectorGeneratedMutationImpl(" +
    "operationName=$operationName, " +
    "dataDeserializer=$dataDeserializer, " +
    "variablesSerializer=$variablesSerializer, " +
    "connector=$connector)"
}



private class AddCommentMutationImpl(
  connector: SkilltokConnectorConnector
):
  AddCommentMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      AddCommentMutation.Data,
      AddCommentMutation.Variables
  >(
    connector,
    AddCommentMutation.Companion.operationName,
    AddCommentMutation.Companion.dataDeserializer,
    AddCommentMutation.Companion.variablesSerializer,
  )


private class CreateCourseMutationImpl(
  connector: SkilltokConnectorConnector
):
  CreateCourseMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      CreateCourseMutation.Data,
      CreateCourseMutation.Variables
  >(
    connector,
    CreateCourseMutation.Companion.operationName,
    CreateCourseMutation.Companion.dataDeserializer,
    CreateCourseMutation.Companion.variablesSerializer,
  )


private class CreateLessonMutationImpl(
  connector: SkilltokConnectorConnector
):
  CreateLessonMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      CreateLessonMutation.Data,
      CreateLessonMutation.Variables
  >(
    connector,
    CreateLessonMutation.Companion.operationName,
    CreateLessonMutation.Companion.dataDeserializer,
    CreateLessonMutation.Companion.variablesSerializer,
  )


private class CreateModuleMutationImpl(
  connector: SkilltokConnectorConnector
):
  CreateModuleMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      CreateModuleMutation.Data,
      CreateModuleMutation.Variables
  >(
    connector,
    CreateModuleMutation.Companion.operationName,
    CreateModuleMutation.Companion.dataDeserializer,
    CreateModuleMutation.Companion.variablesSerializer,
  )


private class CreateReelMutationImpl(
  connector: SkilltokConnectorConnector
):
  CreateReelMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      CreateReelMutation.Data,
      CreateReelMutation.Variables
  >(
    connector,
    CreateReelMutation.Companion.operationName,
    CreateReelMutation.Companion.dataDeserializer,
    CreateReelMutation.Companion.variablesSerializer,
  )


private class DeleteLikeMutationImpl(
  connector: SkilltokConnectorConnector
):
  DeleteLikeMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      DeleteLikeMutation.Data,
      DeleteLikeMutation.Variables
  >(
    connector,
    DeleteLikeMutation.Companion.operationName,
    DeleteLikeMutation.Companion.dataDeserializer,
    DeleteLikeMutation.Companion.variablesSerializer,
  )


private class DeleteSaveMutationImpl(
  connector: SkilltokConnectorConnector
):
  DeleteSaveMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      DeleteSaveMutation.Data,
      DeleteSaveMutation.Variables
  >(
    connector,
    DeleteSaveMutation.Companion.operationName,
    DeleteSaveMutation.Companion.dataDeserializer,
    DeleteSaveMutation.Companion.variablesSerializer,
  )


private class EnrollInCourseMutationImpl(
  connector: SkilltokConnectorConnector
):
  EnrollInCourseMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      EnrollInCourseMutation.Data,
      EnrollInCourseMutation.Variables
  >(
    connector,
    EnrollInCourseMutation.Companion.operationName,
    EnrollInCourseMutation.Companion.dataDeserializer,
    EnrollInCourseMutation.Companion.variablesSerializer,
  )


private class GetCommentsQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetCommentsQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetCommentsQuery.Data,
      GetCommentsQuery.Variables
  >(
    connector,
    GetCommentsQuery.Companion.operationName,
    GetCommentsQuery.Companion.dataDeserializer,
    GetCommentsQuery.Companion.variablesSerializer,
  )


private class GetEnrollmentsQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetEnrollmentsQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetEnrollmentsQuery.Data,
      Unit
  >(
    connector,
    GetEnrollmentsQuery.Companion.operationName,
    GetEnrollmentsQuery.Companion.dataDeserializer,
    GetEnrollmentsQuery.Companion.variablesSerializer,
  )


private class GetLessonsQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetLessonsQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetLessonsQuery.Data,
      GetLessonsQuery.Variables
  >(
    connector,
    GetLessonsQuery.Companion.operationName,
    GetLessonsQuery.Companion.dataDeserializer,
    GetLessonsQuery.Companion.variablesSerializer,
  )


private class GetModulesQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetModulesQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetModulesQuery.Data,
      GetModulesQuery.Variables
  >(
    connector,
    GetModulesQuery.Companion.operationName,
    GetModulesQuery.Companion.dataDeserializer,
    GetModulesQuery.Companion.variablesSerializer,
  )


private class GetUserLikesQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetUserLikesQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetUserLikesQuery.Data,
      Unit
  >(
    connector,
    GetUserLikesQuery.Companion.operationName,
    GetUserLikesQuery.Companion.dataDeserializer,
    GetUserLikesQuery.Companion.variablesSerializer,
  )


private class GetUserProfileQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetUserProfileQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetUserProfileQuery.Data,
      GetUserProfileQuery.Variables
  >(
    connector,
    GetUserProfileQuery.Companion.operationName,
    GetUserProfileQuery.Companion.dataDeserializer,
    GetUserProfileQuery.Companion.variablesSerializer,
  )


private class GetUserSavedQueryImpl(
  connector: SkilltokConnectorConnector
):
  GetUserSavedQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      GetUserSavedQuery.Data,
      Unit
  >(
    connector,
    GetUserSavedQuery.Companion.operationName,
    GetUserSavedQuery.Companion.dataDeserializer,
    GetUserSavedQuery.Companion.variablesSerializer,
  )


private class ListCoursesQueryImpl(
  connector: SkilltokConnectorConnector
):
  ListCoursesQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      ListCoursesQuery.Data,
      Unit
  >(
    connector,
    ListCoursesQuery.Companion.operationName,
    ListCoursesQuery.Companion.dataDeserializer,
    ListCoursesQuery.Companion.variablesSerializer,
  )


private class ListReelsQueryImpl(
  connector: SkilltokConnectorConnector
):
  ListReelsQuery,
  SkilltokConnectorConnectorGeneratedQueryImpl<
      ListReelsQuery.Data,
      Unit
  >(
    connector,
    ListReelsQuery.Companion.operationName,
    ListReelsQuery.Companion.dataDeserializer,
    ListReelsQuery.Companion.variablesSerializer,
  )


private class ToggleLikeMutationImpl(
  connector: SkilltokConnectorConnector
):
  ToggleLikeMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      ToggleLikeMutation.Data,
      ToggleLikeMutation.Variables
  >(
    connector,
    ToggleLikeMutation.Companion.operationName,
    ToggleLikeMutation.Companion.dataDeserializer,
    ToggleLikeMutation.Companion.variablesSerializer,
  )


private class ToggleSaveMutationImpl(
  connector: SkilltokConnectorConnector
):
  ToggleSaveMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      ToggleSaveMutation.Data,
      ToggleSaveMutation.Variables
  >(
    connector,
    ToggleSaveMutation.Companion.operationName,
    ToggleSaveMutation.Companion.dataDeserializer,
    ToggleSaveMutation.Companion.variablesSerializer,
  )


private class UpdateProgressMutationImpl(
  connector: SkilltokConnectorConnector
):
  UpdateProgressMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      UpdateProgressMutation.Data,
      UpdateProgressMutation.Variables
  >(
    connector,
    UpdateProgressMutation.Companion.operationName,
    UpdateProgressMutation.Companion.dataDeserializer,
    UpdateProgressMutation.Companion.variablesSerializer,
  )


private class UpsertUserMutationImpl(
  connector: SkilltokConnectorConnector
):
  UpsertUserMutation,
  SkilltokConnectorConnectorGeneratedMutationImpl<
      UpsertUserMutation.Data,
      UpsertUserMutation.Variables
  >(
    connector,
    UpsertUserMutation.Companion.operationName,
    UpsertUserMutation.Companion.dataDeserializer,
    UpsertUserMutation.Companion.variablesSerializer,
  )


