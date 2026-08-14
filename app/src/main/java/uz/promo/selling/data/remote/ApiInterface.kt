package uz.promo.selling.data.remote

import androidx.annotation.Keep
import okhttp3.MultipartBody
import uz.promo.selling.data.remote.models.ai.AiDraftEnvelope
import uz.promo.selling.data.remote.models.ai.AiSearchData
import uz.promo.selling.data.remote.models.ai.PriceSuggestionDTO
import okhttp3.RequestBody
import uz.promo.selling.data.remote.models.GenericModel
import uz.promo.selling.data.remote.models.LoginBody
import uz.promo.selling.data.remote.models.ModelSuccess
import uz.promo.selling.data.remote.models.RegistrationBody
import uz.promo.selling.data.remote.models.ResetPasswordBody
import uz.promo.selling.data.remote.models.VerificationRes
import uz.promo.selling.data.remote.models.category.Category
import uz.promo.selling.data.remote.models.category.ParentCategories
import uz.promo.selling.data.remote.models.chat.ChatMessage
import uz.promo.selling.data.remote.models.chat.Conversation
import uz.promo.selling.data.remote.models.chat.ConversationPage
import uz.promo.selling.data.remote.models.chat.MessagePage
import uz.promo.selling.data.remote.models.chat.ReportBody
import uz.promo.selling.data.remote.models.chat.SendMessageBody
import uz.promo.selling.data.remote.models.chat.StartConversationBody
import uz.promo.selling.data.remote.models.chat.UnreadCount
import uz.promo.selling.data.remote.models.getNotifications.NotificationData
import uz.promo.selling.data.remote.models.getProfile.SellerInfo
import uz.promo.selling.data.remote.models.getProfile.UpdatePasswordModel
import uz.promo.selling.data.remote.models.getProfile.UpdateProfileModel
import uz.promo.selling.data.remote.models.getProfile.User
import uz.promo.selling.data.remote.models.getPublicProducts.Data
import uz.promo.selling.data.remote.models.leak.ModelLeak
import uz.promo.selling.data.remote.models.nearPost.NearPostsData
import uz.promo.selling.data.remote.models.reverse.ModelAddressReverse
import uz.promo.selling.data.remote.models.searchSuggestion.SearchSuggestionData
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.utils.SharedPref
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

@Keep
interface ApiInterface {

    @POST("auth/register")
    suspend fun register(
        @Body registrationBody: RegistrationBody
    ): ModelSuccess

    @POST("auth/login")
    suspend fun login(
        @Body loginBody: LoginBody
    ): VerificationRes

    @GET("auth/verify")
    suspend fun verify(
        @Query("code") code: Int,
        @Query("email") email: String
    ): VerificationRes

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Query("email") email: String
    ): ModelSuccess

    @POST("auth/reset-new-password")
    suspend fun resetNewPassword(
        @Body body: ResetPasswordBody
    ): ModelSuccess

    @POST("auth/google")
    suspend fun googleAuth(
        @Body body: Map<String, String>
    ): VerificationRes

    @PUT("auth/complete-profile")
    suspend fun completeProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): ModelSuccess

    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Body body: Map<String, String>
    ): GenericModel<Map<String, String>>

    @GET("post")
    suspend fun getPublicProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
        @Query("category_id") categoryId: Int?,
        @Query("query") query: String?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("priceMin") fromPrice: Int? = null,
        @Query("priceMax") toPrice: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("radius") radius: Int? = null,
        // Optional: lets the backend mark which posts the caller already liked.
        @Header("Authorization") token: String? = null
    ): GenericModel<Data>

    // Public seller profile: only live posts of the given user.
    @GET("post/user-posts/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String = SharedPref.language,
    ): GenericModel<Data>


    @GET("notification/pager")
    suspend fun getAllNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
    ): NotificationData

    @GET("post/near")
    suspend fun getNearPosts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("radius") radius: Int? = null
    ): GenericModel<List<NearPostsData>>

    @GET("post/myPosts")
    suspend fun getMyPosts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
        @Query("status") status: Int? = null
    ): GenericModel<Data>

    @GET("categories")
    suspend fun getAllCategories(
        @Query("lang") language: String,
    ): Category

    @GET("categories/parents")
    suspend fun getAllParentCategories(
        @Query("lang") language: String,
    ): ParentCategories

    @GET("categories/{categoryId}")
    suspend fun getCategories(
        @Path("categoryId") categoryId: Int,
    ): ModelLeak

    @Multipart
    @POST("post")
    suspend fun newProduct(
        @Header("Authorization") token: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("category_id") categoryId: Long,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("addressName") addressName: String,
        @Query("addressDescription") addressDescription: String,
        @Query("userId") userId: Int,
        @Part files: List<MultipartBody.Part>,
        @Part("post_params") postParams: RequestBody
    ): ModelSuccess

    /**
     * Edits an existing post. Mirrors [newProduct], minus userId (the server takes
     * the owner from the token) and plus the post id. [files] may be empty — the
     * server keeps the post's current images when no new ones are sent.
     */
    @Multipart
    @PUT("post/{postId}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("category_id") categoryId: Long,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("addressName") addressName: String,
        @Query("addressDescription") addressDescription: String,
        @Query("keepImageIds") keepImageIds: List<Long>,
        @Part files: List<MultipartBody.Part>,
        @Part("post_params") postParams: RequestBody
    ): ModelSuccess

    // AI: analyze the photos and return a draft (title/description/category) plus
    // ready-to-submit post params for the create-post form.
    @Multipart
    @POST("ai/listing-draft")
    suspend fun aiListingDraft(
        @Header("Authorization") token: String,
        @Part files: List<MultipartBody.Part>,
        @Query("lang") lang: String
    ): AiDraftEnvelope

    // AI: SQL-percentile price suggestion for a category (no AI cost).
    @GET("ai/price-suggestion")
    suspend fun aiPriceSuggestion(
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("categoryId") categoryId: Long,
        @Query("currency") currency: String?,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<PriceSuggestionDTO>

    // AI: parse a natural-language search query into structured filters. Called
    // once (page=0,size=1) only to read 'parsed'; the 'results' field is ignored.
    @GET("ai/search")
    suspend fun aiSearch(
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("query") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<AiSearchData>


    @POST("post/prioritize")
    suspend fun prioritizePost(
        @Header("Authorization") token: String,
        @Query("postId") postId: Long,
        @Query("period") periodInHours: Int
    ): ModelSuccess

    @POST("support")
    suspend fun sendSupportMessage(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    // Interests: the category ids the user explicitly picked to personalize
    // AI search / recommendations on the SearchScreen.
    @GET("user/interests")
    suspend fun getInterests(
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<List<Long>>

    @PUT("user/interests")
    suspend fun updateInterests(
        @Header("Authorization") token: String = SharedPref.deviceToken,
        // @JvmSuppressWildcards is required: Kotlin's covariant List<out E> compiles the
        // value type to `? extends List<Long>`, and Retrofit rejects wildcards in @Body
        // at method-parse time — before the request is ever sent.
        @Body body: Map<String, @JvmSuppressWildcards List<Long>>,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<List<Long>>

    @GET("user/notification-prefs")
    suspend fun getNotificationPrefs(
        @Header("Authorization") token: String,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.inbox.NotificationPrefs>

    @POST("user/notification-prefs")
    suspend fun updateNotificationPrefs(
        @Header("Authorization") token: String,
        @Body body: Map<String, Boolean>,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    @GET("user-notifications")
    suspend fun getUserNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 20,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.inbox.UserNotificationPage>

    @GET("user-notifications/unread-count")
    suspend fun getUnreadNotificationCount(
        @Header("Authorization") token: String,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.inbox.UnreadCountRes>

    @POST("user-notifications/read-all")
    suspend fun markNotificationsRead(
        @Header("Authorization") token: String,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    @GET("payments/tariffs")
    suspend fun getBoostTariffs(
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<List<uz.promo.selling.data.remote.models.payments.BoostTariff>>

    @POST("payments/boost-order")
    suspend fun createBoostOrder(
        @Header("Authorization") token: String,
        @Body body: uz.promo.selling.data.remote.models.payments.BoostOrderBody,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.BoostOrderRes>

    @POST("payments/promo/validate")
    suspend fun validatePromo(
        @Body body: uz.promo.selling.data.remote.models.payments.PromoValidateBody,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.PromoValidateRes>

    @GET("payments/orders/{orderId}")
    suspend fun getPaymentOrderStatus(
        @Path("orderId") orderId: Long,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.OrderStatusRes>

    // ---------------- Premium ----------------

    @GET("payments/premium-plans")
    suspend fun getPremiumPlans(
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.PremiumPlansData>

    @POST("payments/premium-order")
    suspend fun createPremiumOrder(
        @Body body: uz.promo.selling.data.remote.models.payments.PremiumOrderBody,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.BoostOrderRes>

    @POST("payments/promote-with-credit")
    suspend fun promoteWithCredit(
        @Body body: Map<String, Long>,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    @GET("user/post-analytics")
    suspend fun getPostAnalytics(
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<uz.promo.selling.data.remote.models.payments.PostAnalyticsData>

    @GET("post/{postId}/interested")
    suspend fun getInterested(
        @Path("postId") postId: Long,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        @Query("lang") lang: String = SharedPref.language
    ): GenericModel<List<uz.promo.selling.data.remote.models.payments.InterestedUser>>

    @POST("post/{id}/mark-sold")
    suspend fun markPostSold(
        @Header("Authorization") token: String,
        @Path("id") postId: Long,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    @POST("post/{id}/activate")
    suspend fun activatePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Long,
        @Query("lang") lang: String = SharedPref.language
    ): ModelSuccess

    @GET("post/{id}")
    suspend fun showProductModel(
        @Path("id") id: Int,
        @Query("lang") language: String,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<PostDetailsData>

    @DELETE("post/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): ModelSuccess

    @POST("post/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("lang") language: String
    ): GenericModel<PostDetailsData>

    @GET("post/user/liked-posts")
    suspend fun getLikedProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): GenericModel<Data>


    @GET("user")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): GenericModel<User>

    @POST("user/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileModel
    ): ModelSuccess

    @POST("user/update-profile-image")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): ModelSuccess

    @POST("user/update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body body: UpdatePasswordModel
    ): ModelSuccess

    @GET("post/search/suggest")
    suspend fun getSearchSuggestions(
        @Query("query") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("lang") lang: String,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        ): GenericModel<SearchSuggestionData>

    @GET("post/search")
    suspend fun searchPosts(
        @Query("lang") lang: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("query") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("category_id") categoryIds: List<Long>?,
        @Query("status") status: Int = 1,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("priceMin") priceMin: Int? = null,
        @Query("priceMax") priceMax: Int? = null,
        @Query("sort") sort: String? = null,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        ): GenericModel<Data>

    @GET
    suspend fun getLocationList(
        @Url url: String
    ): ModelAddressReverse

    @POST("user/fcm-token")
    suspend fun sendFcmToken(
        @Body body: Map<String, String>,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

    // Public seller header (name/avatar/premium) — no auth required.
    @GET("user/public/{userId}")
    suspend fun getPublicUser(
        @Path("userId") userId: Int,
        @Query("lang") lang: String = SharedPref.language,
    ): GenericModel<SellerInfo>

    // ---------------- Chat ----------------

    @POST("chat/conversations")
    suspend fun startConversation(
        @Body body: StartConversationBody,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Conversation>

    @GET("chat/conversations")
    suspend fun getConversations(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<ConversationPage>

    @GET("chat/conversations/{id}")
    suspend fun getConversation(
        @Path("id") id: Long,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Conversation>

    @GET("chat/conversations/{id}/messages")
    suspend fun getChatMessages(
        @Path("id") id: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<MessagePage>

    @POST("chat/conversations/{id}/messages")
    suspend fun sendChatMessage(
        @Path("id") id: Long,
        @Body body: SendMessageBody,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<ChatMessage>

    @POST("chat/conversations/{id}/messages/{messageId}/edit")
    suspend fun editChatMessage(
        @Path("id") id: Long,
        @Path("messageId") messageId: Long,
        @Body body: SendMessageBody,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<ChatMessage>

    @POST("chat/conversations/{id}/messages/{messageId}/delete")
    suspend fun deleteChatMessage(
        @Path("id") id: Long,
        @Path("messageId") messageId: Long,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

    @GET("chat/unread-count")
    suspend fun getChatUnreadCount(
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<UnreadCount>

    @POST("chat/conversations/{id}/report")
    suspend fun reportConversation(
        @Path("id") id: Long,
        @Body body: ReportBody,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

    @POST("chat/conversations/{id}/block")
    suspend fun blockConversation(
        @Path("id") id: Long,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

    @POST("chat/conversations/{id}/unblock")
    suspend fun unblockConversation(
        @Path("id") id: Long,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

    @POST("chat/conversations/{id}/delete")
    suspend fun deleteConversation(
        @Path("id") id: Long,
        @Query("lang") lang: String = SharedPref.language,
        @Header("Authorization") token: String = SharedPref.deviceToken,
    ): GenericModel<Any>

}