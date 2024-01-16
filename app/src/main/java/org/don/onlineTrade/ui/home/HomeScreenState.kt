package org.don.onlineTrade.ui.home

import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.nearPost.NeaPostModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.ui.region.MyLocation
import org.don.onlineTrade.data.remote.models.category.Category
import org.don.onlineTrade.data.remote.models.leak.ModelLeak

data class HomeScreenState(
    val isLoading: Boolean = false,
    val categoryList: Category?= null,
    val getLocation: Boolean = false,
    val error: String = ""
)

data class HomeScreenState2(
    val isLoading: Boolean = false,
    var getNearPost: NeaPostModel?= null,
    val error: String = ""
)

data class RegionsScreenState(
    val isLoading: Boolean = false,
    val regions: List<Data>?= null,
    val districts: List<DataDistrict> ?=null,
    val error: String = "",
    val showAlertDialog: Boolean = false,
    val myLocation: MyLocation ?= null,
    val district: DataDistrict ?= null
)


data class AddProductScreenState(
    val isLoading: Boolean = false,
    val postNewProduct: PostModel ?= null,
    val error: String = "",
    val showSuccessDialog: Boolean= false,
    val categoryDetail: ModelLeak ?= null,
    val showCameraOrGalleryDialog: Boolean = false
)

data class PresentProductState(
    val isLoading: Boolean = false,
    val registerMain: PostDetailsModel?= null,
    val delete: ModelSuccess?= null,
    val error: String = ""
)



data class GetProfileState(
    val isLoading: Boolean = false,
    val getProfile: ModelGetProfile?= null,
    val updateProfileImage: ModelSuccess?= null,
    val error: String = ""
)

data class UpdateProfileState(
    val isLoading: Boolean = false,
    val getProfile: ModelSuccess?= null,
    val error: String = ""
)


data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val main: ModelSuccess?= null,
    val error: String = "",
)

data class ResetNewPasswordState(
    val isLoading: Boolean = false,
    val main: ModelSuccess?= null,
    val error: String = "",
)