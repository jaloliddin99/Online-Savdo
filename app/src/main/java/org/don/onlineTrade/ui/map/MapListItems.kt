package org.don.onlineTrade.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.region.RegionDistrict
import org.don.onlineTrade.data.remote.models.reverse.Component
import org.don.onlineTrade.data.remote.models.reverse.FeatureMember
import org.don.onlineTrade.data.remote.models.reverse.GeoObject
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun DisplayLocations(
    featureMember: List<FeatureMember>?,
    onBackClick: (MapScreenData?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp)

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(MaterialTheme.spacing.dimen16Dp)
                )
                .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
        ) {
            featureMember?.let {
                itemsIndexed(featureMember) { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val lat = item.GeoObject.Point.pos.split(" ")[0].toDouble()
                                val lon = item.GeoObject.Point.pos.split(" ")[1].toDouble()
                                onBackClick(
                                    MapScreenData(
                                        lat = lon,
                                        lon = lat,
                                        addressName = item.GeoObject.name,
                                        addressDescription = item.GeoObject.description
                                    )
                                )
                            },
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.GeoObject.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = robotoFontFamily
                        )
                        Text(
                            text = item.GeoObject.description,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = robotoFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (index != it.lastIndex)
                            Divider(modifier = Modifier.height(0.4.dp))
                    }
                }

            }

        }
    }
}


@Composable
fun ConstraintLayoutContent(
    singleLocation: GeoObject?,
    liftUpListener: Boolean,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        val (markerImage) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(markerImage) {
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            MarkerContent(singleLocation, liftUpListener)
        }
    }
}


@Composable
fun MarkerContent(singleLocation: GeoObject?, liftUpListener: Boolean) {
    val yOffset by animateDpAsState(if (liftUpListener) (-20).dp else 0.dp, label = "")
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .offset(y = yOffset),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MarkerMainBox(singleLocation)
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(2.dp)
                .background(Color.Black)
        )
    }
}


@Composable
fun MarkerMainBox(singleLocation: GeoObject?) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(MaterialTheme.spacing.dimen8Dp)
            )
            .padding(MaterialTheme.spacing.dimen4Dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(MaterialTheme.spacing.dimen8Dp)
                )
                .padding(4.dp)
        ) {
            val composition by
            rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.marker_location))
            LottieAnimation(
                modifier = Modifier.fillMaxSize(),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        singleLocation?.let {
            AnimatedVisibility(visible = true) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = it.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Text(
                        text = it.description,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

val getLocation: (GeoObject?, List<RegionDistrict>?) -> MapScreenData? = { obj, list ->
    if (obj == null || list == null)
        null
    else {
        val long = obj.Point.pos.split(" ")[0].toDouble()
        val lat = obj.Point.pos.split(" ")[1].toDouble()
        val addressName = obj.name
        val addressDescription = obj.description
        val regionList = list
        val districtList = list.flatMap { it.districts }

        val mapRegionList = obj.metaDataProperty.GeocoderMetaData.Address.Components
        var regionId = -1
        var districtId = -1
        mapRegionList.find { it.kind == "province" }?.let {
            regionList.find { region ->
                it.name.contains(region.name.trim().split(" ")[0].lowercase())
            }?.let {
                regionId = it.id
            }
        }
        mapRegionList.find { it.kind == "area" }?.let {
            districtList.find { region ->
                it.name.contains(region.name.trim().split(" ")[0].lowercase())
            }?.let {
                districtId = it.id
            }
        }

        MapScreenData(
            lat = lat,
            lon = long,
            addressName = addressName,
            addressDescription = addressDescription,
            regionId = regionId,
            districtId = districtId
        )
    }
}