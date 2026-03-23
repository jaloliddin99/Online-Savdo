package uz.don.onlineTrade.data.remote.models.reverse

import uz.don.onlineTrade.data.remote.models.reverse.Locality
import uz.don.onlineTrade.data.remote.models.reverse.SubAdministrativeArea

data class AdministrativeArea(
    val AdministrativeAreaName: String,
    val Locality: Locality,
    val SubAdministrativeArea: SubAdministrativeArea
)