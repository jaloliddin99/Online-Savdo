package org.don.onlineTrade.ui.home

import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val registerMain: List<CompactedCategoryItem>?= null,
    val error: String = ""
)


data class RegionsScreenState(
    val isLoading: Boolean = false,
    val regions: List<Data>?= null,
    val districts: List<DataDistrict> ?=null,
    val error: String = ""
)


data class AddProductScreenState(
    val isLoading: Boolean = false,
    val postNewProduct: PostModel ?= null,
    val error: String = ""
)

data class PresentProductState(
    val isLoading: Boolean = false,
    val registerMain: PostDetailsModel?= null,
    val error: String = ""
)

data class LikedProductsState(
    val isLoading: Boolean = false,
    val registerMain: LikedProductsModel?= null,
    val error: String = ""
)


data class GetProfileState(
    val isLoading: Boolean = false,
    val getProfile: ModelGetProfile?= null,
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