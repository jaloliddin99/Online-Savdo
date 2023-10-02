package org.don.onlineTrade.ui.home

import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem
import org.don.onlineTrade.data.remote.models.showProducts.ShowProductModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val registerMain: List<CompactedCategoryItem>?= null,
    val error: String = ""
)


data class RegionsScreenState(
    val isLoading: Boolean = false,
    val regions: List<RegionDistrictModelItem>?= null,
    val error: String = ""
)


data class AddProductScreenState(
    val isLoading: Boolean = false,
    val regions: List<ModelCurrencyListsItem>?= null,
    val postNewProduct: PostModel ?= null,
    val error: String = ""
)

data class PresentProductState(
    val isLoading: Boolean = false,
    val registerMain: ShowProductModel?= null,
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
