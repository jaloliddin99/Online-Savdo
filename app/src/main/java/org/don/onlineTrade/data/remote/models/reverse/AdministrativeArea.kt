package org.don.onlineTrade.data.remote.models.reverse

import org.don.onlineTrade.data.remote.models.reverse.Locality
import org.don.onlineTrade.data.remote.models.reverse.SubAdministrativeArea

data class AdministrativeArea(
    val AdministrativeAreaName: String,
    val Locality: Locality,
    val SubAdministrativeArea: SubAdministrativeArea
)