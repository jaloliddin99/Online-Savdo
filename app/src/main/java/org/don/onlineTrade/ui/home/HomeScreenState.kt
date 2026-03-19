package org.don.onlineTrade.ui.home

import com.google.android.gms.maps.model.LatLng
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.category.Category
import org.don.onlineTrade.data.remote.models.category.ParentCategories
import org.don.onlineTrade.data.remote.models.getProfile.User
import org.don.onlineTrade.data.remote.models.leak.ModelLeak
import org.don.onlineTrade.data.remote.models.nearPost.NearPostsData
import org.don.onlineTrade.data.remote.models.reverse.FeatureMember
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsData

data class HomeScreenState(
    val isLoading: Boolean = false,
    val categoryList: Category? = null,
    val parentCategoryList: ParentCategories? = null,
    val getLocation: Boolean = false,
    val error: String = ""
)

data class HomeScreenState2(
    val isLoading: Boolean = false,
    var getNearPost: List<NearPostsData>? = null,
    val error: String = ""
)

data class RegionsScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val showAlertDialog: Boolean = false,
)


data class AddProductScreenState(
    val isLoading: Boolean = false,
    val postNewProduct: ModelSuccess? = null,
    val error: String = "",
    val showSuccessDialog: Boolean = false,
    val categoryDetail: ModelLeak? = null,
    val showCameraOrGalleryDialog: Boolean = false,
)

data class MapScreenScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val featureMember: List<FeatureMember>? = null,
    val singleFutureMember: List<FeatureMember>? = null,
    val latLng: LatLng? = null,
)

data class PresentProductState(
    val isLoading: Boolean = false,
    val registerMain: PostDetailsData? = null,
    val delete: ModelSuccess? = null,
    val error: String = ""
)

data class MyProfileScreen(
    val isLoading: Boolean = false,
    val getProfile: ModelSuccess? = null,
    val error: String = ""
)


data class GetProfileState(
    val isLoading: Boolean = false,
    val getProfile: User? = null,
    val updateProfileImage: ModelSuccess? = null,
    val error: String = ""
)

data class UpdateProfileState(
    val isLoading: Boolean = false,
    val getProfile: ModelSuccess? = null,
    val error: String = ""
)


data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val main: ModelSuccess? = null,
    val error: String = "",
)

data class ResetNewPasswordState(
    val isLoading: Boolean = false,
    val main: ModelSuccess? = null,
    val error: String = "",
)
